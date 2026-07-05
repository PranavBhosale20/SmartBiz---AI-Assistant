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

  /* ======================================================
     LOAD USER INFO
  ====================================================== */

  const usernameElement = document.getElementById("headerUsername");
  const roleElement = document.getElementById("headerRole");
  const avatarElement = document.getElementById("headerAvatar");

  const username = getUsername();
  const fullName = getFullName();
  const role = getRole();
  const gender = getGender();

  if (usernameElement) {
    usernameElement.textContent = fullName || username || "Guest";
  }

  if (roleElement) {
    if (role === "STAFF") {
      roleElement.textContent = "Staff";
    } else if (role === "PATIENT") {
      roleElement.textContent = "Patient";
    } else {
      roleElement.textContent = "";
    }
  }

  if (avatarElement) {
    if (role === "STAFF") {
      avatarElement.src = "assets/avatars/doctor.png";
    } else {
      if (gender === "FEMALE") {
        avatarElement.src = "assets/avatars/patient-female.png";
      } else {
        avatarElement.src = "assets/avatars/patient-male.png";
      }
    }
  }

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
