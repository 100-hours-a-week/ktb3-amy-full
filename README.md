MoveCrew Backend  
스트릿 댄스 커뮤니티 서비스의 백엔드 API 서버입니다.

💾 데모 영상 https://youtu.be/0EjWwmFrHkY

🚀 Tech Stack
- Java 21  
- Spring Boot
- Spring Security + JWT  
- JPA (Hibernate)  
- MySQL
- Gradle  
- Swagger / SpringDoc



📌 주요 기능 (Core Features)

인증/인가 (JWT)
- Access Token + Refresh Token 구조
- 토큰 만료 시 자동 재발급 지원 (401 → Refresh → 재요청)
- Security Filter 기반 권한 검증

회원 기능
- 회원가입 / 로그인 / 로그아웃
- 프로필 이미지 업로드
- 회원 정보 수정
- 비밀번호 변경
- 회원 탈퇴

게시글 기능
- 게시글 작성 / 수정 / 삭제  
- 이미지 첨부  
- 페이징 조회 (Slice 기반 무한 스크롤)

댓글 기능
- 댓글 작성 / 수정 / 삭제 

좋아요 / 조회수
- 게시글 좋아요  
- 자동 조회수 증가



📌 API 문서 (Swagger)
배포 시 Swagger 주소를 여기에 입력 예정



🧪 Postman Collection
추후 업로드 예정



👩‍💻 개발자
백엔드: 김수민 (Amy)
