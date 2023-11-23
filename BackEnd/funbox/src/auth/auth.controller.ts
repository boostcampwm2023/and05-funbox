import { Body, Controller, Post } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
    constructor(private authService: AuthService){}

    @Post()
    async checkJwt(@Body('accessToken') accessToken: string) {
        return await this.authService.tokenValidation(accessToken);
    }

    @Post('/notoken')
    async getNaverUserAndFindUserDbIfNotMakeNullUser(@Body('naverAccessToken') naverAccessToken: string) {
        const naverUserId = await this.authService.getNaverUserId(naverAccessToken);
        
        try {
            return await this.authService.findIdOauth(naverUserId);
        } catch {
            return await this.authService.createNullUser(naverUserId);
        }
    }
}
