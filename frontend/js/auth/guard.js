/* ==========================================================
   ROUTE GUARD
========================================================== */

function protectPage(requiredRole = null) {
  if (!isLoggedIn()) {
    window.location.href = "login.html";

    return;
  }

  if (isTokenExpired()) {
    logout();

    return;
  }

  if (requiredRole && getRole() !== requiredRole) {
    if (getRole() === "PATIENT") {
      window.location.href = "patient-dashboard.html";
    } else {
      window.location.href = "dashboard.html";
    }
  }
}
