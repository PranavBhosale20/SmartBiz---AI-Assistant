/* ==========================================================
   LOGIN PAGE
========================================================== */

let selectedRole = "STAFF";

/* ==========================================================
   LOGIN
========================================================== */

async function login(username, password, role) {
  const submitButton = document.querySelector(".auth-btn");

  submitButton.disabled = true;
  submitButton.textContent = "Signing In...";

  try {
    /* ======================================================
       SELECT ENDPOINT
    ====================================================== */

    const endpoint =
      role === "STAFF" ? "/api/auth/staff-login" : "/api/auth/patient-login";

    /* ======================================================
       API CALL
    ====================================================== */

    const response = await apiCall(endpoint, "POST", {
      username,
      password,
    });

    /* ======================================================
       SAVE LOGIN
    ====================================================== */

    saveLogin(
      response.token,
      response.username,
      response.fullName,
      response.role,
      response.gender,
    );

    showToast("Login successful. Redirecting...", "success");

    /* ======================================================
       REDIRECT
    ====================================================== */

    setTimeout(() => {
      if (response.role === "STAFF") {
        window.location.href = "dashboard.html";
      } else {
        window.location.href = "patient-dashboard.html";
      }
    }, 1000);
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    submitButton.disabled = false;
    submitButton.textContent = "Login";
  }
}

/* ==========================================================
   INITIALIZE
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");

  const staffButton = document.getElementById("staffRole");

  const patientButton = document.getElementById("patientRole");

  /* Default */

  staffButton.classList.add("active");

  /* Staff */

  staffButton.addEventListener("click", () => {
    selectedRole = "STAFF";

    staffButton.classList.add("active");

    patientButton.classList.remove("active");
  });

  /* Patient */

  patientButton.addEventListener("click", () => {
    selectedRole = "PATIENT";

    patientButton.classList.add("active");

    staffButton.classList.remove("active");
  });

  /* Login */

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();

    const password = document.getElementById("password").value;

    if (!username || !password) {
      showToast("Please enter username and password.", "warning");
      return;
    }

    await login(username, password, selectedRole);
  });
});