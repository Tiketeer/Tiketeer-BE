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