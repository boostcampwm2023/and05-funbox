import {
  ForbiddenException,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import { Socket } from 'socket.io';
import { GameRoomDto } from './dto/game-room.dto';

@Injectable()
export class GameService {
  private gameRoomList: Map<string, GameRoomDto> = new Map();
  private logger: Logger = new Logger('GameService');

  getGameRoom(roomId: string): GameRoomDto {
    const gameRoom = this.gameRoomList.get(roomId);
    if (!gameRoom) {
      throw new NotFoundException('Game room not found');
    }
    return gameRoom;
  }

  createGameRoom(owner: Socket, opponent: Socket, roomId: string) {
    this.gameRoomList.set(roomId, {
      id: roomId,
      players: [opponent, owner],
      quizzes: null,
      round: null,
    });
    owner.data.roomId = roomId;
    opponent.data.roomId = roomId;
    owner.join(roomId);
    opponent.join(roomId);
  }

  deleteGameRoom(roomId: string) {
    const gameRoom = this.getGameRoom(roomId);

    gameRoom.players.forEach((player: Socket) => {
      delete player.data.roomId;
      delete player.data.score;
      player.leave(roomId);
    });

    this.gameRoomList.delete(roomId);
  }

  startGame(roomId: string, size: number) {
    const gameRoom = this.getGameRoom(roomId);

    gameRoom.players.forEach((player: Socket) => {
      player.data.score = 0;
    });
    gameRoom.quizzes = getQuizzes(size);
    gameRoom.round = -1;

    this.nextQuiz(roomId);
  }

  checkGamePlaying(gameRoom: GameRoomDto) {
    if (!gameRoom.round) {
      throw new ForbiddenException("Game isn't playing");
    }
  }

  nextQuiz(roomId: string) {
    const gameRoom = this.getGameRoom(roomId);
    this.checkGamePlaying(gameRoom);

    const quiz = gameRoom.quizzes[++gameRoom.round];
    const target = this.getTarget(gameRoom).data.userId;
    const data = JSON.stringify({ roomId, quiz, target });

    this.all(roomId, 'quiz', data);
  }

  getTarget(gameRoom: GameRoomDto): Socket {
    const targetIndex = gameRoom.round % gameRoom.players.length;
    return gameRoom.players[targetIndex];
  }

  all(roomId: string, event: string, data: string) {
    const gameRoom = this.getGameRoom(roomId);
    this.logger.log(`room ${roomId} emit ${event}: ${data}`);
    gameRoom.players.forEach((player: Socket) => {
      player.emit(event, data);
    });
  }

  verifyAnswer(roomId: string, isCorrect: boolean) {
    const gameRoom = this.getGameRoom(roomId);
    this.checkGamePlaying(gameRoom);

    this.getTarget(gameRoom).data.score += isCorrect ? 1 : 0;

    if (gameRoom.round < gameRoom.quizzes.length - 1) {
      this.nextQuiz(roomId);
    } else {
      this.showScore(roomId);
      this.deleteGameRoom(roomId);
    }
  }

  showScore(roomId: string) {
    const gameRoom = this.getGameRoom(roomId);
    this.checkGamePlaying(gameRoom);

    const data = JSON.stringify(
      gameRoom.players.map((player: Socket) => {
        const { userId, username, score } = player.data;
        return { id: userId, username, score };
      }),
    );
    this.all(roomId, 'score', data);
  }

  lostConnection(roomId: string) {
    this.all(roomId, 'lostConnection', null);
  }
}

function getQuizzes(size: number): string[] {
  const shuffled = dummy.slice(0);
  let i = dummy.length;
  while (i--) {
    const index = Math.floor((i + 1) * Math.random());
    const temp = shuffled[index];
    shuffled[index] = shuffled[i];
    shuffled[i] = temp;
  }
  return shuffled.slice(0, size);
}

const dummy = [
  '저의 MBTI는 뭘까요?',
  '저의 출생지는 어디일까요?',
  '저의 현재 거주하는 곳은 어디일까요?',
  '저의 생년월일은 언제일까요?',
  '저는 형제자매가 있을까요?',
  '제가 최근에 본 영화는?',
  '제가 오늘 아침 먹은 것은 뭘까요?',
  '저의 휴대전화 기종은?',
  '제가 가장 좋아하는 색깔은?',
  '저의 키는 얼마나 될까요?',
  '저의 몸무게는 얼마나 될까요?',
  '제가 선호하는 계절은 뭘까요?',
  '저의 직업은 무엇일까요?',
  '제가 최근에 읽은 책은?',
  '제가 주로 듣는 음악 장르는?',
  '제가 최근에 갔던 여행지는?',
  '저의 특기나 취미는 뭘까요?',
  '제가 좋아하는 스포츠는?',
  '제가 자주 하는 활동은?',
  '제가 가장 좋아하는 음식은?',
  '제가 가장 좋아하는 동물은?',
  '제가 즐겨보는 TV 프로그램은?',
  '제가 가장 좋아하는 과일은?',
  '제가 주로 하는 운동은?',
  '제가 주말에 주로 하는 활동은?',
  '제가 즐겨보는 영화 장르는?',
  '제가 주로 사용하는 앱은?',
  '제가 좋아하는 가수나 밴드는?',
  '제가 좋아하는 브랜드는?',
  '제가 주로 마시는 음료는?',
  '제가 주로 타는 교통수단은?',
  '제가 현재 사용 중인 휴대폰 배경화면은?',
];
