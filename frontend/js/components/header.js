/* ==========================================================
   HEADER
========================================================== */

function initializeHeader() {
  /* ======================================================
     SEARCH
  ====================================================== */

  const searchInput = document.querySelector(".search-box input");

  if (searchInput) {
    searchInput.addEventListener("focus", () => {
      searchInput.parentElement.classList.add("focused");
    });

    searchInput.addEventListener("blur", () => {
      searchInput.parentElement.classList.remove("focused");
    });
  }

  loadCurrentUserHeader().catch(console.error);
  /* ======================================================
     LOAD USER INFO
  ====================================================== */

  const role = getRole();

  /* ======================================================
     PROFILE DROPDOWN
  ====================================================== */

  const profileToggle = document.getElementById("profileToggle");
  const profileMenu = document.getElementById("profileMenu");

  if (profileToggle && profileMenu) {
    profileToggle.addEventListener("click", (event) => {
      event.stopPropagation();

      profileMenu.classList.toggle("show");
    });

    document.addEventListener("click", () => {
      profileMenu.classList.remove("show");
    });

    profileMenu.addEventListener("click", (event) => {
      event.stopPropagation();
    });
  }

  /* ======================================================
     PROFILE BUTTON
  ====================================================== */

  const profileButton = document.getElementById("profileButton");

  if (profileButton) {
    profileButton.addEventListener("click", () => {
      if (role === "STAFF") {
        showToast("Staff Profile page coming soon.", "info");
      } else {
        showToast("Patient Profile page coming soon.", "info");
      }
    });
  }

  /* ======================================================
   APPEARANCE
====================================================== */

  const themeButton = document.getElementById("themeButton");

  if (themeButton) {
    themeButton.addEventListener("click", () => {
      toggleTheme();

      profileMenu?.classList.remove("show");
    });
  }

  /* ======================================================
     LOGOUT
  ====================================================== */

  const logoutButton = document.getElementById("logoutButton");

  if (logoutButton) {
    logoutButton.addEventListener("click", () => {
      showToast("Logging out...", "info");

      setTimeout(() => {
        logout();
      }, 800);
    });
  }

  console.log("✓ Header Initialized");
}

/* ==========================================================
   LOAD CURRENT USER
========================================================== */

async function loadCurrentUserHeader() {
  try {
    const user = await apiCall("/api/users/me");

    const usernameElement = document.getElementById("headerUsername");
    const roleElement = document.getElementById("headerRole");
    const avatarElement = document.getElementById("headerAvatar");

    if (usernameElement) {
      usernameElement.textContent = user.name;
    }

    if (roleElement) {
      roleElement.textContent = user.role === "STAFF" ? "Staff" : "Patient";
    }

    if (avatarElement) {
      if (user.role === "STAFF") {
        avatarElement.src = "assets/avatars/doctor.png";
      } else {
        avatarElement.src =
          user.gender === "FEMALE"
            ? "assets/avatars/patient-female.png"
            : "assets/avatars/patient-male.png";
      }
    }
  } catch (error) {
    console.error(error);
  }
}
