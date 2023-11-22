import { Injectable } from '@nestjs/common';
import { User } from 'src/users/user.entity';
import { UserAuthDto } from './dto/user-auth.dto';

@Injectable()
export class AuthService {
    async createNullUser(id_oauth: string): Promise<UserAuthDto> {
        const user = new User();
        const now = new Date();
        user.username = null;
        user.created_at = now;
        user.profile_url = null;
        user.locX = null;
        user.locY = null;
        user.message = null;
        user.messaged_at = null;
        user.type_login = "NAVER";
        user.id_oauth = id_oauth;
        await user.save();
        return UserAuthDto.of(user);
    }
}
