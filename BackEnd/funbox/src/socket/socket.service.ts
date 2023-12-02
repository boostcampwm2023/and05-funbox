import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { Socket } from 'socket.io';
import { AuthService } from 'src/auth/auth.service';
import { UsersService } from 'src/users/users.service';
import { SocketUserDto } from './dto/socket-user.dto';
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
      client.emit('location', JSON.stringify(await this.getNearUsers()));

      client.on('disconnect', () => {
        this.userIdToClient.delete(id);
        this.logger.log(`Disconnected: ${info}`);
        const roomId = client.data.roomId;
        if (roomId) {
          const room = this.rooms.get(roomId);
          room.lostConnection();
          room.quit();
          this.rooms.delete(roomId);
        }
      });
    } catch (error) {
      client.emit('error', error.response);
      client.disconnect();
    }
  }

  async getNearUsers(): Promise<SocketUserDto[]> {
    return Promise.all(
      Array.from(this.userIdToClient.values())
        .filter((client) => client.data.locX && client.data.locY) // TODO: 주변 위치로 필터링
        .map(async (client) => SocketUserDto.of(client)),
    );
  }

  gameApply(client: Socket, opponentId: number) {
    const opponentClient = this.userIdToClient.get(opponentId);
    if (!opponentClient) {
      const data = JSON.stringify({ answer: GameApplyAnswer.OFFLINE });
      client.emit('gameApplyAnswer', data);
    } else {
      const userId = client.data.userId;
      const roomId = Math.random().toString(36).substring(2, 12);
      opponentClient.emit('gameApply', JSON.stringify({ userId, roomId }));
      this.rooms.set(roomId, new Room(roomId, client, opponentClient));
    }
  }

  gameApplyAnswer(roomId: string, answer: GameApplyAnswer) {
    const room = this.rooms.get(roomId);
    if (!room) {
      throw new NotFoundException();
    }
    const ownerClient = room.owner;
    ownerClient.emit('gameApplyAnswer', JSON.stringify({ answer }));

    if (answer === GameApplyAnswer.REJECT) {
      this.rooms.delete(roomId);
    } else {
      this.rooms.get(roomId).nextQuiz();
    }
  }

  quizAnswer(roomId: string, answer: string) {
    const room = this.rooms.get(roomId);
    room.deliverAnswer(answer);
  }

  verifyAnswer(roomId: string, isCorrect: boolean) {
    const room = this.rooms.get(roomId);
    room.verifyAnswer(isCorrect);
    if (room.round < room.quizzes.length) {
      room.nextQuiz();
    } else {
      room.showScore();
      room.quit();
      this.rooms.delete(roomId);
    }
  }
}
