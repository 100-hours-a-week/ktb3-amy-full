// 프로필 이미지 관련 요소
const profileInput = document.getElementById("profileInput");
const profilePreview = document.getElementById("profilePreview");
const profileError = document.getElementById("profileError");

// 입력 관련 요소
const emailInput = document.getElementById("emailInput");
const passwordInput = document.getElementById("passwordInput");
const passwordCheckInput = document.getElementById("passwordCheckInput");
const nicknameInput = document.getElementById("nicknameInput");

// 에러 표시 요소
const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const passwordCheckError = document.getElementById("passwordCheckError");
const nicknameError = document.getElementById("nicknameError");

// 버튼
const signupBtn = document.getElementById("signupBtn");
const loginLink = document.getElementById("loginLink");

let profileImageFile = null;

// -------------------------------------
// 유효성 검사 함수
// -------------------------------------
function validateEmail(email) {
  return /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);
}

function validatePassword(pw) {
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(pw);
}

function validateNickname(nick) {
  if (!nick) return false;
  if (nick.includes(" ")) return false;
  return nick.length <= 10;
}

// -------------------------------------
// 전체 폼 검증 → 버튼 활성화/비활성화
// -------------------------------------
function checkFormValid() {
  if (
    profileImageFile &&
    validateEmail(emailInput.value.trim()) &&
    validatePassword(passwordInput.value.trim()) &&
    passwordInput.value.trim() === passwordCheckInput.value.trim() &&
    validateNickname(nicknameInput.value.trim()) &&
    !emailError.textContent &&
    !nicknameError.textContent
  ) {
    signupBtn.disabled = false;
    signupBtn.classList.remove("disabled");
  } else {
    signupBtn.disabled = true;
    signupBtn.classList.add("disabled");
  }
}

// -------------------------------------
// 프로필 이미지 업로드
// -------------------------------------
profileInput.addEventListener("change", () => {
  const file = profileInput.files[0];

  if (!file) {
    profileImageFile = null;
    profilePreview.src = "./default-profile.png";
    profileError.textContent = "*프로필 사진을 추가해주세요.";
    checkFormValid();
    return;
  }

  profileImageFile = file;

  const reader = new FileReader();
  reader.onload = () => {
    profilePreview.src = reader.result;
  };
  reader.readAsDataURL(file);

  profileError.textContent = "";
  checkFormValid();
});

// -------------------------------------
// 이메일 입력 후 blur
// -------------------------------------
emailInput.addEventListener("blur", async () => {
  const email = emailInput.value.trim();

  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    checkFormValid();
    return;
  }

  if (!validateEmail(email)) {
    emailError.textContent =
      "*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)";
    checkFormValid();
    return;
  }

  // 이메일 중복 검사
  try {
    const res = await fetch(
      `http://localhost:8080/api/v1/users/check-email?email=${email}`
    );
    const data = await res.json();

    if (data.exists) {
      emailError.textContent = "*중복된 이메일 입니다.";
    } else {
      emailError.textContent = "";
    }
  } catch (e) {
    emailError.textContent = "*이메일 확인 중 오류 발생";
  }

  checkFormValid();
});

// -------------------------------------
// 비밀번호 blur
// -------------------------------------
passwordInput.addEventListener("blur", () => {
  const pw = passwordInput.value.trim();
  const pwCheck = passwordCheckInput.value.trim();

  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요";
  } else if (!validatePassword(pw)) {
    passwordError.textContent =
      "*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.";
  } else if (pwCheck && pw !== pwCheck) {
    passwordError.textContent = "*비밀번호가 다릅니다.";
    passwordCheckError.textContent = "*비밀번호가 다릅니다.";
  } else {
    passwordError.textContent = "";
    if (pw === pwCheck) passwordCheckError.textContent = "";
  }

  checkFormValid();
});

// -------------------------------------
// 비밀번호 확인 blur
// -------------------------------------
passwordCheckInput.addEventListener("blur", () => {
  const pw = passwordInput.value.trim();
  const pwCheck = passwordCheckInput.value.trim();

  if (!pwCheck) {
    passwordCheckError.textContent = "*비밀번호를 한 번 더 입력해주세요";
  } else if (pw !== pwCheck) {
    passwordCheckError.textContent = "*비밀번호가 다릅니다.";
    if (pw) passwordError.textContent = "*비밀번호가 다릅니다.";
  } else {
    passwordCheckError.textContent = "";
    if (validatePassword(pw)) passwordError.textContent = "";
  }

  checkFormValid();
});

// -------------------------------------
// 닉네임 중복 검사
// -------------------------------------
nicknameInput.addEventListener("blur", async () => {
  const nick = nicknameInput.value.trim();

  if (!nick) {
    nicknameError.textContent = "*닉네임을 입력해주세요";
    checkFormValid();
    return;
  }

  if (nick.includes(" ")) {
    nicknameError.textContent = "*띄어쓰기를 없애주세요";
    checkFormValid();
    return;
  }

  if (nick.length > 10) {
    nicknameError.textContent = "*닉네임은 최대 10자까지 작성 가능합니다.";
    checkFormValid();
    return;
  }

  try {
    const res = await fetch(
      `http://localhost:8080/api/v1/users/check-nickname?nickname=${nick}`
    );
    const data = await res.json();

    if (data.exists) {
      nicknameError.textContent = "*중복된 닉네임입니다.";
    } else {
      nicknameError.textContent = "";
    }
  } catch (e) {
    nicknameError.textContent = "*닉네임 확인 중 오류 발생";
  }

  checkFormValid();
});

// -------------------------------------
// 최종 회원가입
// -------------------------------------
signupBtn.addEventListener("click", async () => {
  if (!profileImageFile) {
    profileError.textContent = "*프로필 사진을 추가해주세요.";
    return;
  }

  const formData = new FormData();
  formData.append("email", emailInput.value.trim());
  formData.append("password", passwordInput.value.trim());
  formData.append("nickname", nicknameInput.value.trim());
  formData.append("profileImage", profileImageFile);

  try {
    const response = await fetch("http://localhost:8080/api/v1/users/signup", {
      method: "POST",
      body: formData,
    });

    const result = await response.json();

    if (result.message === "signup_success") {
      signupBtn.textContent = "가입 완료!";
      signupBtn.disabled = true;

      setTimeout(() => {
        window.location.href = "/login/index.html";
      }, 2000);
    }
  } catch (e) {
    alert("회원가입 중 오류 발생");
  }
});

// -------------------------------------
// 로그인 이동 버튼
// -------------------------------------
loginLink.addEventListener("click", () => {
  window.location.href = "/login/index.html";
});
