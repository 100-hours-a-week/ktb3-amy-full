const profileInput = document.getElementById("profileInput");
const profilePreview = document.getElementById("profilePreview");
const profileError = document.getElementById("profileError");
const backBtn = document.getElementById("backBtn");

const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const passwordConfirmInput = document.getElementById("passwordConfirm");
const nicknameInput = document.getElementById("nickname");
const signupBtn = document.getElementById("signupBtn");
const loginBtn = document.getElementById("loginBtn");

const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const passwordConfirmError = document.getElementById("passwordConfirmError");
const nicknameError = document.getElementById("nicknameError");

let profileImageBase64 = null;

// 정규식
const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
const pwRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*\W).{8,20}$/;
const nicknameRegex = /^[^\s]{1,10}$/;

// 뒤로가기 버튼
backBtn.addEventListener("click", () => {
  window.location.href = "/login/index.html";
});

// 프로필 업로드
profileInput.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (!file) {
    profilePreview.innerHTML = "+";
    profileError.textContent = "*프로필 사진을 추가해주세요.";
    profileImageBase64 = null;
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    profilePreview.innerHTML = `<img src="${reader.result}" />`;
    profileError.textContent = "";
    profileImageBase64 = reader.result;
  };
  reader.readAsDataURL(file);
});

// 이메일 검증
emailInput.addEventListener("blur", async () => {
  const email = emailInput.value.trim();

  if (!email) {
    emailError.textContent = "*이메일을 입력해주세요.";
    return;
  }

  if (!emailRegex.test(email)) {
    emailError.textContent = "*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)";
    return;
  }

  // 중복 이메일 체크
  try {
    const res = await fetch("http://localhost:8080/api/v1/users/check-email?email=" + email);
    const data = await res.json();
    if (data.exists) {
      emailError.textContent = "*중복된 이메일 입니다.";
    } else {
      emailError.textContent = "";
    }
  } catch (err) {
    console.error(err);
  }
});

// 비밀번호 검증
passwordInput.addEventListener("blur", () => {
  const pw = passwordInput.value.trim();
  const pwc = passwordConfirmInput.value.trim();

  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요";
  } else if (!pwRegex.test(pw)) {
    passwordError.textContent = "*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.";
  } else {
    passwordError.textContent = "";
  }

  if (pwc && pw !== pwc) {
    passwordConfirmError.textContent = "*비밀번호가 다릅니다.";
  } else if (pwc && pw === pwc) {
    passwordConfirmError.textContent = "";
  }
});

passwordConfirmInput.addEventListener("blur", () => {
  const pw = passwordInput.value.trim();
  const pwc = passwordConfirmInput.value.trim();

  if (!pwc) {
    passwordConfirmError.textContent = "*비밀번호를 한 번 더 입력해주세요";
  } else if (pw !== pwc) {
    passwordConfirmError.textContent = "*비밀번호가 다릅니다.";
  } else {
    passwordConfirmError.textContent = "";
  }
});

// 닉네임 검증
nicknameInput.addEventListener("blur", async () => {
  const nick = nicknameInput.value.trim();

  if (!nick) {
    nicknameError.textContent = "*닉네임을 입력해주세요.";
    return;
  }
  if (nick.includes(" ")) {
    nicknameError.textContent = "*띄어쓰기를 없애주세요";
    return;
  }
  if (nick.length > 10) {
    nicknameError.textContent = "*닉네임은 최대 10자까지 작성 가능합니다.";
    return;
  }

  // 닉네임 중복 검사
  try {
    const res = await fetch("http://localhost:8080/api/v1/users/check-nickname?nickname=" + nick);
    const data = await res.json();
    if (data.exists) {
      nicknameError.textContent = "*중복된 닉네임 입니다.";
    } else {
      nicknameError.textContent = "";
    }
  } catch (err) {
    console.error(err);
  }
});

// 버튼 활성화
function checkFormValid() {
  const allValid =
    profileImageBase64 &&
    emailRegex.test(emailInput.value) &&
    pwRegex.test(passwordInput.value) &&
    passwordInput.value === passwordConfirmInput.value &&
    nicknameRegex.test(nicknameInput.value);

  if (allValid) {
    signupBtn.classList.add("active");
  } else {
    signupBtn.classList.remove("active");
  }
}

document.querySelectorAll("input").forEach((input) => {
  input.addEventListener("input", checkFormValid);
});

// 회원가입 요청
signupBtn.addEventListener("click", async () => {
  if (!signupBtn.classList.contains("active")) return;

  const payload = {
    email: emailInput.value,
    password: passwordInput.value,
    nickname: nicknameInput.value,
    profile_image: profileImageBase64
  };

  try {
    const res = await fetch("http://localhost:8080/api/v1/users", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(payload),
    });

    const data = await res.json();

    if (res.ok && data.message === "register_success") {
      alert("회원가입이 완료되었습니다!");
      window.location.href = "/login/index.html";
    } else {
      alert("입력값을 다시 확인해주세요.");
    }
  } catch (err) {
    alert("서버 오류가 발생했습니다.");
  }
});

// 로그인 버튼 클릭 시 이동
loginBtn.addEventListener("click", () => {
  window.location.href = "/login/index.html";
});