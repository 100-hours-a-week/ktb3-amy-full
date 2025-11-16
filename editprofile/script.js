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

profileImage.addEventListener("click", () => {
  dropdown.classList.toggle("hidden");
});

document.addEventListener("click", (e) => {
  if (!e.target.closest(".profile-wrapper")) {
    dropdown.classList.add("hidden");
  }
});

// 드롭다운 이동
document.querySelectorAll(".dropdown-item").forEach(item => {
  item.addEventListener("click", () => {
    const link = item.dataset.link;
    if (link) window.location.href = link;
  });
});

// 로그아웃
logoutBtn.addEventListener("click", () => {
  localStorage.clear();
  window.location.href = "/login/index.html";
});

async function loadUser() {
  const res = await fetch(`http://localhost:8080/api/v1/users/${userId}`);
  const user = await res.json();

  emailInput.value = user.data.email;
  nicknameInput.value = user.data.nickname;

  if (user.data.profileImage) {
    profilePreview.src = user.data.profileImage;
    profileImage.src = user.data.profileImage;
  }
}

loadUser();

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

nicknameInput.addEventListener("input", () => {
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

saveBtn.addEventListener("click", async () => {
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

  // 저장
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

  setTimeout(() => {
    toast.classList.add("hidden");
  }, 2000);
}

deleteBtn.addEventListener("click", () => {
  overlay.classList.remove("hidden");
  deleteModal.classList.remove("hidden");
  document.body.style.overflow = "hidden";
});

cancelDeleteBtn.addEventListener("click", closeModal);

async function confirmDelete() {
  await fetch(`http://localhost:8080/api/v1/users/${userId}`, {
    method: "DELETE"
  });

  localStorage.clear();
  closeModal();
  window.location.href = "/login/index.html";
}

confirmDeleteBtn.addEventListener("click", confirmDelete);

function closeModal() {
  overlay.classList.add("hidden");
  deleteModal.classList.add("hidden");
  document.body.style.overflow = "auto";
}
