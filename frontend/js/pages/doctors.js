/* ==========================================================
   DOCTORS PAGE
========================================================== */

async function initializeDoctors() {
  const tableBody = document.getElementById("doctorTableBody");

  if (!tableBody) return;

  loadDoctors();
}

/* ==========================================================
   LOAD DOCTORS
========================================================== */

async function loadDoctors() {
  const tableBody = document.getElementById("doctorTableBody");

  showLoading(tableBody, "Loading doctors...");

  try {
    const doctors = await apiCall("/api/doctors");

    renderDoctors(doctors);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load doctors.");
  }
}

/* ==========================================================
   RENDER DOCTORS
========================================================== */

function renderDoctors(doctors) {
  const tableBody = document.getElementById("doctorTableBody");

  if (!doctors || doctors.length === 0) {
    showEmptyState(tableBody, "No doctors found.");
    return;
  }

  tableBody.innerHTML = "";

  doctors.forEach((doctor) => {
    tableBody.innerHTML += `
      <tr>

        <td>#${doctor.id}</td>

        <td>
          <div class="patient-info">
            <img
              src="assets/avatars/doctor.png"
              alt="${doctor.name}"
            />

            <div>
              <h4>${doctor.name}</h4>
            </div>
          </div>
        </td>

        <td>${doctor.specialization}</td>

        <td>-</td>

        <td>${doctor.opdStartTime} - ${doctor.opdEndTime}</td>

        <td>
          <span class="status active">
            Available
          </span>
        </td>

        <td>
          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-doctor"
              data-id="${doctor.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn edit-doctor"
              data-id="${doctor.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-id="${doctor.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>
        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Doctors Loaded");
}

/* ==========================================================
   CREATE DOCTOR
========================================================== */

async function createDoctor() {
  console.log("Save Doctor clicked");
  const doctor = {
    name: document.getElementById("doctorName").value.trim(),
    specialization: document.getElementById("specialization").value,
    opdStartTime: document.getElementById("opdStartTime").value,
    opdEndTime: document.getElementById("opdEndTime").value,
    slotDurationMinutes: Number(
      document.getElementById("slotDurationMinutes").value,
    ),
  };

  try {
    const form = document.getElementById("doctorForm");

    if (form) {
      form.reset();
    }

    closeModal();

    loadDoctors();
  } catch (error) {
    showToast(error.message || "Failed to add doctor.", "error");
  }
}
