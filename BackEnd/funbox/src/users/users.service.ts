import { Injectable, NotFoundException } from '@nestjs/common';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';
import * as fs from 'fs';
import * as path from 'path';
import * as crypto from 'crypto';

@Injectable()
export class UsersService {
  async createUsertest(): Promise<User> {
    const user = new User();
    user.username = 'test';
    user.created_at = new Date();
    user.profile_url = '132123';
    user.locX = 123;
    user.locY = 123;
    user.message = 'hihi';
    user.messaged_at = new Date();
    return await user.save();
  }

  async updateUserLocation(userLocationDto: UserLocationDto): Promise<User> {
    const user = await User.findOne({ where: { id: 1 } }); // [!]OAuth 구현 후, id:1 부분 변경 필요
    user.locX = userLocationDto.locX;
    user.locY = userLocationDto.locY;
    await user.save();
    return user;
  }

  async findNearUsers(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    userLocationDto: UserLocationDto,
  ): Promise<NearUsersDto[]> {
    const users = await findNearUsersAlgorithm(); // [!] 주변 조회 알고리즘 고도화 필요
    const nearUsersDto = [];
    users.forEach((user) => {
      nearUsersDto.push(NearUsersDto.of(user));
    });
    return nearUsersDto;

    async function findNearUsersAlgorithm(): Promise<User[]> {
      return await User.find();
    }
  }

  async getUserById(id: number): Promise<User> {
    const found = await User.findOneBy({ id: id });
    if (!found) {
      throw new NotFoundException(`Can't find User with id ${id}`);
    }
    return found;
  }

  async updateUserMessage(id: number, message: string): Promise<User> {
    const user = await this.getUserById(id);
    user.message = message;
    user.messaged_at = new Date();
    return await user.save();
  }

  async updateUserName(id: number, username: string): Promise<User> {
    const user = await this.getUserById(id);
    user.username = username;
    return await user.save();
  }

  async updateUserProfileUrl(id: number, profileUrl: string): Promise<User> {
    const user = await this.getUserById(id);
    user.profile_url = profileUrl;
    return await user.save();
  }

  async saveFile(file: Express.Multer.File, id: number): Promise<string> {
    const hashedpath = crypto
      .createHash('sha256')
      .update(id + file.originalname)
      .digest('hex');
    const filePath = path.join(
      './test/' + hashedpath + '.' + file.originalname.split('.')[1],
    );
    return new Promise((resolve, reject) => {
      fs.writeFile(filePath, file.buffer, (err) => {
        if (err) {
          reject(err);
        } else {
          resolve(filePath);
        }
      });
    });
  }

  async uploadS3(filepath) {
    const AWS = require('aws-sdk');
    const fs = require('fs');
    const endpoint = new AWS.Endpoint('https://kr.object.ncloudstorage.com');
    const region = 'kr-standard';
    const access_key = 'NGlaoUiOjImoIKwokYPk';
    const secret_key = '5oeFecEZ5U8IED8FmfrHVE6dOHCeMHrXOZeDMzeH';

    const S3 = new AWS.S3({
      endpoint: endpoint,
      region: region,
      credentials: {
        accessKeyId: access_key,
        secretAccessKey: secret_key,
      },
    });

    const bucket_name = 'funbox-profiles';
    const local_file_path = filepath;

    (async () => {
      const object_name = local_file_path;
      // upload file
      await S3.putObject({
        Bucket: bucket_name,
        Key: object_name,

        Body: fs.createReadStream(local_file_path),
      }).promise();
    })();
  }
}
