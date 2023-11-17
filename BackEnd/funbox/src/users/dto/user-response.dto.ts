import { User } from "../user.entity";

export class UserResponseDto {
    name: string;
    profileUrl: string;
    message: string;

    static of(user: User): UserResponseDto {
        return {
            name: user.username,
            profileUrl: user.profile_url,
            message: user.message,
        };
    }
}