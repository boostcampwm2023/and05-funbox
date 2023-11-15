import { Body, Controller, Get, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { User } from './user.model';
import { UserLocationDto } from './dto/user-location.dto';

@Controller('users')
export class UsersController {
    constructor(private usersService: UsersService) {}
    
    @Get()
    test(): string  {
        return "hello?";
    }

    @Post("/location")
    findNearUsers(@Body() userLocationDto: UserLocationDto): User[] {
        this.usersService.updateUserLocation(userLocationDto);
        return this.usersService.getNearUsers(userLocationDto);
    }
}
