import {
  ConflictException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { User } from 'src/users/user.entity';
import { UserAuthDto } from './dto/user-auth.dto';
import * as crypto from 'crypto';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class AuthService {
  constructor(private jwtService: JwtService) {}

  async tokenValidation(accessToken: string): Promise<UserAuthDto> {
    try {
      const decode = await this.jwtService.verifyAsync(accessToken, {
        secret: process.env.JWT_SECRET,
      });
      const { id, username } = decode;
      return { id, username };
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
      throw new UnauthorizedException('not valide naver token!');
    }
    const userId = userInfo.response.id;
    return userId;
  }

  async createNullUser(idOauth: string): Promise<{ accessToken: string }> {
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

    return this.accessToken(user);
  }

  setUserNull(user: User, idOauth: string): User {
    const now = new Date();

    user.username = null;
    user.created_at = now;
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
    const salt = 'teamrpg';
    return crypto
      .createHash('sha256')
      .update(idOauth + salt)
      .digest('hex');
  }

  async findIdOauth(idOauth: string): Promise<{ accessToken: string }> {
    const user = await User.findOne({
      where: { id_oauth: this.hashedId(idOauth) },
    });

    if (!user) {
      throw new NotFoundException(`Can't find User with idOauth`);
    }

    return this.accessToken(user);
  }

  accessToken(user: User): { accessToken: string } {
    const accessToken = this.jwtService.sign(UserAuthDto.of(user));
    return { accessToken };
  }
}
