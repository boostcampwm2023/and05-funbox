import { Body, Controller, Post } from '@nestjs/common';
import { AuthService } from './auth.service';
import { ApiBody, ApiOkResponse, ApiTags } from '@nestjs/swagger';
import { AuthRequstDto } from './dto/auth-request.dto';
import { AuthResponseDto } from './dto/auth-response.dto';

@Controller('auth')
@ApiTags('1. 인증 API')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('/navertoken')
  @ApiBody({ type: AuthRequstDto })
  @ApiOkResponse({ type: AuthResponseDto })
  async getNaverUserAndFindUserDbIfNullMakeNullUser(
    @Body() naverAccessDto: AuthRequstDto,
  ): Promise<AuthResponseDto> {
    const naverUserId = await this.authService.getNaverUserId(
      naverAccessDto.naverAccessToken,
    );

    try {
      return await this.authService.findIdOauth(naverUserId);
    } catch {
      return await this.authService.createNullUser(naverUserId);
    }
  }

  @Post('/notoken/test')
  @ApiBody({
    schema: {
      type: '{naverFakeId: string}',
      example: '{"naverFakeId": "아무string"}',
    },
  })
  @ApiOkResponse({ type: AuthResponseDto })
  async getFakeNaverUserAndFindUserDbIfNotMakeNullUser(
    @Body('naveFakeId') naverFakeId: string,
  ) {
    const naverUserId = naverFakeId;

    try {
      return await this.authService.findIdOauth(naverUserId);
    } catch {
      return await this.authService.createNullUser(naverUserId);
    }
  }
}
