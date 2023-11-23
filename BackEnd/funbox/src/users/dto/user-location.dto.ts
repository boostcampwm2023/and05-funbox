import { ApiProperty } from '@nestjs/swagger';

export class UserLocationDto {
  @ApiProperty({ description: '유저 위치 X좌표' })
  locX: number;

  @ApiProperty({ description: '유저 위치 Y좌표' })
  locY: number;
}
