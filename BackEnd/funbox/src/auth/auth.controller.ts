import { Body, Controller, Get, Post } from '@nestjs/common';
import { AuthService } from './auth.service';

@Controller('auth')
export class AuthController {
    constructor(private authService: AuthService){}

    @Post()
    async checkJwt(@Body('accessToken') accessToken: string) {
        return await this.authService.tokenValidation(accessToken);
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
