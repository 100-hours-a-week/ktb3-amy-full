const postId = new URLSearchParams(location.search).get("id");

const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const imageInput = document.getElementById("imageInput");
const imagePreview = document.getElementById("imagePreview");
const updateBtn = document.getElementById("updateBtn");

const titleError = document.getElementById("titleError");
const contentError = document.getElementById("contentError");
const backBtn = document.getElementById("backBtn");

let base64Image = null;          // 새로 업로드한 이미지
let originalImageUrl = null;     // 기존 이미지 URL

async function loadPost() {
  try {
    const res = await fetch(`http://localhost:8080/api/v1/posts/${postId}`);
    if (!res.ok) throw new Error("게시글 로드 실패");
    const data = await res.json();

    // 기존 글 정보 주입
    titleInput.value = data.title;
    contentInput.value = data.content;

    if (data.imageUrl) {
      originalImageUrl = data.imageUrl;
      imagePreview.src = data.imageUrl;
      imagePreview.classList.remove("hidden");
    }

    validateForm();
  } catch (err) {
    console.error(err);
    alert("게시글 정보를 불러오는 중 오류가 발생했습니다.");
  }
}

loadPost();

titleInput.addEventListener("input", () => {
  if (titleInput.value.length > 26) {
    titleError.classList.remove("hidden");
  } else {
    titleError.classList.add("hidden");
  }
  validateForm();
});

contentInput.addEventListener("input", () => {
  if (!contentInput.value.trim()) {
    contentError.classList.remove("hidden");
  } else {
    contentError.classList.add("hidden");
  }
  validateForm();
});

imageInput.addEventListener("change", () => {
  const file = imageInput.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    base64Image = e.target.result;
    imagePreview.src = base64Image;
    imagePreview.classList.remove("hidden");
  };
  reader.readAsDataURL(file);
});

function validateForm() {
  const titleValid =
    titleInput.value.trim().length > 0 && titleInput.value.length <= 26;

  const contentValid = contentInput.value.trim().length > 0;

  const formValid = titleValid && contentValid;

  updateBtn.disabled = !formValid;
  updateBtn.classList.toggle("enabled", formValid);

  return formValid;
}

updateBtn.addEventListener("click", async () => {
  if (!validateForm()) return alert("제목과 내용을 올바르게 입력해주세요.");

  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  const body = {
    title,
    content,
    imageBase64: base64Image, // 새 이미지 업로드 시 → Base64
    originalImageUrl: originalImageUrl, 
  };

  try {
    const res = await fetch(`http://localhost:8080/api/v1/posts/${postId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("수정 요청 실패");

    alert("게시글이 성공적으로 수정되었습니다!");
    location.href = `/post/index.html?id=${postId}`;

  } catch (err) {
    console.error(err);
    alert("수정 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
});

backBtn.addEventListener("click", () => {
  history.back();
});