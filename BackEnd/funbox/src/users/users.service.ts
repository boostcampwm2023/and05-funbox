import { Injectable, NotFoundException } from '@nestjs/common';
import { User } from './user.entity';
import { UserLocationDto } from './dto/user-location.dto';
import { NearUsersDto } from './dto/near-users.dto';
import { LessThanOrEqual, MoreThanOrEqual, Not } from 'typeorm';
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
  async updateUserLocation(
    id: number,
    userLocationDto: UserLocationDto,
  ): Promise<void> {
    const user = await User.findOne({ where: { id: id } });
    user.locX = userLocationDto.locX;
    user.locY = userLocationDto.locY;
    user.last_polling_at = new Date();
    await user.save();
    return;
  }

  async findNearUsers(id: number): Promise<NearUsersDto[]> {
    const users = await findNearUsersAlgorithm(id);
    const nearUsersDto = [];
    users.forEach((user) => {
      nearUsersDto.push(NearUsersDto.of(user));
    });
    return nearUsersDto;

    async function findNearUsersAlgorithm(idSelf: number): Promise<User[]> {
      const now = new Date();
      const tenSecondsAgo = new Date();
      tenSecondsAgo.setSeconds(now.getSeconds() - 10);
      return await User.find({
        where: {
          id: Not(idSelf),
          last_polling_at: MoreThanOrEqual(tenSecondsAgo),
        },
      });
    }
  }

  async getUserById(id: number): Promise<User> {
    const user = await User.findOneBy({ id: id });
    if (!user) {
      throw new NotFoundException(`Can't find User with id ${id}`);
    }
    return user;
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
        ACL: 'public-read',
      }).promise();
    })();
    return object_name;
  }

  async deleteS3(filepath) {
    if (filepath === null) {
      return;
    }
    await (async () => {
      const object_name = filepath;
      await S3.deleteObject({
        Bucket: bucket_name,
        Key: object_name,
      }).promise();
    })();
  }

  async deleteUser(id: number): Promise<void> {
    const user = await this.getUserById(id);
    await this.deleteS3(user.profile_url);
    await User.delete(id);
    return;
  }
}
