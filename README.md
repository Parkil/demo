# Demo

### 로컬에서 실행

###### 사전 조건
```aiignore
docker, docker compose 가 설치되어 있거나 docker desktop 이 설치가 되어 있어야 한다
```

###### 실행 방법
```aiignore
spring profile 을 default(=설정하지 않음) 로 설정한 상태에서 DemoApplication 실행
```

###### DB 초기설정 스크립트
- 위치
  - src/main/resources/db/migration
- V1__DDL.sql
  - 테이블 생성 sql
- V2__INIT_DATA.sql
  - 초기 데이터 입력 sql

###### docker 관련 파일
- docker-compose.yml
    - 로컬 실행시 DB 인스턴스 생성용 docker compose 파일
- docker-compose-standalone.yml
    - docker 단일 실행용 docker compose 파일
- Dockerfile_alpine_jre21
  - jre 환경 docker image 설정 파일
- Dockerfile_demo
  -  demo application docker image 설정 파일

### StandAlone 모드 실행

###### 사전 조건
```aiignore
docker, docker compose 가 설치되어 있거나 docker desktop 이 설치가 되어 있어야 한다
```

###### 실행 방법
```aiignore
window 환경일 경우 run_standalone_demo.bat 실행
linux, mac 환경일 경우 run_standalone_demo.sh 실행
```

