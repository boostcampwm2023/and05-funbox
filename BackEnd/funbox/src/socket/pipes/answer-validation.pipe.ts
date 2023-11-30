import { BadRequestException, PipeTransform } from '@nestjs/common';
import { GameApplyAnswer } from '../enum/game-apply-answer.enum';

export class AnswerValidationPipe implements PipeTransform {
  readonly answerOptions = [GameApplyAnswer.ACCEPT, GameApplyAnswer.REJECT];

  transform(value: any) {
    if (!value) {
      throw new BadRequestException(`answer is empty`);
    }
    value = value.toUpperCase();
    if (!this.isAnswerValid(value)) {
      throw new BadRequestException(`${value} isn't (ACCEPT or REJECT)`);
    }
    return value;
  }

  private isAnswerValid(answer: any) {
    const index = this.answerOptions.indexOf(answer);
    return index !== -1;
  }
}
