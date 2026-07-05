/* ==========================================================
   PATIENT REGISTRATION PAGE
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  alert("NEW REGISTER.JS LOADED");

  initializeRegister();
});

/* ==========================================================
   INITIALIZE
========================================================== */

function initializeRegister() {
  const registerForm = document.getElementById("registerForm");

  if (!registerForm) {
    return;
  }

  registerForm.addEventListener("submit", handleRegister);

  console.log("✓ Patient Registration Page Loaded");
}

/* ==========================================================
   HANDLE REGISTRATION
========================================================== */

async function handleRegister(event) {
  event.preventDefault();

  const submitButton = document.querySelector(".auth-btn");

  const fullName = document.getElementById("fullName").value.trim();
  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  /* ======================================================
     VALIDATION
  ====================================================== */

  if (
    !fullName ||
    !username ||
    !email ||
    !phone ||
    !password ||
    !confirmPassword
  ) {
    showToast("Please fill all required fields.", "warning");
    return;
  }

  if (password !== confirmPassword) {
    showToast("Passwords do not match.", "error");
    return;
  }

  if (password.length < 6) {
    showToast("Password must be at least 6 characters.", "warning");
    return;
  }

  /* ======================================================
     DISABLE BUTTON
  ====================================================== */

  submitButton.disabled = true;
  submitButton.textContent = "Creating Account...";

  try {
    /* ======================================================
       API CALL
    ====================================================== */

    await apiCall("/api/auth/patient-register", "POST", {
      name: fullName,
      email,
      phone,
      username,
      password,
    });

    showToast(
      "Account created successfully. Redirecting to login...",
      "success",
    );

    document.getElementById("registerForm").reset();

    setTimeout(() => {
      window.location.href = "login.html";
    }, 1500);
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    submitButton.disabled = false;
    submitButton.textContent = "Create Patient Account";
  }
}
