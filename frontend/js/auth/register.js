/* ==========================================================
   PATIENT REGISTRATION PAGE
========================================================== */

let selectedGender = "MALE";

document.addEventListener("DOMContentLoaded", () => {
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

  const phoneInput = document.getElementById("phone");

  phoneInput.addEventListener("input", () => {
    phoneInput.value = phoneInput.value.replace(/\D/g, "").slice(0, 10);
  });

  /* ======================================================
     GENDER SELECTION
  ====================================================== */

  const maleCard = document.getElementById("maleCard");
  const femaleCard = document.getElementById("femaleCard");
  const genderInput = document.getElementById("gender");

  maleCard.addEventListener("click", () => {
    selectedGender = "MALE";

    genderInput.value = selectedGender;

    maleCard.classList.add("active");
    femaleCard.classList.remove("active");
  });

  femaleCard.addEventListener("click", () => {
    selectedGender = "FEMALE";

    genderInput.value = selectedGender;

    femaleCard.classList.add("active");
    maleCard.classList.remove("active");
  });

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
  const gender = document.getElementById("gender").value;

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

  if (!/^[0-9]{10}$/.test(phone)) {
    showToast("Please enter a valid 10-digit mobile number.", "warning");
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
      gender,
    });

    showToast(
      "Account created successfully. Redirecting to login...",
      "success",
    );

    document.getElementById("registerForm").reset();

    selectedGender = "MALE";
    document.getElementById("gender").value = "MALE";
    document.getElementById("maleCard").classList.add("active");
    document.getElementById("femaleCard").classList.remove("active");

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
