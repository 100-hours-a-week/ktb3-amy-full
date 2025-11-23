const profileImg = document.getElementById("headerProfileImg");
const dropdown = document.getElementById("headerDropdown");

const DEFAULT_PROFILE_URL = "http://localhost:8080/uploads/profile/default-profile.png";

async function loadHeaderProfile() {
  const userId = localStorage.getItem("userId");

  // 로그인 안 되어 있으면 기본 이미지
  if (!userId) {
    profileImg.src = DEFAULT_PROFILE_URL;
    return;
  }

  try {
    const res = await fetch(`http://localhost:8080/api/v1/users/${userId}`);

    // API 실패 시 기본 이미지
    if (!res.ok) {
      profileImg.src = DEFAULT_PROFILE_URL;
      return;
    }

    const data = await res.json();
    let imageUrl = data.profileImageUrl || DEFAULT_PROFILE_URL;

    // 상대경로 → 절대경로 변환
    if (imageUrl.startsWith("/uploads")) {
      imageUrl = "http://localhost:8080" + imageUrl;
    }

    profileImg.src = imageUrl;

    // 이미지 로딩 실패 시 기본 이미지로 fallback
    profileImg.onerror = () => {
      profileImg.src = DEFAULT_PROFILE_URL;
    };

  } catch (err) {
    console.error("프로필 이미지 로드 실패:", err);
    profileImg.src = DEFAULT_PROFILE_URL;
  }
}

loadHeaderProfile();

profileImg.addEventListener("click", (e) => {
  e.stopPropagation();
  dropdown.classList.toggle("show");

  dropdown.classList.add("opening");
  setTimeout(() => dropdown.classList.remove("opening"), 150);
});

dropdown.addEventListener("click", (e) => {
  const action = e.target.dataset.action;
  if (!action) return;

  switch (action) {
    case "edit-profile":
      window.location.href = "/editprofile/index.html";
      break;

    case "edit-password":
      window.location.href = "/editpassword/index.html";
      break;

    case "logout":
      localStorage.clear();
      window.location.href = "/login/index.html";
      break;
  }

  dropdown.classList.remove("show");
});

document.addEventListener("click", () => {
  dropdown.classList.remove("show");
});

// ESC 키로 닫기
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") dropdown.classList.remove("show");
});