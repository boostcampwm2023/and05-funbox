import { Injectable } from '@nestjs/common';
import { User } from './user.model';
import { UserLocationDto } from './dto/user-location.dto';

@Injectable()
export class UsersService {
    // mock data, 추후 DB 연동
    private users: User[] = [
        {
            id: 0,
            username: 'user0',
            created_at: '2023-10-10',
            profile_url: 'https://google.com',
            locX: 123.4567,
            locY: 123.4567,
            message: 'hi0',
            messaged_at: '2023-10-10'
        },
        {
            id: 1,
            username: 'user1',
            created_at: '2023-10-10',
            profile_url: 'https://google.com',
            locX: 123.4568,
            locY: 123.4568,
            message: 'hi1',
            messaged_at: '2023-11-11'
        },
        {
            id: 2,
            username: 'user2',
            created_at: '2023-10-10',
            profile_url: 'https://google.com',
            locX: 123.4569,
            locY: 123.4569,
            message: 'hi2',
            messaged_at: '2023-10-10'
        }
    ]

    getNearUsers(userLocationDto: UserLocationDto): User[] {
        const {locX, locY} = userLocationDto;
        // TODO: 주변 유저 필터링 로직
        return this.users
    }
    updateUserLocation(userLocationDto: UserLocationDto): void {
        const user = this.users[0]; // TODO: 인증된 User를 특정할 필요가 있음
        const {locX, locY} = userLocationDto;
        user.locX = locX;
        user.locY = locY;
    }
}
