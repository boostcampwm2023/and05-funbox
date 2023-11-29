import { ApiProperty } from '@nestjs/swagger';
import { User } from 'src/users/user.entity';

export class UserAuthDto {
  @ApiProperty({ description: 'id' })
  id: number;

  @ApiProperty({ description: '유저 이름' })
  username: string;

  static of(user: User): UserAuthDto {
    return { id: user.id, username: user.username };
  }
}
