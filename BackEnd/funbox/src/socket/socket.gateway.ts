import {
  ConnectedSocket,
  MessageBody,
  OnGatewayConnection,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { SocketService } from './socket.service';
import { ParseIntPipe } from '@nestjs/common';

@WebSocketGateway({
  namespace: 'socket',
  cors: { origin: '*' },
})
export class SocketGateway implements OnGatewayConnection {
  @WebSocketServer()
  private server: Server;

  constructor(private socketService: SocketService) {}

  handleConnection(client: Socket): void {
    this.socketService.handleConnection(client);
  }

  @SubscribeMessage('applyQuizGame')
  applyQuizGame(
    @ConnectedSocket() client: Socket,
    @MessageBody(ParseIntPipe) opponentId: number,
  ): void {
    this.socketService.makeGameRoom(client, opponentId);
  }
}
