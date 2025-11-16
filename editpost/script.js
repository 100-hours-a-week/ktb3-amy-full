// --- 요소 가져오기 ---
const profileImage = document.getElementById("profileImage");
const dropdown = document.getElementById("dropdown");
const logoutBtn = document.getElementById("logoutBtn");

const nicknameInput = document.getElementById("nicknameInput");
const nicknameError = document.getElementById("nicknameError");
const emailInput = document.getElementById("emailInput");

const saveBtn = document.getElementById("saveBtn");
const deleteBtn = document.getElementById("deleteBtn");

const overlay = document.getElementById("overlay");
const deleteModal = document.getElementById("deleteModal");
const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
const toast = document.getElementById("toast");

const profilePreview = document.getElementById("profilePreview");
const profileInput = document.getElementById("profileInput");

let userId = localStorage.getItem("userId");


// =====================
//   드롭다운 안전 패치
// =====================
if (profileImage) {
  profileImage.addEventListener("click", (e) => {
    e.stopPropagation(); // 버블링 막기
    dropdown.classList.toggle("hidden");
  });
}

document.addEventListener("click", (e) => {
  if (
    !profileImage?.contains(e.target) &&
    !dropdown?.contains(e.target)
  ) {
    dropdown?.classList.add("hidden");
  }
});


// =====================
//   드롭다운 항목 이동
// =====================
document.querySelectorAll(".dropdown-item").forEach(item => {
  item.addEventListener("click", () => {
    const link = item.dataset.link;
    if (link) window.location.href = link;
  });
});


// =====================
//   로그아웃
// =====================
if (logoutBtn) {
  logoutBtn.addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "/login/index.html";
  });
}


// =====================
//   사용자 정보 로드
// =====================
async function loadUser() {
  if (!userId) return;

  const res = await fetch(`http://localhost:8080/api/v1/users/${userId}`);
  const data = await res.json();

  if (!data.data) return;

  emailInput.value = data.data.email;
  nicknameInput.value = data.data.nickname;

  if (data.data.profileImage) {
    profilePreview.src = data.data.profileImage;
    profileImage.src = data.data.profileImage;
  }

  enableSave();
}

loadUser();


// =====================
//   프로필 사진 변경
// =====================
if (profilePreview && profileInput) {
  profilePreview.addEventListener("click", () => profileInput.click());

  profileInput.addEventListener("change", () => {
    const file = profileInput.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        profilePreview.src = reader.result;
        profileImage.src = reader.result;
      };
      reader.readAsDataURL(file);
    }
  });
}


// =====================
//   닉네임 입력 체크
// =====================
nicknameInput?.addEventListener("input", () => {
  const nick = nicknameInput.value.trim();

  if (!nick) {
    nicknameError.textContent = "*닉네임을 입력해주세요.";
    disableSave();
    return;
  }
  if (nick.includes(" ")) {
    nicknameError.textContent = "*띄어쓰기를 없애주세요.";
    disableSave();
    return;
  }
  if (nick.length > 10) {
    nicknameError.textContent = "*닉네임은 최대 10자까지 작성 가능합니다.";
    disableSave();
    return;
  }

  nicknameError.textContent = "";
  enableSave();
});

function disableSave() {
  saveBtn.disabled = true;
  saveBtn.classList.add("disabled");
}

function enableSave() {
  saveBtn.disabled = false;
  saveBtn.classList.remove("disabled");
}


// =====================
//   저장하기
// =====================
saveBtn?.addEventListener("click", async () => {
  const nick = nicknameInput.value.trim();

  // 중복 체크
  const resCheck = await fetch(
    `http://localhost:8080/api/v1/users/check-nickname?nickname=${nick}`
  );
  const check = await resCheck.json();

  if (check.exists) {
    nicknameError.textContent = "*중복된 닉네임입니다.";
    return;
  }

  // 저장(formData)
  const formData = new FormData();
  formData.append("nickname", nick);

  if (profileInput.files[0]) {
    formData.append("profileImage", profileInput.files[0]);
  }

  await fetch(`http://localhost:8080/api/v1/users/${userId}`, {
    method: "PUT",
    body: formData,
  });

  showToast();
});

function showToast() {
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 2000);
}


// =====================
//   회원 탈퇴
// =====================
deleteBtn?.addEventListener("click", () => {
  overlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");
  document.body.style.overflow = "hidden";
});

cancelDeleteBtn?.addEventListener("click", closeModal);

confirmDeleteBtn?.addEventListener("click", async () => {
  await fetch(`http://localhost:8080/api/v1/users/${userId}`, {
    method: "DELETE",
  });

  localStorage.clear();
  closeModal();
  window.location.href = "/login/index.html";
});

function closeModal() {
  overlay.classList.add("hidden");
  deleteModal.classList.add("hidden");
  document.body.style.overflow = "auto";
}
