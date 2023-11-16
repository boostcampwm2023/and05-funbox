import { Injectable, NotFoundException } from '@nestjs/common';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';

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

    async updateUserLocation(userLocationDto: UserLocationDto): Promise<User> {
        const user = await User.findOne({where : {id:1}}) // [!]OAuth 구현 후, id:1 부분 변경 필요
        user.locX = userLocationDto.locX;
        user.locY = userLocationDto.locY;
        await user.save();
        return user;
    }

    async findNearUsers(userLocationDto: UserLocationDto) : Promise<NearUsersDto[]> {
        const users = await findNearUsersAlgorithm() // [!] 주변 조회 알고리즘 고도화 필요
        const nearUsersDto = [];
        users.forEach(user => { 
            nearUsersDto.push(transFromUserToDto(user));
        });
        return nearUsersDto;

        function transFromUserToDto(user: User) { //[?] 구조분해할당으로 간지나게 하고싶음...
            const dto = new NearUsersDto();
            dto.id = user.id;
            dto.username = user.username;
            dto.locX = user.locX;
            dto.locY = user.locY;
            dto.isMsgInAnHour = true; // [!] timestamp 관련 로직 필요
            return dto;
        }

        async function findNearUsersAlgorithm(): Promise<User[]> {
            return await User.find()
        }
    }
}
