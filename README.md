# Tiketeer BE

## 의존성

- Java 21
- Spring Boot 3

## 실행법

- .env 추가
  ```yml
  DB_USERNAME= # DB Username
  DB_DATABASE= # DB database name
  DB_PASSWORD= # DB Password
  EMAIL_ACCOUNT= # email server account
  EMAIL_PASSWORD= # app password
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
        ddl-auto: validate
      show-sql: true
    flyway:
      enabled: true
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
- 도커 컴포즈
  ```dtd
  docker-compose -f docker-compose.dev.yml -d up
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