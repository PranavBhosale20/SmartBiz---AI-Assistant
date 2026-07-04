/* ==========================================================
   REGISTER PAGE
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  initializeRegister();
});

/* ==========================================================
   INITIALIZE
========================================================== */

function initializeRegister() {
  const staffButton = document.getElementById("staffRole");
  const patientButton = document.getElementById("patientRole");

  const staffFields = document.querySelector(".staff-fields");
  const patientFields = document.querySelector(".patient-fields");

  // Default = Staff

  patientFields.style.display = "none";

  staffButton.classList.add("active");

  staffButton.addEventListener("click", () => {
    staffButton.classList.add("active");
    patientButton.classList.remove("active");

    staffFields.style.display = "block";
    patientFields.style.display = "none";
  });

  patientButton.addEventListener("click", () => {
    patientButton.classList.add("active");
    staffButton.classList.remove("active");

    patientFields.style.display = "block";
    staffFields.style.display = "none";
  });

  console.log("✓ Register Page Loaded");
}
