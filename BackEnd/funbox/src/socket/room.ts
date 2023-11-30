import { Socket } from 'socket.io';

export class Room {
  owner: Socket;
  opponent: Socket;
  quizzes: string[];
  round: number = 0;

  constructor(owner: Socket, opponent: Socket) {
    this.owner = owner;
    this.owner.data.score = 0;
    this.opponent = opponent;
    this.opponent.data.score = 0;
    this.quizzes = getQuizzes(2);
  }

  nextQuiz() {
    const quiz = this.quizzes[this.round++];
    const target = this.round % 2 == 0 ? this.owner : this.opponent;
    const data = JSON.stringify({ quiz, target: target.data.userId });
    this.owner.emit('quiz', data);
    this.opponent.emit('quiz', data);
  }

  deliverAnswer(answer: string) {
    const verifier = this.round % 2 == 0 ? this.opponent : this.owner;
    verifier.emit('quizAnswer', JSON.stringify({ answer }));
  }

  verifyAnswer(isCorrect: boolean) {
    const target = this.round % 2 == 0 ? this.owner : this.opponent;
    if (isCorrect) {
      target.data.score += 1;
    }
  }

  showScore() {
    const data = JSON.stringify([
      {
        id: this.owner.data.userId,
        username: this.owner.data.username,
        score: this.owner.data.score,
      },
      {
        id: this.opponent.data.userId,
        username: this.opponent.data.username,
        score: this.opponent.data.score,
      },
    ]);
    this.owner.emit('score', data);
    this.opponent.emit('score', data);
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
