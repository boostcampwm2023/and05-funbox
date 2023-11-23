import {
  Body,
  Controller,
  Get,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOkResponse, ApiCreatedResponse } from '@nestjs/swagger';
import { UsersService } from './users.service';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';
import { UserResponseDto } from './dto/user-response.dto';
import { AuthGuard } from '@nestjs/passport';

@Controller('users')
@ApiTags('유저 API')
@UseGuards(AuthGuard())
export class UsersController {
  constructor(private usersService: UsersService) {}

  @Post('/create')
  @ApiCreatedResponse({ type: User })
  createUser(): Promise<User> {
    return this.usersService.createUsertest();
  }

  @Post('location')
  @ApiOkResponse({ type: [NearUsersDto] })
  async updateUserLocationAndReturnNearUsers(
    @Body() userLocationDto: UserLocationDto,
  ): Promise<NearUsersDto[]> {
    await this.usersService.updateUserLocation(userLocationDto);
    return await this.usersService.findNearUsers(userLocationDto);
  }

  @Get('/:id')
  @ApiOkResponse({ type: UserResponseDto })
  async getUserById(@Param('id') id: number): Promise<UserResponseDto> {
    const user = await this.usersService.getUserById(id);
    return UserResponseDto.of(user);
  }

  @Patch('/message')
  @ApiOkResponse({ type: UserResponseDto })
  async updateUserMessage(
    @Body('message') message: string,
  ): Promise<UserResponseDto> {
    const id = 1; // TODO: auth 모듈을 통해 user id 추출
    const user = await this.usersService.updateUserMessage(id, message);
    return UserResponseDto.of(user);
  }
}
