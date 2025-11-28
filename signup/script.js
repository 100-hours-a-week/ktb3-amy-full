import { API_BASE_URL } from "../common/config.js";

const profileInput = document.getElementById("profileInput");
const profileCircle = document.getElementById("profileCircle");
const profilePreview = document.getElementById("profilePreview");
const profileIcon = document.getElementById("profileIcon");

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
let emailExists = false;
let nicknameExists = false;

let emailTimer = null;
let nicknameTimer = null;

profileCircle.addEventListener("click", () => {
  profileInput.click();
});

profileInput.addEventListener("change", () => {
  const file = profileInput.files[0];

  if (!file) {
    profileBase64 = null;
    profilePreview.classList.add("hidden");
    profileIcon.classList.remove("hidden");
    updateSubmitState();
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    profileBase64 = reader.result;
    profilePreview.src = profileBase64;
    profilePreview.classList.remove("hidden");
    profileIcon.classList.add("hidden");
    updateSubmitState();
  };
  reader.readAsDataURL(file);
});

function validateEmail(email) {
  return /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/.test(email);
}

function validatePassword(pw) {
  return /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_\-\+=<>?]).{8,20}$/.test(pw);
}

function validateNickname(nick) {
  return nick && !nick.includes(" ") && nick.length <= 10;
}

async function checkEmailDuplicate(email) {
  const res = await fetch(`${API_BASE_URL}/api/v1/users/exists/email?email=${email}`);
  return res.ok ? await res.json() : false;
}

async function checkNicknameDuplicate(nickname) {
  const res = await fetch(`${API_BASE_URL}/api/v1/users/exists/nickname?nickname=${nickname}`);
  return res.ok ? await res.json() : false;
}

async function validateForm() {
  let valid = true;

  // email
  const email = emailInput.value.trim();
  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    valid = false;
  } else if (!validateEmail(email)) {
    emailError.textContent = "*올바른 이메일 형식으로 입력해주세요.";
    valid = false;
  } else {
    emailError.textContent = "";
  }

  // password
  const pw = passwordInput.value.trim();
  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요.";
    valid = false;
  } else if (!validatePassword(pw)) {
    passwordError.textContent = "*8~20자, 대소문자/숫자/특수문자 최소 1개 포함";
    valid = false;
  } else {
    passwordError.textContent = "";
  }

  // pw check
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

  // nickname
  const nick = nicknameInput.value.trim();
  if (!validateNickname(nick)) {
    nicknameError.textContent = "*닉네임은 공백 없이 최대 10자까지 가능합니다.";
    valid = false;
  } else {
    nicknameError.textContent = "";
  }

  if (emailExists || nicknameExists) valid = false;

  return valid;
}

async function updateSubmitState() {
  const ok = await validateForm();
  signupBtn.disabled = !ok;
  signupBtn.classList.toggle("enabled", ok);
}

emailInput.addEventListener("input", () => {
  clearTimeout(emailTimer);
  emailTimer = setTimeout(async () => {
    const email = emailInput.value.trim();
    if (validateEmail(email)) {
      emailExists = await checkEmailDuplicate(email);
      emailError.textContent = emailExists ? "*중복된 이메일입니다." : "";
    }
    updateSubmitState();
  }, 350);
});

nicknameInput.addEventListener("input", () => {
  clearTimeout(nicknameTimer);
  nicknameTimer = setTimeout(async () => {
    const nick = nicknameInput.value.trim();
    if (validateNickname(nick)) {
      nicknameExists = await checkNicknameDuplicate(nick);
      nicknameError.textContent = nicknameExists ? "*중복된 닉네임입니다." : "";
    }
    updateSubmitState();
  }, 350);
});

[passwordInput, passwordCheckInput].forEach((el) =>
  el.addEventListener("input", updateSubmitState)
);

profileInput.addEventListener("change", updateSubmitState);

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
    const res = await fetch(`${API_BASE_URL}/api/v1/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      signupBtn.textContent = "완료!";
      setTimeout(() => (window.location.href = "/login/index.html"), 1000);
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
