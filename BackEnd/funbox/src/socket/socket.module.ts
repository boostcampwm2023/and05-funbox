import { Module } from '@nestjs/common';
import { SocketGateway } from './socket.gateway';
import { SocketService } from './socket.service';
import { AuthModule } from 'src/auth/auth.module';
import { UsersModule } from 'src/users/users.module';
import { GameService } from './game.service';

@Module({
  imports: [AuthModule, UsersModule],
  providers: [SocketGateway, SocketService, GameService],
})
export class SocketModule {}
