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

  /* ======================================================
     PHONE INPUT
  ====================================================== */

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
     REQUIRED FIELDS
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

  /* ======================================================
     FULL NAME
  ====================================================== */

  if (!/^[A-Za-z ]{3,50}$/.test(fullName)) {
    showToast(
      "Full name must be 3-50 characters and contain only letters and spaces.",
      "warning",
    );
    return;
  }

  /* ======================================================
     USERNAME
  ====================================================== */

  if (!/^[A-Za-z0-9_]{4,20}$/.test(username)) {
    showToast(
      "Username must be 4-20 characters and contain only letters, numbers and underscore.",
      "warning",
    );
    return;
  }

  /* ======================================================
     EMAIL
  ====================================================== */

  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  if (!emailRegex.test(email)) {
    showToast("Please enter a valid email address.", "warning");
    return;
  }

  /* ======================================================
     PHONE
  ====================================================== */

  if (!/^[0-9]{10}$/.test(phone)) {
    showToast("Phone number must contain exactly 10 digits.", "warning");
    return;
  }

  /* ======================================================
     PASSWORD
  ====================================================== */

  const passwordRegex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()_\-+=])[A-Za-z\d@$!%*?&^#()_\-+=]{8,}$/;

  if (!passwordRegex.test(password)) {
    showToast(
      "Password must contain at least 8 characters, including uppercase, lowercase, number and special character.",
      "warning",
    );
    return;
  }

  /* ======================================================
     CONFIRM PASSWORD
  ====================================================== */

  if (password !== confirmPassword) {
    showToast("Passwords do not match.", "error");
    return;
  }

  /* ======================================================
     GENDER
  ====================================================== */

  if (!gender) {
    showToast("Please select your gender.", "warning");
    return;
  }

  /* ======================================================
     DISABLE BUTTON
  ====================================================== */

  submitButton.disabled = true;
  submitButton.textContent = "Creating Account...";

  try {
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
