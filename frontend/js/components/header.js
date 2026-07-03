function initializeHeader() {
  const searchInput = document.querySelector(".search-box input");

  if (searchInput) {
    searchInput.addEventListener("focus", () => {
      searchInput.parentElement.classList.add("focused");
    });

    searchInput.addEventListener("blur", () => {
      searchInput.parentElement.classList.remove("focused");
    });
  }

  const headerButtons = document.querySelectorAll(".header-btn");

  headerButtons.forEach((button) => {
    button.addEventListener("click", () => {
      console.log(`${button.title || "Header Button"} clicked`);
    });
  });
}
