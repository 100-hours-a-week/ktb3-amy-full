const backBtn = document.getElementById("backBtn");
const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const imageInput = document.getElementById("imageInput");

const titleError = document.getElementById("titleError");
const contentError = document.getElementById("contentError");

const submitBtn = document.getElementById("submitBtn");

let selectedImage = null;

// 제목 26자 제한
titleInput.addEventListener("input", () => {
  if (titleInput.value.length > 26) {
    titleInput.value = titleInput.value.substring(0, 26);
  }

  validateForm();
});

// 본문 입력 시 유효성 체크
contentInput.addEventListener("input", () => {
  validateForm();
});

// 이미지 업로드
imageInput.addEventListener("change", () => {
  selectedImage = imageInput.files[0] ?? null;
});

// 버튼 활성화 조건 체크
function validateForm() {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (title && content) {
    submitBtn.disabled = false;
    submitBtn.classList.remove("disabled");
  } else {
    submitBtn.disabled = true;
    submitBtn.classList.add("disabled");
  }
}

// 등록 버튼 클릭
submitBtn.addEventListener("click", async () => {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (!title || !content) {
    titleError.textContent = "*제목, 내용을 모두 작성해주세요";
    contentError.textContent = "*제목, 내용을 모두 작성해주세요";
    return;
  }

  titleError.textContent = "";
  contentError.textContent = "";

  // FormData 만들기
  const formData = new FormData();
  formData.append("title", title);
  formData.append("content", content);

  const authorId = localStorage.getItem("userId"); // 저장된 사용자 ID 필요
  formData.append("authorId", authorId);

  if (selectedImage) {
    formData.append("image", selectedImage);
  }

  try {
    const response = await fetch("http://localhost:8080/api/v1/posts", {
      method: "POST",
      body: formData
    });

    const result = await response.json();

    if (result.message === "post_created") {
      submitBtn.textContent = "등록 완료!";
      submitBtn.disabled = true;

      setTimeout(() => {
        window.location.href = "/posts/index.html";
      }, 1500);
    }
  } catch (error) {
    alert("게시글 작성 오류");
    console.error(error);
  }
});

// 뒤로가기
backBtn.addEventListener("click", () => {
  window.history.back();
});
