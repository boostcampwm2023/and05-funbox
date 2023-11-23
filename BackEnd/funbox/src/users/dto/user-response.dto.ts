import { ApiProperty } from '@nestjs/swagger';
import { User } from '../user.entity';

export class UserResponseDto {
  @ApiProperty({ description: '이름' })
  name: string;

  @ApiProperty({
    example: 'https://picsum.photos/200',
    description: '프로필 사진 URL',
  })
  profileUrl: string;

  @ApiProperty({ description: '유저 메시지' })
  message: string;

  static of(user: User): UserResponseDto {
    return {
      name: user.username,
      profileUrl: user.profile_url,
      message: user.message,
    };
  }
}
