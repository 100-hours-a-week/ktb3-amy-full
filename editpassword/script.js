const passwordInput = document.getElementById("passwordInput");
const passwordCheckInput = document.getElementById("passwordCheckInput");

const passwordError = document.getElementById("passwordError");
const passwordCheckError = document.getElementById("passwordCheckError");

const saveBtn = document.getElementById("saveBtn");
const toast = document.getElementById("toast");

let userId = localStorage.getItem("userId");

function validatePassword(pw) {
  const regex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_\-\+=<>?]).{8,20}$/;
  return regex.test(pw);
}

passwordInput.addEventListener("input", validateForm);
passwordCheckInput.addEventListener("input", validateForm);

function validateForm() {
  const pw = passwordInput.value.trim();
  const pw2 = passwordCheckInput.value.trim();

  let valid = true;

  if (!pw) {
    passwordError.textContent = "*비밀번호를 입력해주세요";
    valid = false;
  } 
  else if (!validatePassword(pw)) {
    passwordError.textContent =
      "*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.";
    valid = false;
  } 
  else if (pw2 && pw !== pw2) {
    // 비밀번호는 입력했는데 확인 비밀번호와 다를 때
    passwordError.textContent = "*비밀번호 확인과 다릅니다.";
    valid = false;
  }
  else {
    passwordError.textContent = "";
  }

  if (!pw2) {
    passwordCheckError.textContent = "*비밀번호를 한 번 더 입력해주세요";
    valid = false;
  } 
  else if (pw !== pw2) {
    passwordCheckError.textContent = "*비밀번호와 다릅니다.";
    valid = false;
  }
  else {
    passwordCheckError.textContent = "";
  }

  if (valid) {
    saveBtn.disabled = false;
    saveBtn.classList.remove("disabled");
  } else {
    saveBtn.disabled = true;
    saveBtn.classList.add("disabled");
  }
}

saveBtn.addEventListener("click", async () => {
  const pw = passwordInput.value.trim();

  const body = JSON.stringify({
    password: pw
  });

  try {
    await fetch(`http://localhost:8080/api/v1/users/${userId}/password`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body
    });

    showToast();

  } catch (err) {
    alert("비밀번호 수정 중 오류가 발생했습니다.");
  }
});

function showToast() {
  toast.classList.remove("hidden");

  setTimeout(() => {
    toast.classList.add("hidden");
  }, 2000);
}
