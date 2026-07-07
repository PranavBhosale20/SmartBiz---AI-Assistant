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
              class="action-btn open-modal view-doctor-btn"
              data-modal="view-doctor"
              data-id="${doctor.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="add-doctor"
              data-id="${doctor.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-id="${doctor.id}"
              data-entity="doctor">

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
   SAVE DOCTOR (CREATE / UPDATE)
========================================================== */

async function saveDoctor() {
  const doctorId = document.getElementById("doctorId").value;

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
    if (doctorId) {
      await apiCall(`/api/doctors/${doctorId}`, "PUT", doctor);

      showToast("Doctor updated successfully.", "success");
    } else {
      await apiCall("/api/doctors", "POST", doctor);

      showToast("Doctor added successfully.", "success");
    }

    document.getElementById("doctorForm").reset();

    closeModal();

    loadDoctors();
  } catch (error) {
    showToast(error.message || "Failed to save doctor.", "error");
  }
}

/* ==========================================================
   ADD DOCTOR MODAL
========================================================== */

function initializeAddDoctorModal() {
  document.getElementById("doctorId").value = "";

  document.getElementById("doctorModalTitle").textContent = "Add Doctor";

  document.getElementById("doctorModalSubtitle").textContent =
    "Register a doctor in SmartBiz Clinic OS.";

  document.getElementById("saveDoctorBtnText").textContent = "Save Doctor";

  const saveDoctorBtn = document.getElementById("saveDoctorBtn");

  saveDoctorBtn.replaceWith(saveDoctorBtn.cloneNode(true));

  document
    .getElementById("saveDoctorBtn")
    .addEventListener("click", saveDoctor);
}

/* ==========================================================
   EDIT DOCTOR MODAL
========================================================== */

async function initializeEditDoctorModal(modalData) {
  if (!modalData?.id) return;

  try {
    const doctor = await apiCall(`/api/doctors/${modalData.id}`);

    document.getElementById("doctorId").value = doctor.id;

    document.getElementById("doctorName").value = doctor.name;

    document.getElementById("specialization").value = doctor.specialization;

    document.getElementById("opdStartTime").value =
      doctor.opdStartTime.substring(0, 5);

    document.getElementById("opdEndTime").value = doctor.opdEndTime.substring(
      0,
      5,
    );

    document.getElementById("slotDurationMinutes").value =
      doctor.slotDurationMinutes;

    document.getElementById("doctorModalTitle").textContent = "Edit Doctor";

    document.getElementById("doctorModalSubtitle").textContent =
      "Update doctor information.";

    document.getElementById("saveDoctorBtnText").textContent = "Update Doctor";

    const saveDoctorBtn = document.getElementById("saveDoctorBtn");

    saveDoctorBtn.replaceWith(saveDoctorBtn.cloneNode(true));

    document
      .getElementById("saveDoctorBtn")
      .addEventListener("click", saveDoctor);
  } catch (error) {
    showToast(error.message || "Failed to load doctor.", "error");
  }
}
/* ==========================================================
   VIEW DOCTOR MODAL
========================================================== */

function initializeViewDoctorModal(modalData) {
  if (!modalData?.id) return;

  viewDoctor(modalData.id);
}

/* ==========================================================
   VIEW DOCTOR
========================================================== */

async function viewDoctor(id) {
  try {
    const doctor = await apiCall(`/api/doctors/${id}`);

    document.getElementById("viewDoctorId").value = doctor.id;
    document.getElementById("viewDoctorName").value = doctor.name;
    document.getElementById("viewDoctorSpecialization").value =
      doctor.specialization;
    document.getElementById("viewDoctorSlotDuration").value =
      doctor.slotDurationMinutes;
    document.getElementById("viewDoctorStartTime").value = doctor.opdStartTime;
    document.getElementById("viewDoctorEndTime").value = doctor.opdEndTime;

    document.getElementById("viewDoctorCreatedAt").value = new Date(
      doctor.createdAt,
    ).toLocaleString();
  } catch (error) {
    showToast(error.message || "Failed to load doctor.", "error");
  }
}

/* ==========================================================
   DELETE DOCTOR
========================================================== */

async function deleteDoctor(id) {
  try {
    await apiCall(`/api/doctors/${id}`, "DELETE");

    showToast("Doctor deleted successfully.", "success");

    closeModal();

    loadDoctors();
  } catch (error) {
    showToast(error.message || "Failed to delete doctor.", "error");
  }
}
