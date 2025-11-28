import { API_BASE_URL } from "/common/config.js";

const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const signupBtn = document.getElementById("signupBtn");

const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const toast = document.getElementById("toast");

// 입력값 검증
function validateForm() {
  const email = emailInput.value.trim();
  const pw = passwordInput.value.trim();
  let valid = true;

  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    valid = false;
  } else {
    emailError.textContent = "";
  }

  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요.";
    valid = false;
  } else {
    passwordError.textContent = "";
  }

  loginBtn.disabled = !valid;
  loginBtn.classList.toggle("enabled", valid);

  if (valid) loginBtn.classList.add("neon-glow");
  else loginBtn.classList.remove("neon-glow");
}

// 이벤트 연결
emailInput.addEventListener("input", validateForm);
passwordInput.addEventListener("input", validateForm);

// 로그인 요청
loginBtn.addEventListener("click", async () => {
  if (loginBtn.disabled) return;

  const email = emailInput.value.trim();
  const password = passwordInput.value.trim();

  loginBtn.classList.add("active-press");
  setTimeout(() => loginBtn.classList.remove("active-press"), 150);

  try {
    const response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();
    console.log("로그인 응답:", result);

    if (response.ok && result.success === true) {
      const data = result.data;

      // Access & Refresh Token 저장
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);

      // 유저 정보 저장
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("email", data.email);
      localStorage.setItem("nickname", data.nickname);

      // 버튼 이펙트 + 페이지 이동
      loginBtn.style.background = "#7f6aee";
      setTimeout(() => {
        window.location.href = "/posts/index.html";
      }, 800);
    } else {
      showToast();
    }
  } catch (e) {
    console.error("로그인 오류:", e);
    showToast();
  }
});

// 토스트 메시지
function showToast() {
  if (toast.classList.contains("show")) return;
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), 2000);
}

// 회원가입 이동
signupBtn.addEventListener("click", () => {
  signupBtn.style.color = "#aca0eb";
  setTimeout(() => {
    window.location.href = "/signup/index.html";
  }, 300);
});

// 초기 실행
validateForm();