const profileInput = document.getElementById("profileInput");
const profileCircle = document.getElementById("profileCircle");
const profilePreview = document.getElementById("profilePreview");
const profileIcon = document.getElementById("profileIcon");
const profileError = document.getElementById("profileError");

const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const passwordCheckInput = document.getElementById("passwordCheck");
const nicknameInput = document.getElementById("nickname");

const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const passwordCheckError = document.getElementById("passwordCheckError");
const nicknameError = document.getElementById("nicknameError");

const signupBtn = document.getElementById("signupBtn");
const goLoginBtn = document.getElementById("goLoginBtn");
const toast = document.getElementById("toast");

let profileBase64 = null;

// API 호출 과다 방지용 debounce
let emailTimer = null;
let nicknameTimer = null;

profileCircle.addEventListener("click", () => {
  profileCircle.classList.add("active");
  setTimeout(() => profileCircle.classList.remove("active"), 150);
  profileInput.click();
});

profileInput.addEventListener("change", () => {
  const file = profileInput.files[0];

  if (!file) {
    profileBase64 = null;
    profilePreview.classList.add("hidden");
    profileIcon.classList.remove("hidden");
    profileError.textContent = "*프로필 사진을 추가해주세요.";
    validateForm();
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    profileBase64 = reader.result;
    profilePreview.src = profileBase64;
    profilePreview.classList.remove("hidden");
    profileIcon.classList.add("hidden");
    profileError.textContent = "";
    validateForm();
  };
  reader.readAsDataURL(file);
});

function validateEmail(email) {
  const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
  return regex.test(email);
}

function validatePassword(pw) {
  const regex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_\-\+=<>?]).{8,20}$/;
  return regex.test(pw);
}

function validateNickname(nick) {
  if (!nick) return false;
  if (nick.includes(" ")) return false;
  if (nick.length > 10) return false;
  return true;
}

async function checkEmailDuplicate(email) {
  const res = await fetch(
    `http://localhost:8080/api/v1/users/exists/email?email=${email}`
  );
  return await res.json();
}

async function checkNicknameDuplicate(nickname) {
  const res = await fetch(
    `http://localhost:8080/api/v1/users/exists/nickname?nickname=${nickname}`
  );
  return await res.json();
}

async function validateForm() {
  let valid = true;

  // 프로필
  if (!profileBase64) {
    profileError.textContent = "*프로필 사진을 추가해주세요.";
    valid = false;
  } else {
    profileError.textContent = "";
  }

  // 이메일
  const email = emailInput.value.trim();
  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    valid = false;
  } else if (!validateEmail(email)) {
    emailError.textContent =
      "*올바른 이메일 형식으로 입력해주세요. (예: example@example.com)";
    valid = false;
  } else {
    // Debounce 후 중복 체크
    clearTimeout(emailTimer);
    emailTimer = setTimeout(async () => {
      if (await checkEmailDuplicate(email)) {
        emailError.textContent = "*중복된 이메일입니다.";
        signupBtn.disabled = true;
        signupBtn.classList.remove("enabled");
      }
    }, 350);
    emailError.textContent = "";
  }

  // 비밀번호
  const pw = passwordInput.value.trim();
  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요.";
    valid = false;
  } else if (!validatePassword(pw)) {
    passwordError.textContent =
      "*8~20자, 대소문자/숫자/특수문자 최소 1개씩 포함";
    valid = false;
  } else {
    passwordError.textContent = "";
  }

  // 비밀번호 확인
  const pw2 = passwordCheckInput.value.trim();
  if (!pw2) {
    passwordCheckError.textContent = "*비밀번호를 다시 입력해주세요.";
    valid = false;
  } else if (pw !== pw2) {
    passwordCheckError.textContent = "*비밀번호가 일치하지 않습니다.";
    valid = false;
  } else {
    passwordCheckError.textContent = "";
  }

  // 닉네임
  const nick = nicknameInput.value.trim();
  if (!validateNickname(nick)) {
    nicknameError.textContent =
      "*닉네임은 공백 없이 최대 10자까지 가능합니다.";
    valid = false;
  } else {
    // debounce 중복 체크
    clearTimeout(nicknameTimer);
    nicknameTimer = setTimeout(async () => {
      if (await checkNicknameDuplicate(nick)) {
        nicknameError.textContent = "*중복된 닉네임입니다.";
        signupBtn.disabled = true;
        signupBtn.classList.remove("enabled");
      }
    }, 350);
    nicknameError.textContent = "";
  }

  // 버튼 활성화
  signupBtn.disabled = !valid;
  signupBtn.classList.toggle("enabled", valid);
}

[emailInput, passwordInput, passwordCheckInput, nicknameInput].forEach(
  (input) => {
    input.addEventListener("input", validateForm);
    input.addEventListener("blur", validateForm);
  }
);

profileInput.addEventListener("change", validateForm);

signupBtn.addEventListener("click", async () => {
  if (signupBtn.disabled) return;

  signupBtn.textContent = "처리 중...";
  signupBtn.classList.add("loading");

  const body = {
    email: emailInput.value.trim(),
    password: passwordInput.value.trim(),
    passwordCheck: passwordCheckInput.value.trim(),
    nickname: nicknameInput.value.trim(),
    profileImageBase64: profileBase64,
  };

  try {
    const res = await fetch("http://localhost:8080/api/v1/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      signupBtn.textContent = "완료!";
      setTimeout(() => {
        window.location.href = "/login/index.html";
      }, 1000);
    } else {
      showToast();
      signupBtn.textContent = "회원가입";
    }
  } catch (e) {
    showToast();
    signupBtn.textContent = "회원가입";
  }

  signupBtn.classList.remove("loading");
});

goLoginBtn.addEventListener("click", () => {
  goLoginBtn.style.color = "#bca4ff";
  setTimeout(() => {
    window.location.href = "/login/index.html";
  }, 300);
});

function showToast() {
  toast.classList.add("show");
  setTimeout(() => toast.classList.remove("show"), 2200);
}