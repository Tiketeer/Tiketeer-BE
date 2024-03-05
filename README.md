# Tiketeer BE

## 의존성

- Java 21
- Spring Boot 3

## 실행법

- 서버 실행
  ```shell
  ./gradlew bootrun
  ```
- 유닛 테스트
  ```shell
  ./gradlew clean test
  ```
  
## API

- 서버 실행 후 스웨거를 통해 확인 가능
- /swagger-ui/index.html
- 접속 후 username: user, password: {서버 실행 시 반환되는 패스워드 문자열} 입력 (이는 아직 Security 엔드포인트 설정을 해주지 않아서 그런 것. 수정 후 해당 라인 삭제 요망)