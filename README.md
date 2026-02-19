# 📌 15분식탁 (15min-table)

> 자취생과 1인 가구를 위한  
> **15분 이내 · 저비용 레시피 공유 & 커뮤니티 서비스**

---

## 📖 목차
- [🍳 프로젝트 소개](#-프로젝트-소개)
- [✨ 핵심 기능](#-핵심-기능)
- [👥 팀원 소개](#-팀원-소개)
- [🤝 협업 방식](#-협업-방식)
- [🔍 브랜치 전략](#-브랜치-전략)
- [⚡ 아키텍처 개요](#-아키텍처-개요)
- [🧱 기술 스택](#-기술-스택)
- [🚀 배포](#-배포)
- [🖥 화면 구현](#-화면-구현)

---

## 🍳 프로젝트 소개

**15분식탁**은  
자취생과 1인 가구를 위한  
**15분 이내 · 저비용 레시피 공유 서비스**입니다.

복잡한 조리 과정과 긴 요리 시간을 줄이고,  
누구나 부담 없이 집밥을 즐길 수 있도록  
레시피 공유와 커뮤니티 기능을 중심으로 설계되었습니다.

### ✨ 핵심 컨셉
- ⏱️ 15분 이내 조리 기준
- 💰 저비용 재료 중심
- 🍽️ 자취생 맞춤 집밥
- 💬 레시피 중심 커뮤니티

---

## ✨ 핵심 기능

- 레시피 작성 / 수정 / 조회
- 댓글, 북마크, 마이페이지
- 커뮤니티 게시판
- 카카오맵 기반 주변 마트 / 편의점 탐색
- AI 해시태그 자동 추천
- JWT 기반 인증 / 인가
- 관리자 페이지 실시간 관리
- 팔로워 / 팔로잉 및 실시간 알림 기능

---

## 👥 팀원 소개

| 이름 | 역할 | GitHub |
|---|---|---|
| 심재원 | 팀장 / Backend & Frontend | [@S-JaeWon](https://github.com/S-JaeWon) |
| 배찬익 | 팀원 / Backend & Frontend | [@moon-ray-b](https://github.com/moon-ray-b) |
| 홍해준 | 팀원 / Backend & Frontend | [@HJ-Hong](https://github.com/HJ.Hong) |

---

## 🤝 협업 방식

### 역할 분리 기반 협업
- Notion 기반 요구사항 및 기획 정리
- GitHub Issue 기반 작업 관리
- 기능 단위 브랜치 생성 후 개발
- Pull Request + 코드 리뷰 후 병합
- Discord를 통한 실시간 커뮤니케이션

### 역할 분담
- **Frontend**
  - UI / UX 구현
  - API 연동 및 상태 관리
- **Backend**
  - REST API 설계
  - 인증 / 인가
  - DB 및 비즈니스 로직 구현
- **Design**
  - Figma 기반 디자인 구현

---

## 🔍 브랜치 전략

본 프로젝트는 프론트엔드와 백엔드를 분리하여 개발하는 구조로,  
기능 단위 작업과 안정적인 병합을 위해 명확한 브랜치 전략을 사용하였다.

### 기본 브랜치 구성
- **main**
  - 항상 배포 가능한 상태를 유지
  - 모든 기능은 Pull Request를 통해서만 병합
- **develop (선택)**
  - 기능 통합 테스트 브랜치
  - 필요 시 `main` 병합 전 검증 용도로 사용

### 이슈 기반 브랜치 전략
- 모든 작업은 GitHub Issue 생성 후 시작
- 하나의 이슈는 하나의 브랜치에서만 처리
- 브랜치는 `main` 브랜치에서 분기
- 프론트엔드 / 백엔드는 동일 이슈 기준으로 분리 작업

### 브랜치 네이밍 방식
- 작업 성격과 작업 영역이 드러나도록 구성
- 브랜치 이름만 보고도 작업 내용을 파악 가능
- 이슈 번호를 포함하여 추적 용이성 확보

### 브랜치 운영 원칙
- `main` 브랜치 직접 커밋 금지
- 하나의 브랜치 = 하나의 작업 목적
- PR 기반 코드 리뷰 필수

### 정기 회의 및 코드 리뷰
- **월요일**: 주간 진행 상황 공유 및 계획 수립
- **금요일**: 작업 결과 공유 및 이슈 정리
- 프론트엔드: UI 시연
- 백엔드: API 및 기능 구현 리뷰
- 공통 코드 스타일 및 규칙 지속 개선

---

## ⚡ 아키텍처 개요

본 프로젝트는 프론트엔드와 백엔드를 분리한 구조로 설계하여  
역할 분담이 명확하고 유지보수가 용이하도록 구성하였다.

### 전체 구조
- 프론트엔드: 사용자 화면 및 관리자 페이지
- 백엔드: REST API 및 비즈니스 로직
- REST API 기반 JSON 통신

### 프론트엔드 아키텍처
- React 기반 SPA 구조
- 페이지 / 컴포넌트 단위 분리
- API 요청 로직 분리
- 관리자 페이지 분리 구성
- 인증 상태 일관성 유지

### 백엔드 아키텍처
- Spring Boot 기반 REST API 서버
- Controller / Service / Mapper 계층 구조
- 관리자 및 통계 기능 분리
- MyBatis 기반 SQL 관리

---

## 🧱 기술 스택

### 🎨 Frontend
[![Frontend Skills](https://skillicons.dev/icons?i=js,html,react,materialui,vite)](https://skillicons.dev)

### ⚙️ Backend
[![Backend Skills](https://skillicons.dev/icons?i=spring,java,mysql,postman,redis)](https://skillicons.dev)

### 🚀 Deployment & Infrastructure
[![Infra Skills](https://skillicons.dev/icons?i=github,git,docker,notion)](https://skillicons.dev)

### 🤝 Collaboration
[![Collaboration Skills](https://skillicons.dev/icons?i=idea,vscode)](https://skillicons.dev)

### 💻 OS
[![OS](https://skillicons.dev/icons?i=apple,windows)](https://skillicons.dev)

---

## 🚀 배포

본 프로젝트는 **Docker Compose**를 사용하여  
백엔드 서버 및 Redis 환경을 컨테이너 기반으로 구성하였다.

### 실행 환경
- Docker
- Docker Compose
- Redis
- RedisInsight

---

#### ❇️ 배포 및 실행 방법

 docker-compose.yml

 ✅ 관리용 명령어 모음
 1) 실행(백그라운드):          docker compose up -d
 2) 상태 확인:                docker compose ps
 3) 로그 보기:                docker compose logs -f
 4) 특정 서비스만 로그:        docker compose logs -f redis
 5) 중지(컨테이너 유지):       docker compose stop
 6) 재시작:                   docker compose restart
 7) 내리기(컨테이너/네트워크 삭제): docker compose down
 8) 내리기 + 볼륨까지 삭제(⚠️ 데이터도 삭제): docker compose down -v

 ✅ Redis 접속/테스트
 - Redis CLI 접속:            docker exec -it redis-15mintable redis-cli
 - ping 테스트:               docker exec -it redis-15mintable redis-cli ping

 ✅ RedisInsight 접속(브라우저)
 - http://localhost:5540
 - Redis 연결 URL(도커 RedisInsight 기준):
     방법 A) 컨테이너끼리: redis://redis-15mintable:6379
     방법 B) 호스트 경유: redis://host.docker.internal:6379

---

#### ❇️ API 명세서

| 이름 | 메서드 | 엔드포인트 | 설명 |
|---|---:|---|---|
| signup | POST | /user/auth/signup | 회원가입 |
| signin | POST | /user/auth/signin | 로그인 |
| getPrincipal | GET | /user/account/principal | 로그인 사용자(Principal) 정보 조회 |
| refresh | POST | /user/auth/refresh | Access Token 재발급(Refresh Token 기반) **(추가)** |
| logout | POST | /user/auth/logout | 로그아웃(세션 revoke + 쿠키 삭제/블랙리스트 처리) **(추가)** |
| getActivities | GET | /admin/manage/activities | 관리자 활동/로그/이벤트 목록 조회 **(추가)** |
| getAdminPosts | GET | /admin/manage/posts | 관리자용 게시글 관리 목록 조회 **(추가)** |
| getAdminRecipes | GET | /admin/manage/recipes | 관리자용 레시피 관리 목록 조회 **(추가)** |
| getStats | GET | /admin/manage/stats | 관리자 대시보드 통계 요약 조회 **(추가)** |
| getStatsTimeseries | GET | /admin/manage/stats/timeseries | 통계 시계열 데이터 조회 **(추가)** |
| banUser | POST | /admin/manage/user/{userId}/ban | 특정 유저 제재/차단 처리 **(추가)** |
| restoreUser | POST | /admin/manage/user/{userId}/restore | 특정 유저 제재 해제/복구 처리 **(추가)** |
| changePassword | POST | /user/account/change/password | 비밀번호 변경 |
| changeUsername | POST | /user/account/change/username | 닉네임(유저명) 변경 |
| changeProfileImg | POST | /user/account/change/profileImg | 프로필 이미지 변경(업로드) |
| withdraw | POST | /user/account/withdraw | 회원 탈퇴 처리 |
| 이메일 인증 메일 발송 | POST | /mail/send | 이메일 인증 링크 메일 발송 |
| 이메일 인증 링크 처리 | GET | /mail/verify?token={token} | 이메일 인증 토큰 검증 및 인증 완료 처리 |
| OAuth2 로그인 시작 | GET | /oauth2/authorization/{provider} | OAuth2(provider) 로그인/인증 플로우 시작 |
| OAuth2 signup | POST | /oauth2/signup | OAuth2 신규 회원가입 처리 |
| OAuth2 merge | POST | /oauth2/merge | OAuth2 계정과 기존 계정 연동(병합) |
| getUserList | GET | /admin/manage/user/list | 관리자용 유저 목록 조회 |
| getUserByUsername | GET | /admin/manage/user/{username} | 관리자용 유저 상세 조회(username 기준) |
| addBoard | POST | /board | 게시판 생성 |
| getBoardList | GET | /board | 게시판 목록 조회 |
| removeBoard | DELETE | /board/{boardId} | 게시판 삭제 |
| addRecipe | POST | /board/{boardId}/recipes/add | 레시피 등록 |
| getRecipeDetail | GET | /board/{boardId}/recipes/detail/{recipeId} | 레시피 상세 조회 |
| getRecipeList | GET | /board/{boardId}/recipes/list | 레시피 목록 조회(게시판별) |
| getRecipeListFiltered | GET | /board/{boardId}/recipes/list/filtered | 레시피 목록 필터링 조회(검색/카테고리/정렬 등) **(추가)** |
| modifyRecipe | PUT | /board/{boardId}/recipes/modify/{recipeId} | 레시피 수정 |
| removeRecipe | DELETE | /board/{boardId}/recipes/remove/{recipeId} | 레시피 삭제 |
| generateHashtags | POST | /ai/hashtags | AI 해시태그 추천 생성 |
| searchHashtags | GET | /recipe-hashtag/search | 해시태그 검색 |
| getHashtagsByRecipeId | GET | /recipe-hashtag/list/{recipeId} | 레시피 해시태그 목록 조회 |
| addRecipeHashtags | POST | /recipe-hashtag/add | 레시피 해시태그 연결(추가) |
| getSummary | GET | /rating/{recipeId}/summary | 레시피 평점 요약 조회 |
| deleteRating | POST | /rating/delete/{recipeId} | 평점 삭제 |
| upsertRating | POST | /rating/upsert | 평점 등록/수정(업서트) |
| addBookmark | POST | /bookmark/{recipeId} | 북마크(찜) 추가 |
| existsByRecipeId | GET | /bookmark/{recipeId} | 북마크 여부 조회 |
| deleteBookmark | DELETE | /bookmark/{recipeId} | 북마크(찜) 삭제 |
| addPost | POST | /board/{boardId}/posts/add | 게시글(포스트) 등록 **(추가)** |
| getPostDetail | GET | /board/{boardId}/posts/detail/{postId} | 게시글(포스트) 상세 조회 **(추가)** |
| getPostList | GET | /board/{boardId}/posts/list | 게시글(포스트) 목록 조회(게시판별) **(추가)** |
| modifyPost | PUT | /board/{boardId}/posts/modify/{postId} | 게시글(포스트) 수정 **(추가)** |
| removePost | DELETE | /board/{boardId}/posts/remove/{postId} | 게시글(포스트) 삭제 **(추가)** |
| getRecipeCommentListByRecipeId | GET | /comment/list/recipe/{recipeId} | 레시피 댓글 목록 조회 |
| addRecipeComment | POST | /comment/add/recipe/{recipeId} | 레시피 댓글 등록 |
| addPostComment | POST | /comment/add/post/{postId} | 게시글(포스트) 댓글 등록 **(추가)** |
| getPostCommentListByPostId | GET | /comment/list/post/{postId} | 게시글(포스트) 댓글 목록 조회 **(추가)** |
| deleteComment | POST | /comment/delete/{commentId} | 댓글 삭제 |
| getMyRecipeList | GET | /recipes/my | 내 레시피 목록 조회 |
| getMyPostList | GET | /posts/my | 내 게시글(포스트) 목록 조회 **(추가)** |
| getRating | GET | /rating/{recipeId}/get | 내 레시피 평점 조회 |
| getBookmarkListByUserId | GET | /bookmark/my | 내 북마크(찜) 목록 조회 |
| getMyCommentList | GET | /comment/my/list | 내가 작성한 댓글 목록 조회 |
| getFollowers | GET | /follow/followers | 내 팔로워 목록 조회 |
| getFollowings | GET | /follow/followings | 내 팔로잉 목록 조회 |
| getFollowCount | GET | /follow/count/{userId} | 팔로잉 / 팔로워 수 조회 |
| subscribe | GET | /notifications/subscribe | 알림 SSE 구독 |
| getUnreadCount | GET | /notifications/unread-count | 미읽음 알림 개수 조회 |
| markAllAsRead | POST | /notifications/read-all | 알림 전체 읽음 처리 |
| markAsRead | POST | /notifications/{notificationId}/read | 특정 알림 읽음 처리 |
| getNotifications | GET | /notifications | 알림 목록 조회 |
| follow | POST | /follow/{targetUserId} | 팔로우 처리 |
| unfollow | DELETE | /follow/{targetUserId} | 언팔로우 처리 |
| getFollowStatus | GET | /follow/status/{targetUserId} | 특정 유저 팔로우 상태 조회 |
| getUserProfile | GET | /users/profile/{userId} | 특정 유저의 프로필 정보 조회 |
| getRecipeListByUserId | GET | /recipes/user/{userId} | 특정 유저 레시피 목록 조회 |
| getPostListByUserId | GET | /posts/user/{userId} | 특정 유저 게시글(포스트) 목록 조회 **(추가)** |


## 🖥 화면 구현

---

### ✅ 메인 화면
<img src="https://github.com/user-attachments/assets/fd62dc60-ed31-4c7e-afed-2e1e37b04a34" width="900" alt="메인 화면" />

---

### ✅ 로그인 & 회원가입

<img src="https://github.com/user-attachments/assets/c2dd4175-0ef6-4fa8-98b7-cc76b85bad83" width="900" alt="로그인 화면" />

<img src="https://github.com/user-attachments/assets/ff04954e-a0e5-4154-b5bd-8da23e51660c" width="900" alt="회원가입 화면" />

---

### ✅ 관리자 페이지

<img src="https://github.com/user-attachments/assets/4f904358-9c52-455f-80dd-fc9a27766c47" width="900" alt="관리자 대시보드" />

<img src="https://github.com/user-attachments/assets/e5dac5e6-8f2b-42d8-b8db-3d7004a620c7" width="900" alt="관리자 통계 화면" />

<img src="https://github.com/user-attachments/assets/c6b9415b-1ea6-4fc5-82ee-8e7e53b5fa51" width="900" alt="관리자 사용자 관리" />

<img src="https://github.com/user-attachments/assets/900059d8-1530-4a2e-aa8c-0a0884235a5c" width="900" alt="관리자 콘텐츠 관리" />

---

### ✅ 게시물 & 커뮤니티 페이지

<img src="https://github.com/user-attachments/assets/a20d39c6-15e8-4796-9091-9fbf85a8f73f" width="900" alt="게시물 목록" />

<img src="https://github.com/user-attachments/assets/903fe87f-4ea1-4917-a531-5538107c54d2" width="900" alt="게시물 상세 및 커뮤니티 화면" />
