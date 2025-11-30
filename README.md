MoveCrew Frontend  
스트릿 댄스 커뮤니티 웹 서비스의 프론트엔드입니다.

🚀 Tech Stack
- HTML  
- CSS  
- Vanilla JavaScript  
- Fetch API  
- JWT 기반 인증 구조  
- Live Server (로컬 개발)
- React (예정)



📌 주요 기능

인증 페이지
- 로그인 / 회원가입  
- 유효성 검증  
- 로그인 성공 시 토큰 저장 & 리다이렉트

프로필 기능
- 내 정보 조회  
- 프로필 사진 렌더링  
- 헤더 드롭다운 메뉴  
- 회원정보 수정 / 비밀번호 수정  

게시글 페이지
- 게시글 목록 (무한 스크롤)  
- 검색 기능  
- 게시글 상세보기  
- 좋아요 / 조회수 표시  
- 댓글 CRUD  

에러 처리
- 자동 토큰 재발급 (fetchWithAuth.js)  
- 401 발생 시 Refresh 후 재요청  
- 실패 시 로그인 페이지로 이동
