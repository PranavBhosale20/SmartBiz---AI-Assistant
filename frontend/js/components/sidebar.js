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

  /* ==========================================================
     LOAD USER
  ========================================================== */

  const sidebarUsername = document.getElementById("sidebarUsername");
  const sidebarRole = document.getElementById("sidebarRole");
  const sidebarAvatar = document.getElementById("sidebarAvatar");

  if (sidebarUsername) {
    sidebarUsername.textContent = getFullName() || getUsername() || "Guest";
  }

  if (sidebarRole) {
    const role = getRole();

    sidebarRole.textContent = role === "STAFF" ? "Staff" : "Patient";
  }

  if (sidebarAvatar) {
    if (getRole() === "STAFF") {
      sidebarAvatar.src = "assets/avatars/doctor.png";
    } else {
      sidebarAvatar.src =
        getGender() === "FEMALE"
          ? "assets/avatars/patient-female.png"
          : "assets/avatars/patient-male.png";
    }
  }
}

document.addEventListener("DOMContentLoaded", initializeSidebar);
