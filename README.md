# Tiketeer BE

## 의존성

- Java 21
- Spring Boot 3

## 실행법

- .env 추가
  ```yml
  DB_USERNAME= # DB Username
  DB_PASSWORD= # DB Password
  ```

- application.yml 추가
  ```yml
  # src/main/resources/application.yml
  server:
    port: # Server Port
    servlet:
      context-path: "/api"

  spring:
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: # JDBC Connection URL
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: true
    mail:
      host: smtp.gmail.com
      port: 587
      username: ${EMAIL_ACCOUNT}
      password: ${EMAIL_PASSWORD}
      properties:
        mail:
          smtp:
            auth: true
            starttls:
              enable: true
              required: true
            connectiontimeout: 5000
            timeout: 5000
            writetimeout: 5000
          mime:
            charset: UTF-8
  logging:
    level:
      org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  ```

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