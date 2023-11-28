import { Injectable, Logger } from '@nestjs/common';
import { Socket } from 'socket.io';
import { AuthService } from 'src/auth/auth.service';

@Injectable()
export class SocketService {
  private userIdToClient: Map<number, Socket> = new Map();
  private logger: Logger = new Logger('SocketService');

  constructor(private authService: AuthService) {}

  async handleConnection(client: Socket) {
    try {
      const { id, username } = await this.authService.tokenValidation(
        client.handshake.headers.authorization.replace(/^Bearer /, ''),
      );
      client.data.userId = id;
      client.data.username = username;

      const info = JSON.stringify({ id, username, clientId: client.id });
      this.userIdToClient.set(id, client);
      this.logger.log(`Connected: ${info}`);
      client.emit('message', `Connected: ${info}`);

      client.on('disconnect', () => {
        this.userIdToClient.delete(id);
        this.logger.log(`Disconnected: ${info}`);
      });
    } catch (error) {
      client.emit('error', error.message);
      client.disconnect();
    }
  }

  makeGameRoom(client: Socket, opponentId: number) {
    const opponentClient = this.userIdToClient.get(opponentId);
    if (!opponentClient) {
      client.emit('error', '접속 중인 유저가 아닙니다.');
      return;
    }
    opponentClient.emit('message', `[게임 신청] 상대: ${client.data.username}`);
    const randomRoomId = Math.random().toString(36).substring(2, 12);
    client.join(randomRoomId);
    opponentClient.join(randomRoomId);
  }
}
