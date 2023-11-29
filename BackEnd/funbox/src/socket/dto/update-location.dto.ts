import { IsNotEmpty, IsNumber } from 'class-validator';

export class UpdateLocationDto {
  @IsNotEmpty()
  @IsNumber()
  locX: number;

  @IsNotEmpty()
  @IsNumber()
  locY: number;
}
