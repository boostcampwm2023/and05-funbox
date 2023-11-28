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
}
