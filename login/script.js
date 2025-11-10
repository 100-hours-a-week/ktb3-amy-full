const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const signupBtn = document.getElementById("signupBtn");
const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");

// 이메일 유효성 검사 (공백X, @와 . 포함된 형식)
const emailValidate = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
// 비밀번호 유효성 검사 (8~20자, 대소문자+특수문자 최소 1개 포함)
const passwordValidate = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\W).{8,20}$/;

// 입력 시마다 유효성 검사
emailInput.addEventListener("input", validate);
passwordInput.addEventListener("input", validate);

// 입력값 유효성 검사 함수
function validate() {
    let valid = true;

    // 이메일 검사
    if (!emailInput.value) {
        emailError.textContent = "이메일을 입력해주세요.";
        valid = false;
    } else if (!emailValidate.test(emailInput.value)) {
        emailError.textContent = "올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)";
        valid = false;
    } else {
        emailError.textContent = "";
    }

    // 비밀번호 검사
    if (!passwordInput.value) {
        passwordError.textContent = "비밀번호를 입력해주세요";
        valid = false;
    } else if (!passwordValidate.test(passwordInput.value)) {
        passwordError.textContent = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
        valid = false;
    } else {
        passwordError.textContent = "";
    }

    // 버튼 색상 변경
    if (valid) {
        loginBtn.classList.add("active");
    } else {
        loginBtn.classList.remove("active");
    }
}

// 로그인 클릭 이벤트
loginBtn.addEventListener("click", async () => {
    const email = emailInput.value;
    const password = passwordInput.value;

    // 유효성 검사 통과 확인
    if (!emailValidate.test(email) || !passwordValidate.test(password)) {
        alert("아이디 또는 비밀번호를 확인해주세요");
        return;
    }

    // 버튼 색상 변경 -> 3초 후 페이지 이동
    loginBtn.classList.add("active");

    try {
        const response = await fetch("http://localhost:8080/api/v1/users/login", {
            method: "POST",
            // 요청 헤더: JSON 형식으로 전송
            headers: {"Content-Type": "application/json"},
            // 요청 본문(body): 이메일과 비밀번호 JSON 문자열로 전송
            body: JSON.stringify({email, password}),
        });

        // 서버에서 반환한 응답(JSON)을 파싱하여 data 변수에 저장
        const data = await response.json();

        if (response.ok && data.message === "login_success") {
            loginBtn.textContent = "로그인 성공!";
            // 3초 후 페이지 이동 (게시글 목록 페이지로)
            setTimeout(() => {
                window.location.href = "/posts.html"; // 게시글 목록 페이지로 이동
            }, 3000);
        } else {
            alert("아이디 또는 비밀번호를 확인해주세요.");
        }
    } catch (error) {
        alert("서버 연결 오류가 발생했습니다.");
    }
});

// 회원가입 버튼 클릭 시 이동
signupBtn.addEventListener("click", () => {
    window.location.href = "/signup/index.html";
});