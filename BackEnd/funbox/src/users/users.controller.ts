import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { User } from './user.entity';

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
}
