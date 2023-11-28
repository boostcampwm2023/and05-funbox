import { Socket } from 'socket.io';

export class SocketUserDto {
  id: number;
  username: string;
  locX: number;
  locY: number;

  static of(client: Socket): SocketUserDto {
    const { userId, username, locX, locY } = client.data;
    return { id: userId, username, locX, locY };
  }
}
