import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_BASE_URL } from "../common/config.js";

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

// 로그인 사용자 검증
const userId = getUserIdOrRedirect();
if (!userId) throw new Error("로그인되지 않은 상태에서 접근 불가");

// 뒤로가기
const backBtn = document.getElementById("backBtn");
if (backBtn) {
  backBtn.addEventListener("click", () => {
    window.location.href = "/editprofile/index.html";
  });
}

const passwordInput = document.getElementById("passwordInput");
const passwordCheckInput = document.getElementById("passwordCheckInput");

const passwordError = document.getElementById("passwordError");
const passwordCheckError = document.getElementById("passwordCheckError");

const updateBtn = document.getElementById("updateBtn");
const toast = document.getElementById("toast");

// 비밀번호 규칙 검증
function validatePassword(pw) {
  const regex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_\-\+=<>?]).{8,20}$/;
  return regex.test(pw);
}

// 실시간 입력 검증
passwordInput.addEventListener("input", validateForm);
passwordCheckInput.addEventListener("input", validateForm);

// 입력 검증 함수
function validateForm() {
  const pw = passwordInput.value.trim();
  const pw2 = passwordCheckInput.value.trim();

  let valid = true;

  // 비밀번호 검증
  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요.";
    passwordError.classList.remove("hidden");
    valid = false;
  } else if (!validatePassword(pw)) {
    passwordError.textContent =
      "*8~20자 / 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.";
    passwordError.classList.remove("hidden");
    valid = false;
  } else {
    passwordError.classList.add("hidden");
  }

  // 비밀번호 확인 검증
  if (!pw2) {
    passwordCheckError.textContent = "*비밀번호를 한 번 더 입력해주세요.";
    passwordCheckError.classList.remove("hidden");
    valid = false;
  } else if (pw !== pw2) {
    passwordCheckError.textContent = "*비밀번호 확인이 일치하지 않습니다.";
    passwordCheckError.classList.remove("hidden");
    valid = false;
  } else {
    passwordCheckError.classList.add("hidden");
  }

  // 버튼 상태 변경
  updateBtn.disabled = !valid;
  updateBtn.classList.toggle("enabled", valid);
}

updateBtn.addEventListener("click", async () => {
  const pw = passwordInput.value.trim();
  const pw2 = passwordCheckInput.value.trim();

  const body = {
    newPassword: pw,
    newPasswordCheck: pw2,
  };

  try {
    const res = await fetchWithAuth(
      `${API_BASE_URL}/api/v1/users/me/password`,   
      {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      }
    );

    if (!res.ok) {
      alert("비밀번호 변경 실패! 다시 시도해주세요.");
      return;
    }

    // 성공 UI
    showToast();

    // UI 초기화
    passwordInput.value = "";
    passwordCheckInput.value = "";
    updateBtn.disabled = true;
    updateBtn.classList.remove("enabled");

    // 프로필 페이지로 이동
    setTimeout(() => {
      window.location.href = "/editprofile/index.html";
    }, 1200);

  } catch (err) {
    console.error(err);
    alert("서버 오류가 발생했습니다.");
  }
});

// 토스트 메시지
function showToast() {
  toast.classList.remove("hidden");
  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.classList.add("hidden"), 300);
  }, 1500);
}