import { ApiProperty } from '@nestjs/swagger';

export class UserRequestDto {
  @ApiProperty({
    description: '이름',
    example: '15자 이내의 string',
  })
  username: string;

  @ApiProperty({
    example: 'askjasl123123kfja1232lsdkfj.png',
    description: '프로필 사진 URL',
  })
  profileUrl: string;

  @ApiProperty({
    description: '유저 메시지',
    example: '31자 이내의 string',
  })
  message: string;
}
