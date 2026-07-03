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

    saveLogin(
      response.token,

      response.username,

      response.role,
    );

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
   LOGIN FORM
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");

  if (!form) return;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();

    const password = document.getElementById("password").value;

    const role = document.querySelector('input[name="role"]:checked').value;

    await login(username, password, role);
  });
});
