import { ApiProperty } from '@nestjs/swagger';
import { User } from '../user.entity';

export class NearUsersDto {
  @ApiProperty({ description: 'id' })
  id: number;

  @ApiProperty({ description: '이름' })
  username: string;

  @ApiProperty({ description: '유저 위치 X좌표' })
  locX: number;

  @ApiProperty({ description: '유저 위치 Y좌표' })
  locY: number;

  @ApiProperty({ description: '1시간 내에 메시지가 변경된 경우 Ture' })
  isMsgInAnHour: boolean;

  static of(user: User): NearUsersDto {
    const { id, username, locX, locY } = user;
    let isMsgInAnHour: boolean;
    if (user.messaged_at === null) {
      isMsgInAnHour = false;
    } else {
      isMsgInAnHour = user.messaged_at.getTime() > Date.now() - 3600;
    }
    return { id, username, locX, locY, isMsgInAnHour };
  }
}
