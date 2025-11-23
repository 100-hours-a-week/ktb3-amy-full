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

  // 이메일
  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    valid = false;
  } else {
    emailError.textContent = "";
  }

  // 비밀번호
  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요.";
    valid = false;
  } else {
    passwordError.textContent = "";
  }

  // 버튼 활성화
  loginBtn.disabled = !valid;
  loginBtn.classList.toggle("enabled", valid);

  // 활성화 시 네온 효과
  if (valid) {
    loginBtn.classList.add("neon-glow");
  } else {
    loginBtn.classList.remove("neon-glow");
  }
}

emailInput.addEventListener("input", validateForm);
passwordInput.addEventListener("input", validateForm);

// 로그인 요청
loginBtn.addEventListener("click", async () => {
  if (loginBtn.disabled) return;

  const email = emailInput.value.trim();
  const password = passwordInput.value.trim();

  // 버튼 클릭 애니메이션
  loginBtn.classList.add("active-press");
  setTimeout(() => loginBtn.classList.remove("active-press"), 150);

  try {
    const response = await fetch("http://localhost:8080/api/v1/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();

    if (response.ok && result.message === "login_success") {
      // 저장
      localStorage.setItem("token", result.data.token);
      localStorage.setItem("userId", result.data.id);
      localStorage.setItem("nickname", result.data.nickname);
      localStorage.setItem("profileImageUrl", result.data.profileImageUrl);

      // 버튼 성공 시 네온 색상 변경
      loginBtn.style.background = "#7f6aee";

      // 페이지 이동
      setTimeout(() => {
        window.location.href = "/posts/index.html";
      }, 800);

    } else {
      showToast();
    }

  } catch (e) {
    showToast();
  }
});

// 토스트 메시지
function showToast() {
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

// 초기 상태 검사
validateForm();