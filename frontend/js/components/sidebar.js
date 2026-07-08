/* ==========================================================
   SIDEBAR
========================================================== */

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

  loadCurrentUserSidebar().catch(console.error);

  console.log("✓ Sidebar Initialized");
}

/* ==========================================================
   LOAD CURRENT USER
========================================================== */

async function loadCurrentUserSidebar() {
  try {
    const user = await apiCall("/api/users/me");

    const sidebarUsername = document.getElementById("sidebarUsername");
    const sidebarRole = document.getElementById("sidebarRole");
    const sidebarAvatar = document.getElementById("sidebarAvatar");

    if (sidebarUsername) {
      sidebarUsername.textContent = user.name;
    }

    if (sidebarRole) {
      sidebarRole.textContent = user.role === "STAFF" ? "Staff" : "Patient";
    }

    if (sidebarAvatar) {
      if (user.role === "STAFF") {
        sidebarAvatar.src = "assets/avatars/doctor.png";
      } else {
        sidebarAvatar.src =
          user.gender === "FEMALE"
            ? "assets/avatars/patient-female.png"
            : "assets/avatars/patient-male.png";
      }
    }
  } catch (error) {
    console.error(error);
  }
}