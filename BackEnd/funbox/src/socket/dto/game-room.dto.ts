import { Socket } from 'socket.io';

export class GameRoomDto {
  id: string;
  players: Socket[];
  quizzes: string[];
  round: number;
}
