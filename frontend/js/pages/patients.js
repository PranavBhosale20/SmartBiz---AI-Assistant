/* ==========================================================
   PATIENTS PAGE
========================================================== */

async function initializePatients() {
  const tableBody = document.getElementById("patientTableBody");

  if (!tableBody) return;

  loadPatients();
}

/* ==========================================================
   LOAD PATIENTS
========================================================== */

async function loadPatients() {
  const tableBody = document.getElementById("patientTableBody");

  showLoading(tableBody, "Loading patients...");

  try {
    const patients = await apiCall("/api/users");

    renderPatients(patients);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load patients.");
  }
}

/* ==========================================================
   RENDER PATIENTS
========================================================== */

function renderPatients(patients) {
  const tableBody = document.getElementById("patientTableBody");

  if (!patients || patients.length === 0) {
    showEmptyState(tableBody, "No patients found.");
    return;
  }

  tableBody.innerHTML = "";

  patients.forEach((patient) => {
    const avatar =
      patient.gender === "FEMALE"
        ? "assets/avatars/patient-female.png"
        : "assets/avatars/patient-male.png";

    tableBody.innerHTML += `
      <tr>

        <td>#${patient.id}</td>

        <td>
          <div class="patient-info">
            <img src="${avatar}" alt="${patient.name}" />

            <div>
              <h4>${patient.name}</h4>
              <p>${patient.email ?? "-"}</p>
            </div>
          </div>
        </td>

        <td>-</td>

        <td>${patient.gender ?? "Not Specified"}</td>
        <td>${patient.phone ?? "-"}</td>

        <td>
          <span class="status active">
            Active
          </span>
        </td>

        <td>
          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-patient"
              data-id="${patient.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn edit-patient"
              data-id="${patient.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-id="${patient.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>
        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Patients Loaded");
}
