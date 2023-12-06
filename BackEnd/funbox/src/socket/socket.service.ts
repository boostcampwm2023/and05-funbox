import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { Socket } from 'socket.io';
import { AuthService } from 'src/auth/auth.service';
import { UsersService } from 'src/users/users.service';
import { GameApplyAnswer } from './enum/game-apply-answer.enum';
import { Room } from './room';

@Injectable()
export class SocketService {
  private userIdToClient: Map<number, Socket> = new Map();
  private rooms: Map<string, Room> = new Map();
  private logger: Logger = new Logger('SocketService');

  constructor(
    private authService: AuthService,
    private userService: UsersService,
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
        const room = this.rooms.get(roomId);
        if (room) {
          room.lostConnection();
          this.logger.log(`room ${roomId}: lostConnection`);
          room.quit();
          this.logger.log(`room ${roomId}: quit`);
          this.rooms.delete(roomId);
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
    if (!opponentClient) {
      const data = JSON.stringify({ answer: GameApplyAnswer.OFFLINE });
      this.logger.log(`gameApplyAnswer to ${client.data.userId}: ${data}`);
      client.emit('gameApplyAnswer', data);
    } else {
      const userId = client.data.userId;
      const roomId = Math.random().toString(36).substring(2, 12);
      const data = JSON.stringify({ userId, roomId });
      this.logger.log(`gameApply to ${opponentClient.data.userId}: ${data}`);
      opponentClient.emit('gameApply', data);
      this.rooms.set(roomId, new Room(roomId, client, opponentClient));
    }
  }

  gameApplyAnswer(roomId: string, answer: GameApplyAnswer) {
    const room = this.rooms.get(roomId);
    if (!room) {
      throw new NotFoundException();
    }
    const ownerClient = room.owner;
    const data = JSON.stringify({ answer });
    this.logger.log(`gameApplyAnswer to ${ownerClient.data.userId}: ${data}`);
    ownerClient.emit('gameApplyAnswer', data);

    if (answer === GameApplyAnswer.REJECT) {
      room.quit();
      this.logger.log(`room ${roomId}: quit`);
      this.rooms.delete(roomId);
    } else {
      this.rooms.get(roomId).nextQuiz();
      this.logger.log(`room ${roomId}: nextQuiz`);
    }
  }

  quizAnswer(roomId: string, answer: string) {
    const room = this.rooms.get(roomId);
    room.deliverAnswer(answer);
    this.logger.log(`room ${roomId}: deliverAnswer`);
  }

  verifyAnswer(roomId: string, isCorrect: boolean) {
    const room = this.rooms.get(roomId);
    room.verifyAnswer(isCorrect);
    this.logger.log(`room ${roomId}: verifyAnswer`);
    if (room.round < room.quizzes.length) {
      room.nextQuiz();
      this.logger.log(`room ${roomId}: nextQuiz`);
    } else {
      room.showScore();
      this.logger.log(`room ${roomId}: showScore`);
      room.quit();
      this.logger.log(`room ${roomId}: quit`);
      this.rooms.delete(roomId);
    }
  }

  quitGame(client: Socket, roomId: string) {
    const room = this.rooms.get(roomId);
    room.quitGame(client);
    this.logger.log(`room ${roomId}: quitGame`);
    room.quit();
    this.logger.log(`room ${roomId}: quit`);
    this.rooms.delete(roomId);
  }
}
