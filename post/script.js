const backBtn = document.getElementById("backBtn");
const postContainer = document.getElementById("postContainer");

const overlay = document.getElementById("overlay");

const deletePostModal = document.getElementById("deletePostModal");
const cancelDeletePost = document.getElementById("cancelDeletePost");
const confirmDeletePost = document.getElementById("confirmDeletePost");

const deleteCommentModal = document.getElementById("deleteCommentModal");
const cancelDeleteComment = document.getElementById("cancelDeleteComment");
const confirmDeleteComment = document.getElementById("confirmDeleteComment");

let postId = new URLSearchParams(window.location.search).get("id");
let editingCommentId = null;

// 단위 축약
function formatCount(n) {
  if (n >= 100000) return "100k";
  if (n >= 10000) return "10k";
  if (n >= 1000) return "1k";
  return n;
}

// 날짜 포맷
function formatDate(str) {
  const d = new Date(str);
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const mi = String(d.getMinutes()).padStart(2, "0");
  const ss = String(d.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
}

async function loadPost() {
  const res = await fetch(`http://localhost:8080/api/v1/posts/${postId}`);
  const data = await res.json();
  const p = data.data;

  postContainer.innerHTML = `
    <h2 class="post-title">${p.title}</h2>

    <div class="post-meta">
      <div class="writer-img"></div>
      <span>${p.authorNickname}</span>
      <span>${formatDate(p.createdAt)}</span>
      <button id="editBtn">수정</button>
      <button id="deleteBtn">삭제</button>
    </div>

    ${p.imageUrl ? `<img src="${p.imageUrl}" style="width:100%; border-radius:10px; margin-top:10px;">` : ""}

    <div class="post-content">${p.content}</div>

    <div class="post-stats">
      <button id="likeBtn" class="stat-btn ${p.liked ? "active" : ""}">
        ${formatCount(p.likes)} 좋아요
      </button>
      <button class="stat-btn">${formatCount(p.views)} 조회수</button>
      <button class="stat-btn">${formatCount(p.commentCount)} 댓글</button>
    </div>

    <div class="comment-box">
      <textarea id="commentInput" placeholder="댓글을 남겨주세요"></textarea>
      <button id="commentSubmitBtn">댓글 등록</button>
    </div>

    <div id="commentList"></div>
  `;

 document.getElementById("editBtn").addEventListener("click", () => {
  window.location.href = "/editpost/index.html?id=" + postId;
});


  document.getElementById("deleteBtn").addEventListener("click", () => {
    openModal(deletePostModal);
  });

  document.getElementById("likeBtn").addEventListener("click", toggleLike);
  document.getElementById("commentInput").addEventListener("input", handleCommentInput);
  document.getElementById("commentSubmitBtn").addEventListener("click", submitComment);

  loadComments();
}

async function loadComments() {
  const res = await fetch(`http://localhost:8080/api/v1/comments/post/${postId}`);
  const data = await res.json();

  const commentList = document.getElementById("commentList");
  commentList.innerHTML = "";

  data.data.forEach((c) => {
    const div = document.createElement("div");
    div.className = "comment-item";

    div.innerHTML = `
      <div class="comment-meta">
        <div class="comment-img"></div>
        <span>${c.authorNickname}</span>
        <span>${formatDate(c.createdAt)}</span>
        <button class="editCommentBtn" data-id="${c.id}">수정</button>
        <button class="deleteCommentBtn" data-id="${c.id}">삭제</button>
      </div>
      <div class="comment-text">${c.content}</div>
    `;

    commentList.appendChild(div);
  });

  document.querySelectorAll(".editCommentBtn").forEach(btn =>
    btn.addEventListener("click", enterEditComment)
  );

  document.querySelectorAll(".deleteCommentBtn").forEach(btn =>
    btn.addEventListener("click", openDeleteCommentModal)
  );
}

async function toggleLike() {
  const likeBtn = document.getElementById("likeBtn");
  const liked = likeBtn.classList.contains("active");

  const res = await fetch(`http://localhost:8080/api/v1/posts/${postId}/like`, {
    method: "POST"
  });
  const data = await res.json();

  likeBtn.classList.toggle("active");

  likeBtn.textContent = `${formatCount(data.data.likes)} 좋아요`;
}

function handleCommentInput() {
  const text = document.getElementById("commentInput").value.trim();
  const btn = document.getElementById("commentSubmitBtn");

  if (text.length > 0) {
    btn.classList.add("enabled");
  } else {
    btn.classList.remove("enabled");
  }
}

async function submitComment() {
  const input = document.getElementById("commentInput");
  const text = input.value.trim();
  if (!text) return;

  const url = editingCommentId
    ? `http://localhost:8080/api/v1/comments/${editingCommentId}`
    : `http://localhost:8080/api/v1/comments`;

  const method = editingCommentId ? "PUT" : "POST";

  const body = JSON.stringify({
    postId,
    content: text
  });

  await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body
  });

  editingCommentId = null;
  document.getElementById("commentSubmitBtn").textContent = "댓글 등록";
  input.value = "";
  handleCommentInput();

  loadComments();
}

function enterEditComment(event) {
  editingCommentId = event.target.dataset.id;
  const text = event.target.closest(".comment-item").querySelector(".comment-text").textContent;

  document.getElementById("commentInput").value = text;
  document.getElementById("commentSubmitBtn").textContent = "댓글 수정";
  handleCommentInput();
}

let deletingCommentId = null;

function openDeleteCommentModal(event) {
  deletingCommentId = event.target.dataset.id;
  openModal(deleteCommentModal);
}

confirmDeleteComment.addEventListener("click", async () => {
  await fetch(`http://localhost:8080/api/v1/comments/${deletingCommentId}`, {
    method: "DELETE"
  });

  closeModal(deleteCommentModal);
  loadComments();
});

confirmDeletePost.addEventListener("click", async () => {
  await fetch(`http://localhost:8080/api/v1/posts/${postId}`, {
    method: "DELETE"
  });

  closeModal(deletePostModal);
  window.location.href = "/posts/index.html";
});

function openModal(modal) {
  overlay.classList.remove("hidden");
  modal.classList.remove("hidden");
  document.body.style.overflow = "hidden";
}

function closeModal(modal) {
  overlay.classList.add("hidden");
  modal.classList.add("hidden");
  document.body.style.overflow = "auto";
}

cancelDeletePost.addEventListener("click", () => closeModal(deletePostModal));
cancelDeleteComment.addEventListener("click", () => closeModal(deleteCommentModal));

// 뒤로가기
backBtn.addEventListener("click", () => {
  window.location.href = "/posts/index.html";
});

// 초기 로딩
loadPost();
