/* ==========================================================
   LOGIN PAGE
========================================================== */

let selectedRole = "STAFF";

/* ==========================================================
   LOGIN
========================================================== */

async function login(username, password, role) {
  try {
    // TODO
    // Replace with:
    // const response = await apiCall("/api/auth/...", "POST", {...});

    const response = {
      token: "dummy-jwt-token",
      username,
      role,
    };

    saveLogin(response.token, response.username, response.role);

    if (response.role === "STAFF") {
      window.location.href = "dashboard.html";
    } else {
      window.location.href = "patient-dashboard.html";
    }
  } catch (error) {
    alert("Invalid username or password.");
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

    await login(username, password, selectedRole);
  });
});
