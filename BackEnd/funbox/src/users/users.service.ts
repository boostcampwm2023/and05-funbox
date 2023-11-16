import { Injectable, NotFoundException } from '@nestjs/common';
import { User } from './user.entity';

@Injectable()
export class UsersService {
    async createUsertest(): Promise<User> {
        const user = new User();
        user.username = "test";
        user.created_at = "123123";
        user.profile_url = "132123";
        user.locX = 123;
        user.locY = 123;
        user.message =  "hihi";
        user.messaged_at = "123123";
        return await user.save();
    }
}
