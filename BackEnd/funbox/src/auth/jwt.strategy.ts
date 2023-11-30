import { Injectable, NotFoundException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { User } from 'src/users/user.entity';
import { UserAuthDto } from './dto/user-auth.dto';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      secretOrKey: process.env.JWT_SECRET,
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload) {
    const { id } = payload;
    const userAuthDto = UserAuthDto.of(
      await User.findOne({ where: { id: id } }),
    );
    if (!userAuthDto) {
      throw new NotFoundException('there is not that user');
    }
    return userAuthDto;
  }
}
