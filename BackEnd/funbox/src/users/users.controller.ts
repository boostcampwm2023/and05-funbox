import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';

@Controller('users')
export class UsersController {
    constructor(private usersService: UsersService) {}

    @Get()
    test(): string {
        return "hello?";
    }

    @Get('/create')
    createUser(): Promise<User> {
        return this.usersService.createUsertest();
    }

    @Post()
    async updateUserLocationAndReturnNearUsers(@Body() userLocationDto: UserLocationDto): Promise<NearUsersDto[]> {
        await this.usersService.updateUserLocation(userLocationDto);
        return await this.usersService.findNearUsers(userLocationDto);
    }
}
