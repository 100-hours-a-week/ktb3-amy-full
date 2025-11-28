import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

const postId = new URLSearchParams(location.search).get("id");

const backBtn = document.getElementById("backBtn");
const editBtn = document.getElementById("editBtn");
const deleteBtn = document.getElementById("deleteBtn");

const modalOverlay = document.getElementById("modalOverlay");
const deleteModal = document.getElementById("deleteModal");
const modalConfirmBtn = document.getElementById("modalConfirmBtn");
const modalCancelBtn = document.getElementById("modalCancelBtn");

const likeBtn = document.getElementById("likeBtn");
const likeCountEl = document.getElementById("likeCount");
const viewCountEl = document.getElementById("viewCount");
const commentCountEl = document.getElementById("commentCount");

const titleEl = document.getElementById("postTitle");
const contentEl = document.getElementById("postContent");
const imageEl = document.getElementById("postImage");

const commentInput = document.getElementById("commentInput");
const commentSubmitBtn = document.getElementById("commentSubmitBtn");
const commentList = document.getElementById("commentList");

let editCommentId = null;
let likeCooldown = false;

// 숫자 포맷
function formatCount(n) {
  if (n >= 100000) return "100k";
  if (n >= 10000) return "10k";
  if (n >= 1000) return Math.floor(n / 1000) + "k";
  return n;
}

// 게시글 불러오기
async function fetchPost() {
  const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/posts/${postId}`);
  if (!res.ok) throw new Error("게시글 불러오기 실패");
  return await res.json();
}

// 로그인한 사용자
async function fetchCurrentUser() {
  const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/users/me`);
  if (!res.ok) return null;
  return (await res.json()).data;
}

// 게시글 렌더링
function renderPost(data, currentUser) {
  titleEl.textContent = data.title;
  contentEl.textContent = data.content;

  // 이미지 표시
  if (data.imageUrl) {
    let img = data.imageUrl;
    if (img.startsWith("/uploads")) img = `${API_BASE_URL}${img}`;
    imageEl.src = img;
    imageEl.classList.remove("hidden");
  }

  // 숫자 표시
  viewCountEl.textContent = formatCount(data.viewCount);
  likeCountEl.textContent = formatCount(data.likeCount);
  commentCountEl.textContent = data.commentCount;

  // 좋아요 초기 상태: 누르기 전 = enabled
  likeBtn.classList.add("enabled");

  // 수정/삭제 권한
  if (!currentUser || currentUser.id !== data.authorId) {
    editBtn.classList.add("hidden");
    deleteBtn.classList.add("hidden");
  }
}

async function loadPost() {
  try {
    const [postData, currentUser] = await Promise.all([
      fetchPost(),
      fetchCurrentUser()
    ]);

    renderPost(postData, currentUser);
  } catch (err) {
    console.error(err);
    alert("게시글 정보를 불러오는 중 오류 발생");
  }
}

// 댓글 불러오기
async function fetchComments() {
  const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/comments/post/${postId}`);
  if (!res.ok) return [];
  return await res.json();
}

function renderComments(comments) {
  commentList.innerHTML = comments
    .map(c => `
      <li class="comment-item fade-in" data-id="${c.id}">
        <p class="comment-text">${c.content}</p>
        <div class="comment-actions">
          <button class="edit-btn" data-id="${c.id}" data-content="${c.content}">수정</button>
          <button class="delete-btn" data-id="${c.id}">삭제</button>
        </div>
      </li>
    `)
    .join("");

  commentCountEl.textContent = comments.length;
}

async function loadComments() {
  renderComments(await fetchComments());
}

// 댓글 입력
commentInput.addEventListener("input", () => {
  const hasText = commentInput.value.trim().length > 0;
  commentSubmitBtn.disabled = !hasText;
  commentSubmitBtn.classList.toggle("enabled", hasText);
});

// 댓글 등록
commentSubmitBtn.addEventListener("click", async () => {
  const content = commentInput.value.trim();
  if (!content) return;

  try {
    let url = `${API_BASE_URL}/api/v1/comments`;
    let method = "POST";
    let body = { postId: Number(postId), content };

    if (editCommentId) {
      url = `${API_BASE_URL}/api/v1/comments/${editCommentId}`;
      method = "PATCH";
      body = { content };
      editCommentId = null;
      commentSubmitBtn.textContent = "댓글 등록";
    }

    await fetchWithAuth(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    commentInput.value = "";
    commentSubmitBtn.disabled = true;
    commentSubmitBtn.classList.remove("enabled");

    loadComments();

  } catch (err) {
    console.error(err);
    alert("댓글 처리 오류 발생");
  }
});

// 댓글 수정/삭제 이벤트
commentList.addEventListener("click", (e) => {
  const t = e.target;

  if (t.classList.contains("edit-btn")) {
    editCommentId = t.dataset.id;
    commentInput.value = t.dataset.content;
    commentSubmitBtn.textContent = "댓글 수정";
    commentSubmitBtn.disabled = false;
    commentSubmitBtn.classList.add("enabled");
    commentInput.focus();
    return;
  }

  if (t.classList.contains("delete-btn")) {
    openDeleteCommentModal(t.dataset.id);
  }
});

// 모달 관련
function openDeleteCommentModal(id) {
  modalOverlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");

  modalConfirmBtn.onclick = async () => {
    await fetchWithAuth(`${API_BASE_URL}/api/v1/comments/${id}`, { method: "DELETE" });

    closeModal();
    loadComments();
  };
}

function closeModal() {
  modalOverlay.classList.add("hidden");
  deleteModal.classList.add("hidden");
}

modalCancelBtn.addEventListener("click", closeModal);

// 게시글 삭제
deleteBtn.addEventListener("click", () => {
  modalOverlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");

  modalConfirmBtn.onclick = async () => {
    await fetchWithAuth(`${API_BASE_URL}/api/v1/posts/${postId}`, { method: "DELETE" });
    window.location.href = "/posts/index.html";
  };
});

// 좋아요 기능
likeBtn.addEventListener("click", async () => {
  if (likeCooldown) return;
  likeCooldown = true;
  setTimeout(() => (likeCooldown = false), 500);

  const res = await fetchWithAuth(
    `${API_BASE_URL}/api/v1/likes?postId=${postId}`,
    { method: "POST" }
  );
  const result = await res.json();
  const liked = result.liked;

  let raw = likeCountEl.textContent.replace(/[^0-9]/g, "");
  let count = Number(raw) || 0;

  if (liked) {
    // 좋아요 ON → 버튼 dim 상태
    likeBtn.classList.remove("enabled");
    likeBtn.classList.add("disabled");
    likeCountEl.textContent = count + 1;
  } else {
    // 좋아요 OFF → 다시 enabled 보라색
    likeBtn.classList.remove("disabled");
    likeBtn.classList.add("enabled");
    likeCountEl.textContent = Math.max(0, count - 1);
  }
});

backBtn.addEventListener("click", () => {
  window.location.href = "/posts/index.html";
});

editBtn.addEventListener("click", () => {
  window.location.href = `/editpost/index.html?id=${postId}`;
});

loadPost();
loadComments();