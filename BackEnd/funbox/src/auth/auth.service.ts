import {
  ConflictException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { User } from 'src/users/user.entity';

import * as crypto from 'crypto';
import { JwtService } from '@nestjs/jwt';
import { AuthResponseDto } from './dto/auth-response.dto';
import { UserAuthDto } from './dto/user-auth.dto';

@Injectable()
export class AuthService {
  constructor(private jwtService: JwtService) {}

  async tokenValidation(accessToken: string): Promise<UserAuthDto> {
    try {
      const decode = await this.jwtService.verifyAsync(accessToken, {
        secret: process.env.JWT_SECRET,
      });
      const { id } = decode;
      return { id };
    } catch (error) {
      throw new UnauthorizedException('Invalid access token');
    }
  }

  async getNaverUserId(accessToken: string): Promise<string> {
    const url = 'https://openapi.naver.com/v1/nid/me';
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        Authorization: 'Bearer ' + accessToken,
      },
    });
    const userInfo = await response.json();
    if (userInfo.message !== 'success') {
      throw new UnauthorizedException('invalid NAVER access token');
    }
    const userId = userInfo.response.id;
    return userId;
  }

  async createNullUser(idOauth: string): Promise<AuthResponseDto> {
    const user = new User();
    this.setUserNull(user, idOauth);

    try {
      await user.save();
    } catch (error) {
      if (error.code === 'ER_DUP_ENTRY') {
        throw new ConflictException('Existing id (OAuth)');
      } else {
        console.log(error);
        throw new InternalServerErrorException();
      }
    }

    const jwtString = this.jwtService.sign(UserAuthDto.of(user));
    return AuthResponseDto.of(jwtString);
  }

  setUserNull(user: User, idOauth: string): User {
    user.username = null;
    user.created_at = new Date();
    user.profile_url = null;
    user.locX = null;
    user.locY = null;
    user.message = null;
    user.messaged_at = null;
    user.type_login = 'NAVER';
    user.id_oauth = this.hashedId(idOauth);

    return user;
  }

  hashedId(idOauth: string): string {
    const salt = process.env.SALT_OAUTHID;
    return crypto
      .createHash('sha256')
      .update(idOauth + salt)
      .digest('hex');
  }

  async findIdOauth(idOauth: string): Promise<AuthResponseDto> {
    const user = await User.findOne({
      where: { id_oauth: this.hashedId(idOauth) },
    });

    if (!user) {
      throw new NotFoundException(`Can't find User with idOauth`);
    }

    const jwtString = this.jwtService.sign(UserAuthDto.of(user));
    return AuthResponseDto.of(jwtString);
  }
}
