export function initDropdown(triggerElement, dropdownElement) {
  if (!triggerElement || !dropdownElement) return;

  // 버튼 클릭 → 열기/닫기
  triggerElement.addEventListener("click", (e) => {
    e.stopPropagation();
    dropdownElement.classList.toggle("show");
  });

  // 드롭다운 내부 클릭은 닫히지 않게
  dropdownElement.addEventListener("click", (e) => {
    e.stopPropagation();
  });

  // 바깥 클릭 → 닫기
  document.addEventListener("click", (e) => {
    // 클릭한 곳이 드롭다운도 아니고, 트리거도 아니면 닫기
    if (e.target !== triggerElement && !dropdownElement.contains(e.target)) {
      dropdownElement.classList.remove("show");
    }
  });

  // ESC → 닫기
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      dropdownElement.classList.remove("show");
    }
  });
}