function initializeSidebar() {
  const currentPage = window.location.pathname.split("/").pop();

  const menuLinks = document.querySelectorAll(".menu-link");

  menuLinks.forEach((link) => {
    const href = link.getAttribute("href");

    if (href === currentPage) {
      link.classList.add("active");
    } else {
      link.classList.remove("active");
    }
  });
}
