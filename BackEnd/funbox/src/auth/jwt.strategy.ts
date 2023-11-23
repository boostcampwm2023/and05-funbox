import { Injectable, NotFoundException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { UserAuthDto } from './dto/user-auth.dto';
import { User } from 'src/users/user.entity';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      secretOrKey: process.env.JWT_SECRET,
      jwtFromRequest: ExtractJwt.fromBodyField('accessToken'),
    });
  }

  async validate(payload) {
    const { id, username } = payload;
    const userAuthDto = UserAuthDto.of(
      await User.findOne({ where: { id: id, username: username } }),
    );
    if (!userAuthDto) {
      throw new NotFoundException('there is not that user');
    }
    return userAuthDto;
  }
}
