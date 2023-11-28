import {
  Body,
  Controller,
  Delete,
  Get,
  NotFoundException,
  Param,
  Patch,
  Post,
  Req,
  UploadedFile,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { ApiTags, ApiOkResponse, ApiCreatedResponse } from '@nestjs/swagger';
import { UsersService } from './users.service';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';
import { UserResponseDto } from './dto/user-response.dto';
import { AuthGuard } from '@nestjs/passport';
import { FileInterceptor } from '@nestjs/platform-express';
import { Express } from 'express';

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
    @Req() req,
    @Body('message') message: string,
  ): Promise<UserResponseDto> {
    const id = req.user.id;
    const user = await this.usersService.updateUserMessage(id, message);
    return UserResponseDto.of(user);
  }

  @Post('/username')
  @ApiOkResponse({ type: UserResponseDto })
  async updateUserName(
    @Req() req,
    @Body('userName') userName: string,
  ): Promise<UserResponseDto> {
    const user = await this.usersService.updateUserName(req.user.id, userName);
    return UserResponseDto.of(user);
  }

  @Post('/profile')
  @ApiOkResponse({ type: UserResponseDto })
  @UseInterceptors(FileInterceptor('file'))
  async updateUserProfile(
    @Req() req,
    @UploadedFile() file: Express.Multer.File,
  ): Promise<UserResponseDto> {
    if (!file) {
      throw new NotFoundException('File not found');
    }
    const user = await this.usersService.getUserById(req.user.id);

    if (user.profile_url !== null) {
      await this.usersService.deleteS3(user.profile_url);
    }
    const filePath = await this.usersService.uploadS3(req.user.id, file);

    user.profile_url = filePath;
    await user.save();
    return UserResponseDto.of(user);
  }

  @Delete('/profile')
  @ApiOkResponse({ type: UserResponseDto })
  async deleteUserProfile(@Req() req): Promise<UserResponseDto> {
    const user = await this.usersService.getUserById(req.user.id);

    if (user.profile_url === null) {
      return UserResponseDto.of(user);
    }
    await this.usersService.deleteS3(user.profile_url);

    user.profile_url = null;
    await user.save();
    return UserResponseDto.of(user);
  }
}