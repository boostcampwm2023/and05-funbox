# 20231114 Tue
## 구현한 기능 명세
### nest.js 서버 설치

[feat] nest.js 서버 인스톨

nest.js 서버를 인스톨 했다.
nest.js 프레임워크를 선택한 이유는 아래와 같다.
- 기본 구조가 통일화되어 있어 구조 파악이 명확하고 간편함.
- 자유도가 다소 떨어지지만, 빠른 개발이 가능함
- 타입스크립트를 적극 지원함으로써, 이슈 발생 미연에 방지
- 개발에 자주 사용되는 기능이 내장되어 있어 편리.
```
sudo npm i -g @nestjs/cli
nest new funbox
```

### 서버 구동 확인
아래와 같이 잘 작동함을 확인했다.
```
$ npm run start

> funbox@0.0.1 start
> nest start

[Nest] 77419  - 11/14/2023, 3:10:52 PM     LOG [NestFactory] Starting Nest application...
[Nest] 77419  - 11/14/2023, 3:10:53 PM     LOG [InstanceLoader] AppModule dependencies initialized +70ms
[Nest] 77419  - 11/14/2023, 3:10:53 PM     LOG [RoutesResolver] AppController {/}: +32ms
[Nest] 77419  - 11/14/2023, 3:10:53 PM     LOG [RouterExplorer] Mapped {/, GET} route +5ms
[Nest] 77419  - 11/14/2023, 3:10:53 PM     LOG [NestApplication] Nest application successfully started +6ms
```

### users 모듈 생성
```
$ nest g module users
```
### users 컨트롤러 생성
```
$ nest g controller users --no-spec
```
### users 서비스 생성
```
$ nest g service users --no-spec
```
### users 테스트 메소드 작성
get 요청 응답 확인

### 

## 고민과 해결 과정

## 학습 메모

## 참고 자료