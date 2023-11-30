import { ApiProperty } from '@nestjs/swagger';
import { User } from 'src/users/user.entity';

export class UserAuthDto {
  @ApiProperty({ description: 'id' })
  id: number;

  static of(user: User): UserAuthDto {
    return { id: user.id };
  }
}
