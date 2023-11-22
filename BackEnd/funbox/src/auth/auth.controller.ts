import { Body, Controller, Get, Post } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UserAuthDto } from './dto/user-auth.dto';

@Controller('auth')
export class AuthController {
    constructor(private authService: AuthService){}

    @Post('/signup')
    async makeNullUserAndReturnWithToken(@Body('id_oauth') idOauth: string) {
        return await this.authService.createNullUser(idOauth);
    }
}
