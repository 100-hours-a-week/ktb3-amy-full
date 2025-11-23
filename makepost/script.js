const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const imageInput = document.getElementById("imageInput");
const previewImage = document.getElementById("previewImage");

const titleError = document.getElementById("titleError");
const submitBtn = document.getElementById("submitBtn");
const submitError = document.getElementById("submitError");

const backBtn = document.getElementById("backBtn");

let base64Image = null;

backBtn.addEventListener("click", () => {
  backBtn.classList.add("active"); // 클릭 효과
  setTimeout(() => {
    window.location.href = "/posts/index.html";
  }, 150);
});

titleInput.addEventListener("input", () => {
  const len = titleInput.value.length;

  // 26자 제한
  if (len > 26) {
    titleError.classList.remove("hidden");
  } else {
    titleError.classList.add("hidden");
  }

  checkFormValid();
});

contentInput.addEventListener("input", checkFormValid);

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

    // 애니메이션 효과
    previewImage.classList.add("fade-in");
    setTimeout(() => previewImage.classList.remove("fade-in"), 500);
  };

  reader.readAsDataURL(file);
});

function checkFormValid() {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  const valid =
    title &&
    content &&
    title.length <= 26;

  submitBtn.disabled = !valid;
  submitBtn.classList.toggle("enabled", valid);
}

submitBtn.addEventListener("click", async () => {
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  // 마지막 방어
  if (!title || !content) {
    submitError.classList.remove("hidden");
    return;
  } else {
    submitError.classList.add("hidden");
  }

  // 중복 제출 방지
  submitBtn.disabled = true;
  submitBtn.textContent = "등록 중...";
  submitBtn.classList.add("loading");

  const userId = localStorage.getItem("userId");

  const data = {
    authorId: Number(userId),
    title,
    content,
    imageUrl: base64Image || null
  };

  try {
    const res = await fetch("http://localhost:8080/api/v1/posts", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    if (!res.ok) throw new Error("등록 실패");

    // 성공 후 부드러운 페이지 이동
    submitBtn.textContent = "완료!";
    submitBtn.classList.remove("loading");
    submitBtn.classList.add("success");

    setTimeout(() => {
      window.location.href = "/posts/index.html";
    }, 600);

  } catch (err) {
    console.error(err);
    alert("게시글 등록에 실패했습니다.");

    submitBtn.disabled = false;
    submitBtn.textContent = "등록하기";
    submitBtn.classList.remove("loading");
  }
});