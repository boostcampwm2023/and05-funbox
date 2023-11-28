import { Injectable, NotFoundException } from '@nestjs/common';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';
import * as crypto from 'crypto';

const AWS = require('aws-sdk');
const endpoint = new AWS.Endpoint('https://kr.object.ncloudstorage.com');
const region = 'kr-standard';
const access_key = process.env.OS_ACCESS_KEY;
const secret_key = process.env.OS_SECRET_KEY;
const S3 = new AWS.S3({
  endpoint: endpoint,
  region: region,
  credentials: {
    accessKeyId: access_key,
    secretAccessKey: secret_key,
  },
});
const bucket_name = 'funbox-profiles';

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

  async uploadS3(id, file) {
    const hashedpath = crypto
      .createHash('sha256')
      .update(id + file.originalname)
      .digest('hex');
    const object_name = hashedpath + '.' + file.originalname.split('.')[1];
    await (async () => {
      await S3.putObject({
        Bucket: bucket_name,
        Key: object_name,
        Body: file.buffer,
      }).promise();
    })();
    return object_name;
  }

  async deleteS3(filepath) {
    await (async () => {
      const object_name = filepath;
      await S3.deleteObject({
        Bucket: bucket_name,
        Key: object_name,
      }).promise();
    })();
  }
}
