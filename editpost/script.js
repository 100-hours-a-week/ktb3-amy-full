import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

const postId = new URLSearchParams(location.search).get("id");

// 로그인 여부 확인
const userId = localStorage.getItem("userId");
if (!userId) {
  alert("로그인이 필요합니다.");
  window.location.href = "/login/index.html";
}

const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const imageInput = document.getElementById("imageInput");
const imagePreview = document.getElementById("imagePreview");
const updateBtn = document.getElementById("updateBtn");

const titleError = document.getElementById("titleError");
const contentError = document.getElementById("contentError");
const backBtn = document.getElementById("backBtn");

let base64Image = null;
let originalImageUrl = null;

// 게시글 로딩
async function loadPost() {
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/posts/${postId}`);

    if (!res.ok) throw new Error("게시글 로드 실패");

    const data = await res.json();

    titleInput.value = data.title;
    contentInput.value = data.content;

    if (data.imageUrl) {
      let img = data.imageUrl;

      // 절대 경로로 변환
      if (img.startsWith("/uploads")) {
        img = `${API_BASE_URL}${img}`;
      }

      originalImageUrl = img;
      imagePreview.src = img;
      imagePreview.classList.remove("hidden");
    }

    validateForm();

  } catch (err) {
    console.error(err);
    alert("게시글 정보를 불러오는 데 실패했습니다.");
  }
}

loadPost();

// 제목 검증
titleInput.addEventListener("input", () => {
  if (titleInput.value.length > 26) {
    titleError.classList.remove("hidden");
  } else {
    titleError.classList.add("hidden");
  }
  validateForm();
});

// 내용 검증
contentInput.addEventListener("input", () => {
  if (!contentInput.value.trim()) {
    contentError.classList.remove("hidden");
  } else {
    contentError.classList.add("hidden");
  }
  validateForm();
});

// 이미지 base64 변환
imageInput.addEventListener("change", () => {
  const file = imageInput.files[0];
  if (!file) return;

  if (!file.type.startsWith("image/")) {
    alert("이미지 파일만 업로드 가능합니다.");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    base64Image = e.target.result;
    imagePreview.src = base64Image;
    imagePreview.classList.remove("hidden");
  };

  reader.readAsDataURL(file);
});

// 입력 유효성 검사
function validateForm() {
  const titleValid =
    titleInput.value.trim().length > 0 && titleInput.value.length <= 26;
  const contentValid = contentInput.value.trim().length > 0;

  const valid = titleValid && contentValid;

  updateBtn.disabled = !valid;
  updateBtn.classList.toggle("enabled", valid);

  return valid;
}

// 게시글 수정 요청
updateBtn.addEventListener("click", async () => {
  if (!validateForm()) {
    alert("제목과 내용을 올바르게 입력해주세요.");
    return;
  }

  const body = {
    title: titleInput.value.trim(),
    content: contentInput.value.trim(),
    imageBase64: base64Image || null,
    originalImageUrl: originalImageUrl || null,
  };

  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/posts/${postId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" }, 
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("수정 요청 실패");

    alert("게시글이 성공적으로 수정되었습니다!");
    window.location.href = `/post/index.html?id=${postId}`;

  } catch (err) {
    console.error(err);
    alert("수정 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
});

// 뒤로가기
backBtn.addEventListener("click", () => {
  history.back();
});