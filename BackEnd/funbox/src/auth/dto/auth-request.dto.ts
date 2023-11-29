import { ApiProperty } from '@nestjs/swagger';

export class AuthRequstDto {
  @ApiProperty({
    description: 'NAVER AccessToken',
    example: 'NAVER Access Token',
  })
  naverAccessToken: string;
}
