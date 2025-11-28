import { fetchWithAuth } from "/common/fetchWithAuth.js";
import { API_BASE_URL } from "/common/config.js";
import { initDropdown } from "/common/dropdown.js";

document.addEventListener("DOMContentLoaded", () => {
  const profileImg = document.getElementById("headerProfileImg");
  const dropdown = document.getElementById("headerDropdown");

  if (!profileImg || !dropdown) {
    console.warn("헤더 요소 없음 → header.js 중단");
    return;
  }

  const DEFAULT_PROFILE_URL = "/images/default-profile.png";

  async function loadHeaderProfile() {
    try {
      const res = await fetchWithAuth(`${API_BASE_URL}/api/v1/users/me`);

      if (!res.ok) {
        localStorage.clear();
        window.location.href = "/login/index.html";
        return;
      }

      const result = await res.json();
      const user = result.data;

      let imageUrl = user.profileImageUrl || DEFAULT_PROFILE_URL;

      if (imageUrl.startsWith("/uploads")) {
        imageUrl = `${API_BASE_URL}${imageUrl}`;
      }

      profileImg.src = imageUrl;

      profileImg.onerror = () => {
        profileImg.src = DEFAULT_PROFILE_URL;
      };

    } catch (err) {
      console.error("헤더 프로필 로드 실패:", err);
      profileImg.src = DEFAULT_PROFILE_URL;
    }
  }

  loadHeaderProfile();

  // 드롭다운 열기/닫기
  initDropdown(profileImg, dropdown);

  dropdown.addEventListener("click", (e) => {
    const action = e.target.dataset.action;

    switch (action) {
      case "edit-profile":
        window.location.href = "/editprofile/index.html";
        break;

      case "edit-password":
        window.location.href = "/editpassword/index.html";
        break;

      case "logout":
        localStorage.clear();
        window.location.href = "/login/index.html";
        break;

      default:
        break;
    }
  });
});