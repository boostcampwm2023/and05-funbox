import { ApiProperty } from '@nestjs/swagger';
import {
  BaseEntity,
  Column,
  Entity,
  PrimaryGeneratedColumn,
  Unique,
} from 'typeorm';

@Entity()
@Unique(['id_oauth'])
export class User extends BaseEntity {
  @PrimaryGeneratedColumn()
  @ApiProperty({
    description: 'id',
  })
  id: number;

  @Column({ length: 15, nullable: true })
  @ApiProperty({
    description: '이름',
  })
  username: string;

  @Column({ type: 'timestamp', nullable: false })
  @ApiProperty({ description: '유저 생성 시간' })
  created_at: Date;

  @Column({ length: 255, nullable: true })
  @ApiProperty({
    example: 'sldkfjglkcvjboweirjglskdfjsdfkgjlsdfkg.png',
    description: '프로필 사진 URL',
  })
  profile_url: string;

  @Column({ type: 'double', nullable: true })
  @ApiProperty({ description: '유저 위치 X좌표' })
  locX: number;

  @Column({ type: 'double', nullable: true })
  @ApiProperty({ description: '유저 위치 Y좌표' })
  locY: number;

  @Column({ length: 31, nullable: true })
  @ApiProperty({ description: '유저 메시지' })
  message: string;

  @Column({ type: 'timestamp', nullable: true })
  @ApiProperty({ description: '메시지가 작성된 시간' })
  messaged_at: Date;

  @Column({ length: 31, nullable: false })
  type_login: string;

  @Column({ length: 127, nullable: false })
  id_oauth: string;

  @Column({ type: 'timestamp', nullable: true })
  last_polling_at: Date;
}
