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
  Logger,
  ParseBoolPipe,
  ParseIntPipe,
  UseFilters,
} from '@nestjs/common';
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
  private logger: Logger = new Logger('SocketGateway');

  constructor(private socketService: SocketService) {}

  handleConnection(client: Socket): void {
    this.logger.log(`handleConnection: ${client.id}`);
    this.socketService.handleConnection(client);
  }

  @SubscribeMessage('gameApply')
  gameApply(
    @ConnectedSocket() client: Socket,
    @MessageBody('opponentId', ParseIntPipe) opponentId: number,
  ): void {
    const log = JSON.stringify({ opponentId });
    this.logger.log(`gameApply from ${client.data.userId}: ${log}`);
    this.socketService.gameApply(client, opponentId);
  }

  @SubscribeMessage('gameApplyAnswer')
  gameApplyAnswer(
    @ConnectedSocket() client: Socket,
    @MessageBody('answer', AnswerValidationPipe) answer: GameApplyAnswer,
  ): void {
    const log = JSON.stringify({ answer });
    this.logger.log(`gameApplyAnswer from ${client.data.userId}: ${log}`);
    this.socketService.gameApplyAnswer(client, answer);
  }

  @SubscribeMessage('quizAnswer')
  quizAnswer(
    @ConnectedSocket() client: Socket,
    @MessageBody('answer') answer: string,
  ): void {
    const log = JSON.stringify({ answer });
    this.logger.log(`quizAnswer from ${client.data.userId}: ${log}`);
    this.socketService.quizAnswer(client, answer);
  }

  @SubscribeMessage('verifyAnswer')
  verifyAnswer(
    @ConnectedSocket() client: Socket,
    @MessageBody('isCorrect', ParseBoolPipe) isCorrect: boolean,
  ): void {
    const log = JSON.stringify({ isCorrect });
    this.logger.log(`verifyAnswer from ${client.data.userId}: ${log}`);
    this.socketService.verifyAnswer(client, isCorrect);
  }

  @SubscribeMessage('quitGame')
  quitGame(@ConnectedSocket() client: Socket): void {
    this.logger.log(`quitGame from ${client.data.userId}`);
    this.socketService.quitGame(client);
  }

  @SubscribeMessage('directMessage')
  directMessage(
    @ConnectedSocket() client: Socket,
    @MessageBody('userId', ParseIntPipe) userId: number,
    @MessageBody('message') message: string,
  ): void {
    const log = JSON.stringify({ userId, message });
    this.logger.log(`directMessage from ${client.data.userId}: ${log}`);
    this.socketService.directMessage(client, userId, message);
  }
}
