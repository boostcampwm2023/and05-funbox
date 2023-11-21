import { User } from "../user.entity";

export class NearUsersDto {
    id: number;
    username: string;
    locX: number;
    locY: number;
    isMsgInAnHour: boolean;

    static of(user: User): NearUsersDto {
        const {id, username, locX, locY} = user;
        return {id, username, locX, locY, isMsgInAnHour: true}
    }
}