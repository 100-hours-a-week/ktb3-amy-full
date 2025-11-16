const postContainer = document.getElementById("postContainer");
const createPostBtn = document.getElementById("createPostBtn");

let page = 0;
let loading = false;
let lastPageReached = false;

// 게시글 작성 페이지 이동
createPostBtn.addEventListener("click", () => {
  window.location.href = "/makepost/index.html";
});

// 날짜 포맷 함수
function formatDate(dateString) {
  const date = new Date(dateString);
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const min = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`;
}

// 숫자 단위 축약
function formatCount(num) {
  if (num >= 100000) return "100k";
  if (num >= 10000) return "10k";
  if (num >= 1000) return "1k";
  return num;
}

// 게시글 카드 UI 생성
function createPostCard(post) {
  const div = document.createElement("div");
  div.className = "post-card";

  // 제목 26자 제한
  const title = post.title.length > 26 ? post.title.slice(0, 26) + "..." : post.title;

  div.innerHTML = `
    <div class="post-title">${title}</div>

    <div class="post-info">
      <span>좋아요 ${formatCount(post.likes)}</span>
      <span>댓글 ${formatCount(post.commentCount)}</span>
      <span>조회수 ${formatCount(post.views)}</span>
    </div>

    <div class="post-footer">
      <div class="writer-box">
        <div class="writer-img"></div>
        <span>${post.authorNickname}</span>
      </div>
      <div class="post-date">${formatDate(post.createdAt)}</div>
    </div>
  `;

  div.addEventListener("click", () => {
    window.location.href = `/post/index.html?id=${post.id}`;
  });

  return div;
}

// 게시글 목록 불러오기 (Slice Paging)
async function loadPosts() {
  if (loading || lastPageReached) return;
  loading = true;

  try {
    const res = await fetch(`http://localhost:8080/api/v1/posts/search/slice?page=${page}&size=6`);
    const data = await res.json();

    const posts = data.data.content;
    const isLastPage = data.data.last;

    posts.forEach(post => {
      postContainer.appendChild(createPostCard(post));
    });

    if (isLastPage) {
      lastPageReached = true;
    }

    page++;
  } catch (error) {
    console.error("게시글 불러오기 오류:", error);
  } finally {
    loading = false;
  }
}

// 인피니티 스크롤
window.addEventListener("scroll", () => {
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = document.documentElement.scrollTop;
  const clientHeight = document.documentElement.clientHeight;

  if (scrollTop + clientHeight >= scrollHeight - 200) {
    loadPosts();
  }
});

// 첫 로딩
loadPosts();
