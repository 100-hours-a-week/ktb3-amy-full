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

function formatCount(n) {
  if (n >= 100000) return "100k";
  if (n >= 10000) return "10k";
  if (n >= 1000) return "1k";
  return n;
}

async function fetchPost() {
  const res = await fetch(`http://localhost:8080/api/v1/posts/${postId}`);
  if (!res.ok) throw new Error("게시글 불러오기 실패");
  return await res.json();
}

function renderPost(data) {
  titleEl.textContent = data.title;
  contentEl.textContent = data.content;

  // 업로드된 이미지 경로 보정
  if (data.imageUrl) {
    let img = data.imageUrl;
    if (img.startsWith("/uploads")) img = "http://localhost:8080" + img;

    imageEl.src = img;
    imageEl.classList.remove("hidden");
  }

  viewCountEl.textContent = formatCount(data.viewCount);
  likeCountEl.textContent = formatCount(data.likeCount);
  commentCountEl.textContent = data.commentCount;

  // 로그인한 사용자가 작성자인 경우만 수정/삭제 버튼 표시
  const loggedUserId = Number(localStorage.getItem("userId"));
  if (data.authorId !== loggedUserId) {
    editBtn.classList.add("hidden");
    deleteBtn.classList.add("hidden");
  }
}

async function loadPost() {
  try {
    const data = await fetchPost();
    renderPost(data);
  } catch (e) {
    console.error(e);
    alert("게시글 정보를 불러오는 중 오류가 발생했습니다.");
  }
}

async function fetchComments() {
  const res = await fetch(`http://localhost:8080/api/v1/comments/post/${postId}`);
  if (!res.ok) return [];
  return await res.json();
}

function renderComments(comments) {
  commentList.innerHTML = "";

  comments.forEach(c => {
    commentList.innerHTML += `
      <li class="comment-item fade-in" data-id="${c.id}">
        <p class="comment-text">${c.content}</p>
        <div class="comment-actions">
          <button class="edit-btn" data-id="${c.id}" data-content="${c.content}">수정</button>
          <button class="delete-btn" data-id="${c.id}">삭제</button>
        </div>
      </li>
    `;
  });

  commentCountEl.textContent = comments.length;
}

async function loadComments() {
  const comments = await fetchComments();
  renderComments(comments);
}

commentInput.addEventListener("input", () => {
  const hasText = commentInput.value.trim().length > 0;
  commentSubmitBtn.disabled = !hasText;
  commentSubmitBtn.classList.toggle("enabled", hasText);
});

commentSubmitBtn.addEventListener("click", async () => {
  const content = commentInput.value.trim();
  if (!content) return;

  const userId = Number(localStorage.getItem("userId"));
  if (!userId) {
    alert("로그인이 필요합니다.");
    return;
  }

  try {
    // 수정
    if (editCommentId) {
      await fetch(`http://localhost:8080/api/v1/comments/${editCommentId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content }),
      });

      editCommentId = null;
      commentSubmitBtn.textContent = "댓글 등록";

    } else {
      // 등록
      await fetch(`http://localhost:8080/api/v1/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          postId: Number(postId),
          userId: userId,
          content: content
        }),
      });
    }

  } catch (err) {
    console.error(err);
    alert("댓글 등록 중 오류가 발생했습니다.");
  }

  commentInput.value = "";
  commentSubmitBtn.disabled = true;
  commentSubmitBtn.classList.remove("enabled");

  loadComments();
});

commentList.addEventListener("click", (e) => {
  const target = e.target;

  // 수정
  if (target.classList.contains("edit-btn")) {
    editCommentId = target.dataset.id;
    commentInput.value = target.dataset.content;

    commentSubmitBtn.textContent = "댓글 수정";
    commentSubmitBtn.disabled = false;
    commentSubmitBtn.classList.add("enabled");

    commentInput.focus();
    commentInput.classList.add("highlight");
    setTimeout(() => commentInput.classList.remove("highlight"), 600);

    return;
  }

  // 삭제
  if (target.classList.contains("delete-btn")) {
    openDeleteCommentModal(target.dataset.id);
    return;
  }
});

function openDeleteCommentModal(commentId) {
  modalOverlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");

  modalConfirmBtn.onclick = async () => {
    await fetch(`http://localhost:8080/api/v1/comments/${commentId}`, {
      method: "DELETE",
    });

    closeModal();
    loadComments();
  };
}

function closeModal() {
  modalOverlay.classList.add("hidden");
  deleteModal.classList.add("hidden");
}

modalCancelBtn.addEventListener("click", closeModal);

deleteBtn.addEventListener("click", () => {
  modalOverlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");

  modalConfirmBtn.onclick = async () => {
    await fetch(`http://localhost:8080/api/v1/posts/${postId}`, {
      method: "DELETE",
    });
    window.location.href = "/posts/index.html";
  };
});

likeBtn.addEventListener("click", async () => {
  if (likeCooldown) return;
  likeCooldown = true;
  setTimeout(() => (likeCooldown = false), 500);

  const userId = Number(localStorage.getItem("userId"));

  const res = await fetch(
    `http://localhost:8080/api/v1/likes?userId=${userId}&postId=${postId}`,
    { method: "POST" }
  );

  const result = await res.json();
  const liked = result.liked;

  let count = Number(likeCountEl.textContent.replace(/[^0-9]/g, ""));

  if (liked) {
    likeBtn.classList.add("enabled");
    likeBtn.classList.remove("disabled");
    likeCountEl.textContent = count + 1;
  } else {
    likeBtn.classList.remove("enabled");
    likeBtn.classList.add("disabled");
    likeCountEl.textContent = count - 1;
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