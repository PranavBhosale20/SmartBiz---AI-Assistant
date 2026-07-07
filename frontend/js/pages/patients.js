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

        <td>${patient.dateOfBirth ?? "-"}</td>

        <td>${patient.gender ?? "-"}</td>

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
              class="action-btn open-modal"
              data-modal="add-patient"
              data-id="${patient.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-entity="patient"
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

/* ==========================================================
   SAVE PATIENT (CREATE / UPDATE)
========================================================== */

async function savePatient() {
  const patientId = document.getElementById("patientId").value;

  const patient = {
    name: document.getElementById("firstName").value.trim(),

    email: document.getElementById("email").value.trim(),

    phone: document.getElementById("phone").value.trim(),

    gender: document.getElementById("gender").value,

    dateOfBirth: document.getElementById("dateOfBirth").value,

    address: document.getElementById("address").value.trim(),

    bloodGroup: document.getElementById("bloodGroup").value,

    emergencyContact: document.getElementById("emergencyContact").value.trim(),
  };

  try {
    if (patientId) {
      await apiCall(`/api/users/${patientId}`, "PUT", patient);

      showToast("Patient updated successfully.", "success");
    } else {
      await apiCall("/api/users", "POST", patient);

      showToast("Patient added successfully.", "success");
    }

    document.getElementById("patientForm").reset();

    closeModal();

    loadPatients();
  } catch (error) {
    showToast(error.message || "Failed to save patient.", "error");
  }
}

/* ==========================================================
   ADD PATIENT MODAL
========================================================== */

function initializeAddPatientModal() {
  document.getElementById("patientId").value = "";

  document.getElementById("patientModalTitle").textContent = "Add New Patient";

  document.getElementById("patientModalSubtitle").textContent =
    "Register a new patient into SmartBiz Clinic OS.";

  document.getElementById("savePatientBtnText").textContent = "Save Patient";

  document.getElementById("patientForm").reset();

  const savePatientBtn = document.getElementById("savePatientBtn");

  savePatientBtn.replaceWith(savePatientBtn.cloneNode(true));

  document
    .getElementById("savePatientBtn")
    .addEventListener("click", savePatient);
}

/* ==========================================================
   EDIT PATIENT MODAL
========================================================== */

async function initializeEditPatientModal(modalData) {
  if (!modalData?.id) return;

  try {
    const patient = await apiCall(`/api/users/${modalData.id}`);

    document.getElementById("patientId").value = patient.id;

    document.getElementById("firstName").value = patient.name;

    document.getElementById("email").value = patient.email ?? "";

    document.getElementById("phone").value = patient.phone ?? "";

    document.getElementById("gender").value = patient.gender ?? "";

    document.getElementById("dateOfBirth").value = patient.dateOfBirth ?? "";

    document.getElementById("address").value = patient.address ?? "";

    document.getElementById("bloodGroup").value = patient.bloodGroup ?? "";

    document.getElementById("emergencyContact").value =
      patient.emergencyContact ?? "";

    document.getElementById("patientModalTitle").textContent = "Edit Patient";

    document.getElementById("patientModalSubtitle").textContent =
      "Update patient information.";

    document.getElementById("savePatientBtnText").textContent =
      "Update Patient";

    const savePatientBtn = document.getElementById("savePatientBtn");

    savePatientBtn.replaceWith(savePatientBtn.cloneNode(true));

    document
      .getElementById("savePatientBtn")
      .addEventListener("click", savePatient);
  } catch (error) {
    showToast(error.message || "Failed to load patient.", "error");
  }
}

/* ==========================================================
   VIEW PATIENT MODAL
========================================================== */

function initializeViewPatientModal(modalData) {
  if (!modalData?.id) return;

  viewPatient(modalData.id);
}

/* ==========================================================
   VIEW PATIENT
========================================================== */

async function viewPatient(id) {
  try {
    const patient = await apiCall(`/api/users/${id}`);

    document.getElementById("viewPatientId").value = patient.id;
    document.getElementById("viewPatientName").value = patient.name;
    document.getElementById("viewPatientEmail").value = patient.email ?? "";
    document.getElementById("viewPatientPhone").value = patient.phone ?? "";
    document.getElementById("viewPatientGender").value = patient.gender ?? "";
    document.getElementById("viewPatientDob").value = patient.dateOfBirth ?? "";
    document.getElementById("viewPatientBloodGroup").value =
      patient.bloodGroup ?? "";
    document.getElementById("viewPatientEmergencyContact").value =
      patient.emergencyContact ?? "";
    document.getElementById("viewPatientAddress").value = patient.address ?? "";

    document.getElementById("viewPatientCreatedAt").value = new Date(
      patient.createdAt,
    ).toLocaleString();
  } catch (error) {
    showToast(error.message || "Failed to load patient.", "error");
  }
}

/* ==========================================================
   DELETE PATIENT
========================================================== */

async function deletePatient(id) {
  try {
    await apiCall(`/api/users/${id}`, "DELETE");

    showToast("Patient deleted successfully.", "success");

    closeModal();

    loadPatients();
  } catch (error) {
    showToast(error.message || "Failed to delete patient.", "error");
  }
}
