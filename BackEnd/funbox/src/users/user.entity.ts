import { BaseEntity, Column, Entity, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class User extends BaseEntity {
    @PrimaryGeneratedColumn()
    id: number;

    @Column({length: 15, nullable: true})
    username: string;

    @Column({type: 'timestamp',nullable: false})
    created_at: Date;

    @Column({length: 255, nullable: true})
    profile_url: string;

    @Column({type: 'double', nullable: true})
    locX: number;

    @Column({type: 'double', nullable: true})
    locY: number;

    @Column({length: 31, nullable: true})
    message: string;

    @Column({type: 'timestamp', nullable: true})
    messaged_at: Date;

    @Column({length: 31, nullable: false})
    type_login: string;

    @Column({length: 127, nullable: false})
    id_oauth: string;
}