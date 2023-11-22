import { ConflictException, Injectable, InternalServerErrorException } from '@nestjs/common';
import { User } from 'src/users/user.entity';
import { UserAuthDto } from './dto/user-auth.dto';
import * as crypto from 'crypto';

@Injectable()
export class AuthService {
    async createNullUser(idOauth: string): Promise<UserAuthDto> {
        const user = new User();
        this.setUserNull(user, idOauth);
        try {
            await user.save();
        } catch (error) {
            
            if(error.code === 'ER_DUP_ENTRY') {
                throw new ConflictException("Existing id (OAuth)");
            } else {
                console.log(error);
                throw new InternalServerErrorException();
            }
        }
        return UserAuthDto.of(user);
    }

    setUserNull(user: User, idOauth: string): User{
        const now = new Date();
        
        user.username = null;
        user.created_at = now;
        user.profile_url = null;
        user.locX = null;
        user.locY = null;
        user.message = null;
        user.messaged_at = null;
        user.type_login = "NAVER";
        user.id_oauth = this.hashedId(idOauth);

        return user;
    }

    hashedId(idOauth: string): string {
        const salt = "teamrpg";
        return crypto.createHash('sha256').update(idOauth + salt).digest('hex');
    }
}
