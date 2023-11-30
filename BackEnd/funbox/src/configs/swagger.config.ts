import { DocumentBuilder } from '@nestjs/swagger';

export const swaggerConfig = new DocumentBuilder()
  .setTitle('FunBox API Document')
  .setDescription('FunBox 프로젝트의 API 문서입니다.')
  .setVersion('1.0.0')
  .addBearerAuth()
  .build();
