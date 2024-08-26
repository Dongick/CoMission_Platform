# 📝 CoMission Platform - 공유미션 플랫폼 서비스
> ### 배포 도메인 -> [Comission Platform](https://comission-platform.shop)
<br/>


🔽 서비스 메인 페이지
---
![Animation1](https://github.com/Dongick/CoMission_Platform/assets/91418544/bcc09051-272c-4583-bf4d-7e82d9b426c8)
---
<br/>

🔽 소셜 로그인 후 메인 페이지
---
![Animation](https://github.com/Dongick/CoMission_Platform/assets/91418544/fb73658e-491b-4300-b386-a18dea9dcc95)
---
<br/>

🔽 미션 참가 후, 인증글 작성/삭제
---
![Animation2-min](https://github.com/Dongick/CoMission_Platform/assets/91418544/fdc27d1e-3dfe-4998-a8d1-c03d64da7856)
---

<br/>

## ✅ 프로젝트 소개
- **Comission Platform**은 사용자가 로그인하고 미션을 생성하여 사람들과 공유할 수 있는 웹 애플리케이션입니다
- 사용자는 특정 제목, 설명, 인증주기 및 참여 제한을 사용하여 미션을 만들 수 있습니다.
- 미션이 생성되면 다른 사용자들과 함께 꾸준히 미션을 진행하며 목표 달성을 위해 노력할 수 있습니다.
- 사용자들이 함께 목표를 공유하고 달성할 수 있도록 함으로써 경쟁의식과 동기를 부여합니다.


## ✅ 프로젝트 사용법
- 누구나 본인들만의 미션을 생성&공유를 통해, 참여자들을 모집해 각자의 미션을 진행할 수 있습니다.
- 미션에 참가하는 참여자들은 미션에 기재된 인증주기에 따라 인증글을 작성하여야 한다

## ✅ 개발환경 및 기술스택

### ⭕ Frontend
#### 언어
- Typescript (4.9.5)

#### 라이브러리
- React.js (18.2.0)
- Styled-components

#### 상태관리
* 클라이언트 상태 관리
  - Recoil (0.7.7)
* 서버 상태 관리
  - Tanstack Query (v5)

#### 클라우드
- AWS S3
- AWS Cloudfront
- AWS ACM
- AWS Route53

#### 기타
- Axios (HTTP 비동기 통신)
- JWT (JSON Web Tokens)

### ⭕ Backend
#### 언어
- Java 17

#### 프레임워크
- Spring Boot
- Spring Data JPA

#### 데이터베이스
- MongoDB
- MySQL

#### 보안
- JWT (JSON Web Tokens)
- OAuth2 (네이버 및 구글 로그인 구현)
- Spring Security

#### 클라우드
- AWS EC2 (배포)
- AWS S3 (이미지 파일 저장)

#### CI && CD
- Docker
- Github Actions

#### 성능테스트
- K6
- Grafana

#### 기타
- Swagger (API 문서화)


## ✅ Git branch 전략
- main
  - develop
      - frontend
      - backend
