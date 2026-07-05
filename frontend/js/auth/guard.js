/* ==========================================================
   SMARTBIZ ROUTE GUARD
========================================================== */

/* ==========================================================
   PROTECT PAGE
========================================================== */

function protectPage(requiredRole = null) {
  /* ======================================================
     USER NOT LOGGED IN
  ====================================================== */

  if (!isLoggedIn()) {
    window.location.href = "login.html";
    return;
  }

  /* ======================================================
     TOKEN EXPIRED
  ====================================================== */

  if (isTokenExpired()) {
    showToast("Your session has expired. Please login again.", "warning");

    setTimeout(() => {
      logout();
    }, 1000);

    return;
  }

  /* ======================================================
     ROLE PROTECTION
  ====================================================== */

  const currentRole = getRole();

  if (requiredRole && currentRole !== requiredRole) {
    if (currentRole === "STAFF") {
      window.location.href = "dashboard.html";
    } else {
      window.location.href = "patient-dashboard.html";
    }

    return;
  }

  console.log("✓ Route Guard Passed");
}
