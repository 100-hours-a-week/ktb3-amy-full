// 프로필 드롭다운은 profile-wrapper가 있을 때만 활성화
const profileWrapper = document.querySelector(".profile-wrapper");

if (profileWrapper) {
  const profileImage = document.getElementById("profileImage");
  const dropdown = document.getElementById("dropdown");

  profileImage.addEventListener("click", (e) => {
    e.stopPropagation();
    dropdown.classList.toggle("hidden");
  });

  document.addEventListener("click", (e) => {
    if (!e.target.closest(".profile-wrapper")) {
      dropdown.classList.add("hidden");
    }
  });
}
