import { User } from 'src/users/user.entity';

export class UserAuthDto {
  id: number;
  username: string;

  static of(user: User): UserAuthDto {
    const { id, username } = user;
    return { id, username };
  }
}
