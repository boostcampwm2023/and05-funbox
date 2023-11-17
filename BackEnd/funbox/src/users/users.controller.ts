import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { User } from './user.entity';
import { UserResponseDto } from './dto/user-response.dto';

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

    @Get('/:id')
    async getUserById(@Param('id') id: number): Promise<UserResponseDto> {
        const user = await this.usersService.getUserById(id)
        return UserResponseDto.of(user);
    }

    @Patch("/message")
    async updateUserMessage(
        @Body('message') message: string
    ): Promise<UserResponseDto> {
        const id = 1; // TODO: auth 모듈을 통해 user id 추출
        const user = await this.usersService.updateUserMessage(id, message)
        return UserResponseDto.of(user);
    }
}
