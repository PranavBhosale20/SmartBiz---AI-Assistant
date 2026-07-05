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
  const role = getRole();

  if (usernameElement) {
    usernameElement.textContent = username || "Guest";
  }

  if (roleElement) {
    roleElement.textContent = role || "";
  }

  if (avatarElement) {
    if (role === "STAFF") {
      avatarElement.src = "assets/avatars/doctor.png";
    } else {
      avatarElement.src = "assets/avatars/patient-male.png";
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
        // TODO
        // window.location.href = "staff-profile.html";

        showToast("Staff Profile page coming soon.", "info");
      } else {
        window.location.href = "patient-profile.html";
      }
    });
  }

  /* ======================================================
     APPEARANCE
  ====================================================== */

  const themeButton = document.getElementById("themeButton");

  if (themeButton) {
    themeButton.addEventListener("click", () => {
      showToast("Theme support will be added soon.", "info");
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
}
