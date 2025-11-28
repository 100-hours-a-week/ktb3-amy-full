import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

// 로그인 여부 검증 + userId 가져오기
function getUserIdOrRedirect() {
  const raw = localStorage.getItem("userId");

  if (!raw) {
    alert("로그인이 필요합니다.");
    window.location.href = "/login/index.html";
    return null;
  }

  const id = Number(raw);

  if (!id || id <= 0) {
    alert("로그인 정보가 유효하지 않습니다. 다시 로그인해주세요.");
    window.location.href = "/login/index.html";
    return null; 
  }

  return id;
}

// 로그인 체크
const userId = getUserIdOrRedirect();
if (!userId) throw new Error("로그인되지 않은 상태에서 게시글 작성 페이지 접근");

const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const imageInput = document.getElementById("imageInput");
const previewImage = document.getElementById("previewImage");

const titleError = document.getElementById("titleError");
const submitBtn = document.getElementById("submitBtn");
const submitError = document.getElementById("submitError");

const backBtn = document.getElementById("backBtn");

let base64Image = null;

// 뒤로가기
backBtn.addEventListener("click", () => {
  backBtn.classList.add("active");
  setTimeout(() => {
    window.location.href = "/posts/index.html";
  }, 150);
});

// 제목 검증
titleInput.addEventListener("input", () => {
  const len = titleInput.value.length;
  if (len > 26) titleError.classList.remove("hidden");
  else titleError.classList.add("hidden");

  checkFormValid();
});

// 내용 입력 체크
contentInput.addEventListener("input", checkFormValid);

// 이미지 선택 → base64 변환 + 미리보기 표시
imageInput.addEventListener("change", () => {
  const file = imageInput.files[0];
  if (!file) return;

  if (!file.type.startsWith("image/")) {
    alert("이미지 파일만 업로드 가능합니다.");
    imageInput.value = "";
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    base64Image = reader.result;

    previewImage.src = base64Image;
    previewImage.classList.remove("hidden");
    previewImage.classList.add("show");

    // 선택적으로 fade 효과도 적용 가능
    previewImage.classList.add("fade-in");
    setTimeout(() => previewImage.classList.remove("fade-in"), 500);
  };

  reader.readAsDataURL(file);
});

// 폼 유효성
function checkFormValid() {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  const valid = title && content && title.length <= 26;

  submitBtn.disabled = !valid;
  submitBtn.classList.toggle("enabled", valid);
}

// 게시글 등록
submitBtn.addEventListener("click", async () => {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (!title || !content) {
    submitError.classList.remove("hidden");
    return;
  } else {
    submitError.classList.add("hidden");
  }

  submitBtn.disabled = true;
  submitBtn.textContent = "등록 중...";
  submitBtn.classList.add("loading");

  const body = {
    authorId: userId,
    title,
    content,
    imageBase64: base64Image || null
  };

  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/posts`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    if (!res.ok) throw new Error("등록 실패");

    submitBtn.textContent = "완료!";
    submitBtn.classList.remove("loading");
    submitBtn.classList.add("success");

    setTimeout(() => {
      window.location.href = "/posts/index.html";
    }, 600);

  } catch (err) {
    console.error(err);
    alert("게시글 등록 실패");

    submitBtn.disabled = false;
    submitBtn.textContent = "등록하기";
    submitBtn.classList.remove("loading");
  }
});