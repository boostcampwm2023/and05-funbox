import { BaseEntity, Column, Entity, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class User extends BaseEntity {
    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    username: string;

    @Column()
    created_at: string; //timestamp

    @Column()
    profile_url: string;

    @Column()
    locX: number;

    @Column()
    locY: number;

    @Column()
    message: string;

    @Column()
    messaged_at: string; //timestamp
}