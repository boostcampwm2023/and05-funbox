import { BadRequestException, Injectable, Logger } from '@nestjs/common';
import { Socket } from 'socket.io';
import { AuthService } from 'src/auth/auth.service';
import { UsersService } from 'src/users/users.service';
import { GameApplyAnswer } from './enum/game-apply-answer.enum';
import { GameService } from './game.service';

@Injectable()
export class SocketService {
  private userIdToClient: Map<number, Socket> = new Map();
  private logger: Logger = new Logger('SocketService');

  constructor(
    private authService: AuthService,
    private userService: UsersService,
    private gameService: GameService,
  ) {}

  async handleConnection(client: Socket) {
    try {
      const { id } = await this.authService.tokenValidation(
        client.handshake.headers.authorization.replace(/^Bearer /, ''),
      );
      const { username } = await this.userService.getUserById(id);
      client.data.userId = id;
      client.data.username = username;

      const info = JSON.stringify({ id, username, clientId: client.id });
      this.userIdToClient.set(id, client);
      this.logger.log(`Connected: ${info}`);
      client.emit('connected', info);

      client.on('disconnect', () => {
        this.userIdToClient.delete(id);
        this.logger.log(`Disconnected: ${info}`);
        const roomId = client.data.roomId;
        if (roomId) {
          this.gameService.lostConnection(roomId);
          this.gameService.deleteGameRoom(roomId);
        }
      });
    } catch (error) {
      const data = JSON.stringify(error.response);
      this.logger.log(`error: ${data}`);
      client.emit('error', data);
      client.disconnect();
    }
  }

  gameApply(client: Socket, opponentId: number) {
    const opponentClient = this.userIdToClient.get(opponentId);
    if (!opponentClient || opponentClient.data.roomId) {
      const answer = !opponentClient
        ? GameApplyAnswer.OFFLINE
        : GameApplyAnswer.PLAYING;
      const data = JSON.stringify({ answer });
      this.logger.log(`gameApplyAnswer to ${client.data.userId}: ${data}`);
      client.emit('gameApplyAnswer', data);
    } else {
      const userId = client.data.userId;
      const roomId = Math.random().toString(36).substring(2, 12);
      const data = JSON.stringify({ userId, roomId });
      this.logger.log(`gameApply to ${opponentClient.data.userId}: ${data}`);
      opponentClient.emit('gameApply', data);
      this.gameService.createGameRoom(client, opponentClient, roomId);
    }
  }

  gameApplyAnswer(client: Socket, answer: GameApplyAnswer) {
    const roomId = this.getRoomId(client);

    const data = JSON.stringify({ answer });
    this.logger.log(`gameApplyAnswer to room(${roomId}): ${data}`);
    client.to(roomId).emit('gameApplyAnswer', data);

    if (answer === GameApplyAnswer.REJECT) {
      this.gameService.deleteGameRoom(roomId);
    } else {
      this.gameService.startGame(roomId, 4);
    }
  }

  quizAnswer(client: Socket, answer: string) {
    const roomId = this.getRoomId(client);

    const data = JSON.stringify({ roomId, answer });
    this.logger.log(`quizAnswer to room(${roomId}): ${data}`);
    client.to(roomId).emit('quizAnswer', data);
  }

  verifyAnswer(client: Socket, isCorrect: boolean) {
    const roomId = this.getRoomId(client);

    this.gameService.verifyAnswer(roomId, isCorrect);
  }

  quitGame(client: Socket) {
    const roomId = this.getRoomId(client);

    const data = JSON.stringify({ userId: client.data.userId });
    this.logger.log(`quitGame to room(${roomId}): ${data}`);
    client.to(roomId).emit('quitGame', data);

    this.gameService.deleteGameRoom(roomId);
  }

  getRoomId(client: Socket): string {
    const roomId = client.data.roomId;
    if (!roomId) {
      throw new BadRequestException("Client isn't in game");
    }
    return roomId;
  }

  directMessage(client: Socket, userId: number, message: string) {
    const receiver = this.userIdToClient.get(userId);
    if (!receiver) return;

    const data = JSON.stringify({ userId: client.data.userId, message });
    this.logger.log(`directMessage to ${receiver.data.userId}: ${data}`);
    receiver.emit('directMessage', data);
  }
}
