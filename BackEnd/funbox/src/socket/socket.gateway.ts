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
import {
  ParseBoolPipe,
  ParseIntPipe,
  UseFilters,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { UpdateLocationDto } from './dto/update-location.dto';
import { SocketUserDto } from './dto/socket-user.dto';
import { WsExceptionFilter } from './filters/ws-exception.filter';
import { GameApplyAnswer } from './enum/game-apply-answer.enum';
import { AnswerValidationPipe } from './pipes/answer-validation.pipe';

@WebSocketGateway({
  namespace: 'socket',
  cors: { origin: '*' },
})
@UseFilters(WsExceptionFilter)
export class SocketGateway implements OnGatewayConnection {
  @WebSocketServer()
  private server: Server;

  constructor(private socketService: SocketService) {}

  handleConnection(client: Socket): void {
    this.socketService.handleConnection(client);
  }

  @SubscribeMessage('updateLocation')
  @UsePipes(ValidationPipe)
  updateLocation(client: Socket, updateLocationDto: UpdateLocationDto): void {
    const { locX, locY } = updateLocationDto;
    client.data.locX = locX;
    client.data.locY = locY;

    this.server.emit('location', JSON.stringify(SocketUserDto.of(client)));
  }

  @SubscribeMessage('gameApply')
  gameApply(
    @ConnectedSocket() client: Socket,
    @MessageBody('opponentId', ParseIntPipe) opponentId: number,
  ): void {
    this.socketService.gameApply(client, opponentId);
  }

  @SubscribeMessage('gameApplyAnswer')
  gameApplyAnswer(
    @MessageBody('roomId') roomId: string,
    @MessageBody('answer', AnswerValidationPipe) answer: GameApplyAnswer,
  ): void {
    this.socketService.gameApplyAnswer(roomId, answer);
  }

  @SubscribeMessage('quizAnswer')
  quizAnswer(
    @MessageBody('roomId') roomId: string,
    @MessageBody('answer') answer: string,
  ): void {
    this.socketService.quizAnswer(roomId, answer);
  }

  @SubscribeMessage('verifyAnswer')
  verifyAnswer(
    @MessageBody('roomId') roomId: string,
    @MessageBody('isCorrect', ParseBoolPipe) isCorrect: boolean,
  ): void {
    this.socketService.verifyAnswer(roomId, isCorrect);
  }
}
