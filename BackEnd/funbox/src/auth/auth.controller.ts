import { Body, Controller, Get, Post, UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
    constructor(private authService: AuthService){}

    @Post()
    async checkJwt(@Body('accessToken') accessToken: string) {
        return await this.authService.tokenValidation(accessToken);
    }

    @Post('/notoken')
    async getNaverUser(@Body('naverAccessToken') naverAccessToken: string) {
        const isNaverUser = await this.authService.getNaverUser(naverAccessToken);
        if (!isNaverUser) {
            throw new UnauthorizedException("invalid naver user");
        }
        try {
            return await this.authService.findIdOauth(naverAccessToken);
        } catch {
            return await this.authService.createNullUser(naverAccessToken);
        }

    }
    
    @Post('/signup')
    async makeNullUserAndReturnWithToken(@Body('id_oauth') idOauth: string) {
        return await this.authService.createNullUser(idOauth);
    }

    @Post('/login')
    async login(@Body('id_oauth') idOauth: string) {
        return await this.authService.findIdOauth(idOauth);
    }
}
