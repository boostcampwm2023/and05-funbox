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
import { UserLocationDto } from 'src/users/dto/user-location.dto';

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

  @SubscribeMessage('updateLocation')
  updateLocation(client: Socket, userLocationDto: UserLocationDto): void {
    const id = client.data.userId;
    const { locX, locY } = userLocationDto;
    client.data.location = { locX, locY };
    this.server.emit('location', JSON.stringify({ id, locX, locY }));
  }

  @SubscribeMessage('applyQuizGame')
  applyQuizGame(
    @ConnectedSocket() client: Socket,
    @MessageBody(ParseIntPipe) opponentId: number,
  ): void {
    this.socketService.makeGameRoom(client, opponentId);
  }
}
