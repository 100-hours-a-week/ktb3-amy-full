const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const signupBtn = document.getElementById("signupBtn");

const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");

// 이메일 형식 검증
function validateEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

// 비밀번호 규칙 검증
function validatePassword(password) {
  const regex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/;
  return regex.test(password);
}

// 로그인 버튼 클릭 이벤트
loginBtn.addEventListener("click", async () => {
  const email = emailInput.value.trim();
  const password = passwordInput.value.trim();

  let valid = true;

  // 이메일 검증
  if (!email || !validateEmail(email)) {
    emailError.textContent =
      "*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)";
    valid = false;
  } else {
    emailError.textContent = "";
  }

  // 비밀번호 검증
  if (!password) {
    passwordError.textContent = "*비밀번호를 입력해주세요";
    valid = false;
  } else if (!validatePassword(password)) {
    passwordError.textContent =
      "*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 최소 1개 포함해야 합니다.";
    valid = false;
  } else {
    passwordError.textContent = "";
  }

  if (!valid) return;

  try {
    const response = await fetch("http://localhost:8080/api/v1/users/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (data.message !== "login_success") {
      passwordError.textContent = "*아이디 또는 비밀번호를 확인해주세요";
      return;
    }

    // 성공 → token 저장
    localStorage.setItem("token", data.data.token);

    loginBtn.disabled = true;
    loginBtn.textContent = "로그인 성공!";

    // 3초 후 이동
    setTimeout(() => {
      window.location.href = "/posts/index.html";
    }, 3000);

  } catch (error) {
    passwordError.textContent = "*아이디 또는 비밀번호를 확인해주세요";
  }
});

// 회원가입 페이지 이동
signupBtn.addEventListener("click", () => {
  signupBtn.textContent = "이동 중...";
  setTimeout(() => {
    window.location.href = "/signup/index.html";
  }, 3000);
});
