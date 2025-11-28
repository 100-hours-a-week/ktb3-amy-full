import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

const userId = localStorage.getItem("userId");

const profileImg = document.getElementById("profileImg");
const profileDropdown = document.getElementById("profileDropdown");
const nicknameInput = document.getElementById("nicknameInput");
const nicknameError = document.getElementById("nicknameError");
const updateBtn = document.getElementById("updateBtn");
const deleteAccountBtn = document.getElementById("deleteAccountBtn");
const modalOverlay = document.getElementById("modalOverlay");
const cancelDelete = document.getElementById("cancelDelete");
const confirmDelete = document.getElementById("confirmDelete");
const toast = document.getElementById("toast");
const backBtn = document.getElementById("backBtn");

const DEFAULT_PROFILE_URL = "/images/default-profile.png";

// 로그인 체크
if (!userId) {
  window.location.href = "/login/index.html";
}

async function loadProfile() {
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/users/me`);

    if (!res.ok) throw new Error("로드 실패");

    const result = await res.json();
    const user = result.data;   

    // 이미지 설정
    let img = user.profileImageUrl || DEFAULT_PROFILE_URL;

    if (img.startsWith("/uploads")) {
      img = `${API_BASE_URL}${img}`;
    }

    profileImg.src = img;
    profileImg.dataset.deleted = "false";

    profileImg.onerror = () => {
      profileImg.onerror = null;
      profileImg.src = DEFAULT_PROFILE_URL;
    };

    // 닉네임 설정
    nicknameInput.value = user.nickname;  

  } catch (err) {
    console.error(err);
    alert("회원 정보를 불러오는 중 문제가 발생했습니다.");
  }
}

loadProfile();

profileImg.addEventListener("click", (e) => {
  e.stopPropagation();
  profileDropdown.classList.toggle("hidden");
});

document.addEventListener("click", () => {
  profileDropdown.classList.add("hidden");
});

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") profileDropdown.classList.add("hidden");
});

document.querySelector("[data-value='change']").addEventListener("click", () => {
  const input = document.createElement("input");
  input.type = "file";
  input.accept = "image/*";
  input.click();

  input.onchange = () => {
    const file = input.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      profileImg.src = e.target.result;
      profileImg.dataset.deleted = "false";
    };

    reader.readAsDataURL(file);
  };

  profileDropdown.classList.add("hidden");
});

// 이미지 삭제
document.querySelector("[data-value='delete']").addEventListener("click", () => {
  profileImg.src = DEFAULT_PROFILE_URL;
  profileImg.dataset.deleted = "true";
  profileDropdown.classList.add("hidden");
});

let debounceTimer = null;

nicknameInput.addEventListener("input", () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(validateNickname, 350);
  disableButton();
});

async function validateNickname() {
  const nickname = nicknameInput.value.trim();

  nicknameError.textContent = "";
  nicknameError.classList.add("hidden");

  if (!nickname) return showError("*닉네임을 입력해주세요.");
  if (nickname.includes(" ")) return showError("*띄어쓰기를 없애주세요.");
  if (nickname.length > 10) return showError("*닉네임은 최대 10자까지 입력 가능합니다.");

  try {
    const res = await fetchWithAuth(
      `${API_BASE_URL}/api/v1/users/exists/nickname?nickname=${nickname}`
    );

    const exists = await res.json();
    if (exists) return showError("*중복된 닉네임입니다.");

  } catch (err) {
    return showError("*닉네임 검증 중 오류가 발생했습니다.");
  }

  enableButton();
  return true;
}

function showError(msg) {
  nicknameError.textContent = msg;
  nicknameError.classList.remove("hidden");
  disableButton();
  return false;
}

function enableButton() {
  updateBtn.disabled = false;
  updateBtn.classList.add("enabled");
}

function disableButton() {
  updateBtn.disabled = true;
  updateBtn.classList.remove("enabled");
}

updateBtn.addEventListener("click", async () => {
  const nickname = nicknameInput.value.trim();
  if (!(await validateNickname())) return;

  const body = {
    nickname,
    deleteImage: profileImg.dataset.deleted === "true",
    profileImageBase64: profileImg.src.startsWith("data:") ? profileImg.src : null,
  };

  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/users/me`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("수정 실패");

    showToast();

  } catch (err) {
    alert("정보 수정 중 오류가 발생했습니다.");
  }
});

function showToast() {
  toast.classList.remove("hidden");
  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.classList.add("hidden"), 300);
  }, 2000);
}

deleteAccountBtn.addEventListener("click", () => {
  modalOverlay.classList.remove("hidden");
});

cancelDelete.addEventListener("click", () => {
  modalOverlay.classList.add("hidden");
});

confirmDelete.addEventListener("click", async () => {
  try {
    await fetchWithAuth(`${API_BASE_URL}/api/v1/users/me`, {
      method: "DELETE",
    });

    localStorage.clear();
    alert("회원 탈퇴가 완료되었습니다.");
    location.href = "/login/index.html";

  } catch (err) {
    alert("회원 탈퇴 중 오류 발생");
  }
});

backBtn.addEventListener("click", () => {
  window.location.href = "/posts/index.html";
});