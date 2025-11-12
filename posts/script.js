const postContainer = document.getElementById("postContainer");
const createPostBtn = document.getElementById("createPostBtn");

let page = 1;
let loading = false;

document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token"); // 로그인 시 저장된 JWT

  if (!token) return; // 로그인 안 되어 있으면 패스

  try {
    const res = await fetch("http://localhost:8080/api/v1/users/me", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await res.json();

    if (res.ok && data.data) {
      const userImg = document.querySelector(".profile");
      userImg.src = data.data.profile_image || "https://via.placeholder.com/40";
    }
  } catch (error) {
    console.error("유저 정보 불러오기 실패:", error);
  }
});

// 게시글 작성 페이지 이동
createPostBtn.addEventListener("click", () => {
  window.location.href = "/write/index.html";
});

// 날짜 포맷
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

// 숫자 축약 (좋아요, 댓글, 조회수)
function formatCount(num) {
  if (num >= 100000) return "100k";
  if (num >= 10000) return "10k";
  if (num >= 1000) return "1k";
  return num;
}

// 게시글 카드 생성
function createPostCard(post) {
  const card = document.createElement("div");
  card.className = "post-card";

  const title = post.title.length > 26 ? post.title.slice(0, 26) + "..." : post.title;

  card.innerHTML = `
    <div class="post-header">
      <div class="post-title">${title}</div>
      <div class="post-date">${formatDate(post.created_at)}</div>
    </div>
    <div class="post-meta">
      <div class="stats">
        ❤️ ${formatCount(post.like_count)}
        💬 ${formatCount(post.comment_count)}
        👀 ${formatCount(post.view_count)}
      </div>
    </div>
    <div class="post-author">
      <img src="${post.author_image || 'https://via.placeholder.com/25'}" alt="author" />
      ${post.author}
    </div>
  `;

  // 카드 클릭 → 상세 페이지 이동
  card.addEventListener("click", () => {
    window.location.href = `/posts/${post.post_id}/index.html`;
  });

  postContainer.appendChild(card);
}

// 게시글 불러오기 (Fetch API)
async function fetchPosts() {
  if (loading) return;
  loading = true;

  try {
    const res = await fetch(`http://localhost:8080/api/v1/posts?page=${page}`);
    const data = await res.json();

    if (res.ok && data.data) {
      data.data.forEach(createPostCard);
      page++;
    }
  } catch (error) {
    console.error("게시글 불러오기 실패:", error);
  } finally {
    loading = false;
  }
}

// 인피니티 스크롤
window.addEventListener("scroll", () => {
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
  if (scrollTop + clientHeight >= scrollHeight - 5) {
    fetchPosts();
  }
});

// 초기 로드
fetchPosts();