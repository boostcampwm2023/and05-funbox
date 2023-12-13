import { ApiProperty } from '@nestjs/swagger';

export class AuthResponseDto {
  @ApiProperty({
    description: 'jwt-accesstoken',
    example: 'jwt 형식의 string',
  })
  accessToken: string;

  static of(jwtString: string): AuthResponseDto {
    return { accessToken: jwtString };
  }
}
