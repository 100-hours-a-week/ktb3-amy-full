import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

const postContainer = document.getElementById("postContainer");
const createBtn = document.getElementById("createBtn");
const loading = document.getElementById("loading");
const endMessage = document.getElementById("endMessage");

let page = 0;
let isLoading = false;
let end = false;

// 버튼 호버 효과
createBtn.addEventListener("mouseover", () => {
  createBtn.classList.add("hover-effect");
});
createBtn.addEventListener("mouseleave", () => {
  createBtn.classList.remove("hover-effect");
});

// 게시글 작성 페이지 이동
createBtn.addEventListener("click", () => {
  window.location.href = "../makepost/index.html";
});

// 숫자 포맷
function formatCount(num) {
  if (num >= 100000) return "100k";
  if (num >= 10000) return "10k";
  if (num >= 1000) return "1k";
  return num;
}

// 날짜 포맷
function formatDate(dateString) {
  const d = new Date(dateString);
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const min = String(d.getMinutes()).padStart(2, "0");
  const sec = String(d.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${min}:${sec}`;
}

// 게시글 카드 생성
function createPostCard(post) {
  const card = document.createElement("div");
  card.classList.add("post-card");

  let title = post.title;
  if (title.length > 26) title = title.substring(0, 26) + "...";

  card.innerHTML = `
    <button class="more-btn">⋯</button>

    <div class="dropdown-menu">
      <div data-action="edit" data-id="${post.id}">수정</div>
      <div data-action="delete" data-id="${post.id}">삭제</div>
    </div>

    <div class="post-title">${title}</div>
    <div class="post-info">${formatDate(post.createdAt)}</div>

    <div class="count-box">
      <span>조회수 ${formatCount(post.viewCount)}</span>
      <span>댓글 ${formatCount(post.commentCount)}</span>
      <span>좋아요 ${formatCount(post.likeCount)}</span>
    </div>
  `;

  card.classList.add("card-hover");

  // 카드 전체 클릭 → 상세 페이지 이동
  card.addEventListener("click", (e) => {
    if (e.target.closest(".more-btn")) return; // ⋯ 버튼 눌렀으면 이동X
    if (e.target.closest(".dropdown-menu")) return; // 메뉴 눌렀으면 이동X
    window.location.href = `../post/index.html?id=${post.id}`;
  });

  return card;
}

// 게시글 로딩
async function loadPosts() {
  if (isLoading || end) return;

  isLoading = true;
  loading.classList.remove("hidden");

  try {
    const res = await fetchWithAuth(
      `${API_BASE_URL}/api/v1/posts/search/slice?keyword=&page=${page}&size=10&sortBy=id&direction=desc`
    );

    if (!res.ok) throw new Error("불러오기 실패");

    const slice = await res.json();

    slice.content.forEach((post) => {
      postContainer.appendChild(createPostCard(post));
    });

    if (slice.last) {
      end = true;
      endMessage.classList.remove("hidden");
    }

    page++;

  } catch (err) {
    console.error("게시글 로딩 오류:", err);
    alert("게시글을 불러오는 중 오류가 발생했습니다.");
  }

  loading.classList.add("hidden");
  isLoading = false;
}

// 초기 로딩
loadPosts();

// 무한 스크롤 (Throttle)
let throttleTimer = null;
window.addEventListener("scroll", () => {
  if (throttleTimer) return;

  throttleTimer = setTimeout(() => {
    throttleTimer = null;

    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 250) {
      loadPosts();
    }
  }, 220);
});

// ⋯ 버튼 + 드롭다운 메뉴 열기
document.addEventListener("click", (e) => {
  const btn = e.target.closest(".more-btn");

  // 열려있는 모든 드롭다운 닫기
  document.querySelectorAll(".dropdown-menu.show")
    .forEach((menu) => menu.classList.remove("show"));

  // 새로운 ⋯ 버튼 클릭 시 토글
  if (btn) {
    btn.nextElementSibling.classList.toggle("show");
    e.stopPropagation();
  }
});

// 수정 / 삭제 메뉴 클릭 처리
document.addEventListener("click", async (e) => {
  const item = e.target.closest(".dropdown-menu div");
  if (!item) return;

  const action = item.dataset.action;
  const id = item.dataset.id;

  if (action === "edit") {
    window.location.href = `../editpost/index.html?id=${id}`;
    return;
  }

  if (action === "delete") {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    try {
      const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/posts/${id}`, {
        method: "DELETE",
      });

      if (!res.ok) {
        alert("삭제 실패!");
        return;
      }

      const card = item.closest(".post-card");
      card.classList.add("fade-out");
      setTimeout(() => card.remove(), 300);

    } catch (err) {
      console.error("삭제 오류:", err);
      alert("오류 발생: 삭제 불가");
    }
  }
});