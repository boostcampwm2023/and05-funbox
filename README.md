# FunBox
### 지도 기반 아이스브레이킹 소셜 플랫폼

> "아이스브레이킹 앱으로 우리동네에서 친구 만들기!"

<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/2f6c6652-87ea-445f-b730-197689e9d98c" width="200">
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/a005eeae-9acf-4cb4-9bd6-2d083de7e47b" width="200">
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/5039c0d0-c930-45bc-af99-25ad69b8fb96" width="200">

* 지도에서 새로운 친구를 찾아요!
* 친구를 찾으면 오프라인으로 만나러 가요
* 만나서 미니게임으로 아이스브레이킹을 해요

## 📒 주요 기능

### 네이버 소셜 로그인과 프로필 사진
* 네이버 계정으로 간편하게 로그인해요
* 나만의 프로필 사진을 보여줄 수 있어요
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/9c8f29f4-b453-41a1-b95a-d2bd4e12cf7c" width="200">
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/cc33c9d3-1953-40a8-ae93-be604018ec0f" width="200">

### 네이버 지도상 실시간 위치 공유
* 폴링으로 유저들의 실시간 위치를 공유해요
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/bffe0656-2107-4f50-bf7d-8f5e84a02de7" width="200">

### 채팅과 미니 게임 (TMI 퀴즈)
* Socket.io로 채팅과 미니 게임을 구현했어요 

<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/0d95f3d8-cb0d-45bb-9cd3-eae76eb093a9" width="200">
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/5e173e39-6ea8-4b98-9c5f-b7fbbbeb70db" width="200">
<img src="https://github.com/boostcampwm2023/and05-funbox/assets/11827454/0f5020d1-572a-493f-a694-879cbc8c0be6" width="200">

## ⚒ 기술스택

### Android
| Category  | TechStack |
| ------------- | ------------- |
| Architecture  | [MVVM](https://www.notion.so/standingtiger/Android-MVVM-17b0401697b440abbfce093c37ce2664) | 
| Network | [Retrofit](https://www.notion.so/standingtiger/Android-Retrofit2-with-moshi-424c2a23ba67468c983c0ed50deec952), [OkHttp](https://www.notion.so/standingtiger/Android-Retrofit2-with-moshi-424c2a23ba67468c983c0ed50deec952), [Moshi](https://www.notion.so/standingtiger/Android-Retrofit2-with-moshi-424c2a23ba67468c983c0ed50deec952), socket.io | 
| Asynchronous | [Coroutines](https://www.notion.so/standingtiger/Android-Coroutines-188727db63d24c9582817f4958156fb0), [Flow](https://www.notion.so/standingtiger/Android-SharedFlow-vs-StateFlow-d21f3d6ac52d4d729952dd63858ea267) | 
| Jetpack | DataBinding, Navigation, Fragment, Lifecycle, Material Design Components | 
| Image | [Coil](https://www.notion.so/standingtiger/Android-Coil-38933ade945444fe91e968fb1e5f839d), Glide | 
| Map | [Naver Map](https://www.notion.so/standingtiger/Android-NAVER-4fc0840f69fd4018b909874982285eaf) | 
| Login | [OAuth](https://www.notion.so/standingtiger/Android-NAVER-3eca4c9d3dcc4691bd794af1649efb1c) | 
| CI/CD | [Firebase App Distribution](https://www.notion.so/standingtiger/Android-Firebase-App-Distribution-d4cedd3dc6de4af2a2807f49214306f7) | 
| Test | Junit | 
| Logging | [Timber](https://www.notion.so/standingtiger/Timber-eea2750e4aba4f5e8a811aa0b45fccf5) | 

### Backend
| Category  | TechStack | Reason |
| ------------- | ------------- | ------------- |
| Framework, Language | NestJS, TypeScript  | 강력한 타입 시스템과 모듈화된 구조로 협업에 적합 |
| DB | MySQL  | 안정적이고 널리 사용되는 관계형 데이터베이스 |
| ORM | TypeORM  | TypeScript와 잘 통합되며 객체 지향적 데이터베이스 관리 가능 |
| API Docs | Swagger | API 설계와 문서화 자동화로 개발 과정 간소화 |
| CI/CD | Github Actions  |  코드 변경에 대한 자동 빌드, 테스트, 배포로 CI/CD 효율성 향상 |
| NCP | Server, VPC, Object Storage|안정적인 서버 운영, 보안 네트워크, 확장 가능한 데이터 저장소 |

## 💬 기술적 고민과 선택

### Backend
1. 백엔드 for 클라이언트 개발 : 안드로이드 팀과 협업을 위한 도구 구현
   - Swagger를 도입해 수시로 바뀌는 API를 효과적으로 전달하고 또 테스트 도구로도 활용했습니다.
   - 개발용 인증 우회 API를 구현해 개발 과정을 가속시켰습니다.
   - 가상 유저를 구현해 UX 향상에 기여했습니다.

    백엔드는 유저를 위한 서비스 뿐 아니라 클라이언트 개발을 위한 서비스도 개발한다는 것을 배웠습니다.

2. 프로필 사진 전송 : 보안 vs 서버 부하 + 편의성
   - 클라이언트가 서버를 통해 프로필 사진을 받으면 보안적 이점이 있으나, 서버의 부하가 증가합니다.
   - Object storage에서 직접 전송하도록 하여 서버의 부하를 낮췄습니다.
   - 파일명을 hash로 암호화하여 악의적인 유저의 무작위 접근을 막았습니다.

3. 양방향 통신 : 폴링 vs Socket
   - 유저의 실시간 위치공유와 미니게임, 채팅을 구현하기 위해 서버에서 클라이언트로 메시지를 전달합니다.
   - 폴링으로 유저의 위치를 다른 유저와 실시간으로 공유하도록 했습니다.
   - 게임과 채팅은 소켓으로 구현하였습니다.

   자세한 내용은 링크를 참고해주세요!

### Android
1. 퀴즈 게임 로직 구현 : 소켓 이벤트 수신의 문제
   - 퀴즈 게임 기능을 구현하면서 발생한 문제를 해결하였습니다.
     - 각 화면별로 소켓을 새로 연결하는 것이 아닌 **싱글톤으로 하나의 소켓 객체를 여러 화면이 활용하도록** 하였습니다.
     - **ViewModel에서 이벤트를 구독**할 수 있게 하였습니다.
     - 그 결과 **모든 화면이 게임에 필요한 이벤트를 구독하여 게임 시작 이벤트를 수신**할 수 있었습니다.

2. Naver Map의 InfoWindow : 이동할 때마다 깜빡여 신경쓰인다!
   - **마커를 터치하면 나타나는 InfoWindow에 해당 유저의 정보가 깜빡이면서 업데이트 되는 현상**이 발생했습니다.
   - 또한, **InfoWindow가 존재하는 상태에서 해당 유저가 움직여도 InfoWindow는 그러지 않는 문제가 발생**하였습니다.
     - 이러한 문제를 해결하기 위해 **처음 지도에 들어 왔을때 조건에 맞는 유저들의 정보를 StateFlow 형태의 HashMap에 저장**하였습니다.
     - 이후 3초마다 유저 정보를 받아온 후 **이미 HashMap에 있는 유저는 위치 정보와 수정 사항만 갱신하면서 InfoWindow를 동일하게 출력**하도록 하였습니다.
     - 그 결과 이동이 자연스럽게 동작하고 있는 것을 확인하였습니다.

3. 채팅 기능 : 채팅 내용이 바로 보이지 않음
   - 게임 화면의 채팅 내용이 제대로 갱신되지 않는 문제가 있었습니다.
     - 채팅의 경우 채팅 내용을 flow를 이용하여 내용을 채팅 내역들을 저장하고 보여주는 형식으로 작성하였습니다.
     - 이 때 리스트로된 stateFlow는 리스트값이 추가되는 것으로는 collect가 작동하지 않아 **채팅이 제대로 갱신되지 않는 문제**가 있었습니다.
     - **채팅 전송 상태에 따른 stateflow를 따로 두어서 채팅이 추가 될 때 viewModel 내부에서 업데이트 후 채팅 전송 상태를 collect하여 갱신**시키도록 변경하였습니다.

4. 권한과 버전별 대응
   - 프로필 사진을 설정하기 위하여 **저장 공간 및 카메라 관련 권한을 요청**합니다.
   - 사용자의 현재 위치를 서버에 전송하는 과정에서 **위치 관련 권한이 필수적**입니다.
     - 하지만 사용자가 권한을 허용하고 싶지 않아할 경우에는 **권한 허용이 필요하다는 토스트 메시지를 띄우도록** 하였습니다.
     - 다만, 안드로이드 버전이 높다면 권한 요청 없이 PhotoPicker를 사용하여 이미지 파일을 가져올 수 있도록 하였습니다.

### 링크
<a href="https://www.notion.so/standingtiger/FunBox-17ef334b88974d09b356c4833cc37806">
  <img alt="Static Badge" src="https://img.shields.io/badge/Notion-%2523000000.svg?style=for-the-badge&logo=notion&logoColor=white&labelColor=black&color=black&link=https%3A%2F%2Fwww.notion.so%2Fstandingtiger%2FFunBox-17ef334b88974d09b356c4833cc37806">
</a>
<a href="https://github.com/boostcampwm2023/and05-funbox/wiki">
  <img alt="Static Badge" src="https://img.shields.io/badge/github%20wiki-%252523121011.svg?style=for-the-badge&logo=github&logoColor=white&labelColor=black&color=black&link=https%3A%2F%2Fgithub.com%2Fboostcampwm2023%2Fand05-funbox%2Fwiki">
</a>

## 👥 Team RPG (Rhythm, Power, Gypjoongryuk!)

### 팀 구성
<div align="center">

|김선범(BE)|김승찬(BE)|김시환(AOS)|김신영(AOS)|정지원(AOS)|
|:--:|:--:|:--:|:--:|:--:|
|<a href="https://github.com/k33ps/"> <img src="https://avatars.githubusercontent.com/u/11827454?v=4" width="100px" height="100px"> </a>|<a href="https://github.com/sschan99"> <img src="https://avatars.githubusercontent.com/u/42964952?v=4" width="100px" height="100px"> </a>|<a href="https://github.com/van1164"> <img src="https://avatars.githubusercontent.com/u/52437971?v=4" width="100px" height="100px"> </a>|<a href="https://github.com/tlsdud135"> <img src="https://avatars.githubusercontent.com/u/62438036?v=4" width="100px" height="100px"> </a>|<a href="https://github.com/littlesam95"> <img src="https://avatars.githubusercontent.com/u/55424662?v=4" width="100px" height="100px"> </a>|
|"이때까지 배운것들의 복습. 기본기에 충실하자!"|"기본적인 아키텍처나 디자인 패턴 등을 완성도있게 활용할 생각입니다."|"이력서 포트폴리오에 쓸 수있는 최종 배포까지 가는 프로젝트"|"끝까지 했으면 좋겠습니다"|"멤버십에서 8주간 학습한 것을 활용해보고 싶습니다."|
</div>
