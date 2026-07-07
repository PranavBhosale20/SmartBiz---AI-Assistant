/* ==========================================================
   APPOINTMENTS PAGE
========================================================== */

async function initializeAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  if (!tableBody) return;

  loadAppointments();
}

/* ==========================================================
   LOAD APPOINTMENTS
========================================================== */

async function loadAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  showLoading(tableBody, "Loading appointments...");

  try {
    const appointments = await apiCall("/api/appointments");

    renderAppointments(appointments);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load appointments.");
  }
}

/* ==========================================================
   RENDER APPOINTMENTS
========================================================== */

function renderAppointments(appointments) {
  const tableBody = document.getElementById("appointmentTableBody");

  if (!appointments || appointments.length === 0) {
    showEmptyState(tableBody, "No appointments found.");
    return;
  }

  tableBody.innerHTML = "";

  appointments.forEach((appointment) => {
    const avatar = "assets/avatars/patient-male.png";

    const appointmentDate = new Date(appointment.appointmentDate);

    const date = appointmentDate.toLocaleDateString();

    const time = appointmentDate.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });

    tableBody.innerHTML += `
      <tr>

        <td>
          <div class="patient-info">

            <img
              src="${avatar}"
              alt="${appointment.userName}"
            />

            <div>
              <h4>${appointment.userName}</h4>
              <p>#${appointment.userId}</p>
            </div>

          </div>
        </td>

        <td>${appointment.doctorName}</td>

        <td>${date}</td>

        <td>${time}</td>

        <td>
          <span class="status ${appointment.status.toLowerCase()}">
            ${appointment.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-appointment"
              data-id="${appointment.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="add-appointment"
              data-id="${appointment.id}">
              <i data-lucide="square-pen"></i>
            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-entity="appointment"
              data-id="${appointment.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Appointments Loaded");
}

/* ==========================================================
   ADD APPOINTMENT MODAL
========================================================== */

async function initializeAddAppointmentModal() {
  document.getElementById("appointmentId").value = "";

  document.getElementById("appointmentModalTitle").textContent =
    "Book Appointment";

  document.getElementById("appointmentModalSubtitle").textContent =
    "Schedule a new appointment.";

  document.getElementById("saveAppointmentBtnText").textContent =
    "Book Appointment";

  await loadPatientsDropdown();
  await loadDoctorsDropdown();
  await loadVisitTypesDropdown();

  initializeAvailableSlots();

  const saveBtn = document.getElementById("saveAppointmentBtn");

  saveBtn.replaceWith(saveBtn.cloneNode(true));

  document
    .getElementById("saveAppointmentBtn")
    .addEventListener("click", saveAppointment);
}

/* ==========================================================
   EDIT APPOINTMENT MODAL
========================================================== */

async function initializeEditAppointmentModal(modalData) {
  if (!modalData?.id) return;

  await loadPatientsDropdown();
  await loadDoctorsDropdown();
  await loadVisitTypesDropdown();

  initializeAvailableSlots();

  try {
    const appointment = await apiCall(`/api/appointments/${modalData.id}`);

    document.getElementById("appointmentId").value = appointment.id;

    document.getElementById("appointmentPatient").value = appointment.userId;

    document.getElementById("appointmentDoctor").value = appointment.doctorId;

    document.getElementById("visitType").value = appointment.visitTypeId;

    const dateTime = new Date(appointment.appointmentDate);

    document.getElementById("appointmentDate").value = dateTime
      .toISOString()
      .split("T")[0];

    await loadAvailableSlots();

    document.getElementById("appointmentTime").value =
      appointment.appointmentDate.substring(11, 16);

    document.getElementById("appointmentModalTitle").textContent =
      "Edit Appointment";

    document.getElementById("appointmentModalSubtitle").textContent =
      "Update appointment details.";

    document.getElementById("saveAppointmentBtnText").textContent =
      "Update Appointment";

    const saveBtn = document.getElementById("saveAppointmentBtn");

    saveBtn.replaceWith(saveBtn.cloneNode(true));

    document
      .getElementById("saveAppointmentBtn")
      .addEventListener("click", saveAppointment);
  } catch (error) {
    showToast(error.message || "Failed to load appointment.", "error");
  }
}

/* ==========================================================
   LOAD PATIENT DROPDOWN
========================================================== */

async function loadPatientsDropdown() {
  const select = document.getElementById("appointmentPatient");

  select.innerHTML = `<option value="">Select Patient</option>`;

  const patients = await apiCall("/api/users");

  patients.forEach((patient) => {
    select.innerHTML += `
      <option value="${patient.id}">
        ${patient.name}
      </option>
    `;
  });
}

/* ==========================================================
   LOAD DOCTOR DROPDOWN
========================================================== */

async function loadDoctorsDropdown() {
  const select = document.getElementById("appointmentDoctor");

  select.innerHTML = `<option value="">Select Doctor</option>`;

  const doctors = await apiCall("/api/doctors");

  doctors.forEach((doctor) => {
    select.innerHTML += `
      <option value="${doctor.id}">
        ${doctor.name}
      </option>
    `;
  });
}

/* ==========================================================
   LOAD VISIT TYPE DROPDOWN
========================================================== */

async function loadVisitTypesDropdown() {
  const select = document.getElementById("visitType");

  select.innerHTML = `<option value="">Select Visit Type</option>`;

  const visitTypes = await apiCall("/api/visit-types");

  visitTypes.forEach((visitType) => {
    select.innerHTML += `
      <option value="${visitType.id}">
        ${visitType.name}
      </option>
    `;
  });
}

/* ==========================================================
   LOAD AVAILABLE TIME SLOTS
========================================================== */

function initializeAvailableSlots() {
  console.log("✓ initializeAvailableSlots()");

  const doctorSelect = document.getElementById("appointmentDoctor");
  const dateInput = document.getElementById("appointmentDate");

  if (!doctorSelect || !dateInput) return;

  // Prevent duplicate listeners when reopening the modal
  doctorSelect.onchange = loadAvailableSlots;
  dateInput.onchange = loadAvailableSlots;
}

async function loadAvailableSlots() {
  const doctorId = document.getElementById("appointmentDoctor").value;
  const date = document.getElementById("appointmentDate").value;
  const timeSelect = document.getElementById("appointmentTime");

  console.log("Doctor ID:", doctorId);
  console.log("Date:", date);

  timeSelect.innerHTML = `
    <option value="">Select Time</option>
  `;

  if (!doctorId || !date || date.length !== 10) {
    console.log("Waiting for Doctor and Date...");
    return;
  }

  try {
    console.log(
      `Fetching: /api/appointments/available-slots?doctorId=${doctorId}&date=${date}`,
    );

    const slots = await apiCall(
      `/api/appointments/available-slots?doctorId=${doctorId}&date=${date}`,
    );

    console.log("Available Slots:", slots);

    timeSelect.innerHTML = "";

    if (!slots || slots.length === 0) {
      timeSelect.innerHTML = `
        <option value="">No Slots Available</option>
      `;
      return;
    }

    timeSelect.innerHTML = `
      <option value="">Select Time</option>
    `;

    slots.forEach((slot) => {
      timeSelect.innerHTML += `
        <option value="${slot}">
          ${slot.substring(0, 5)}
        </option>
      `;
    });
  } catch (error) {
    console.error("Failed to load slots:", error);

    showToast("Failed to load available slots.", "error");
  }
}

/* ==========================================================
   SAVE APPOINTMENT
========================================================== */

async function saveAppointment() {
  const patientId = document.getElementById("appointmentPatient").value;
  const doctorId = document.getElementById("appointmentDoctor").value;
  const visitTypeId = document.getElementById("visitType").value;
  const date = document.getElementById("appointmentDate").value;
  const time = document.getElementById("appointmentTime").value;

  if (!patientId || !doctorId || !visitTypeId || !date || !time) {
    showToast("Please fill all appointment details.", "warning");
    return;
  }

  const appointment = {
    userId: Number(patientId),
    doctorId: Number(doctorId),
    visitTypeId: Number(visitTypeId),
    appointmentDate: `${date}T${time}`,
  };

  try {
    const appointmentId = document.getElementById("appointmentId").value;

    if (appointmentId) {
      await apiCall(`/api/appointments/${appointmentId}`, "PUT", appointment);

      showToast("Appointment updated successfully.", "success");
    } else {
      await apiCall("/api/appointments", "POST", appointment);

      showToast("Appointment booked successfully.", "success");
    }

    closeModal();

    loadAppointments();
  } catch (error) {
    showToast(error.message || "Failed to book appointment.", "error");
  }
}

/* ==========================================================
   VIEW APPOINTMENT MODAL
========================================================== */

function initializeViewAppointmentModal(modalData) {
  if (!modalData?.id) return;

  viewAppointment(modalData.id);
}

/* ==========================================================
   VIEW APPOINTMENT
========================================================== */

async function viewAppointment(id) {
  try {
    const appointment = await apiCall(`/api/appointments/${id}`);

    document.getElementById("viewAppointmentId").textContent =
      "#" + appointment.id;

    document.getElementById("viewAppointmentPatient").textContent =
      appointment.userName;

    document.getElementById("viewAppointmentDoctor").textContent =
      appointment.doctorName;

    document.getElementById("viewAppointmentVisitType").textContent =
      appointment.visitTypeName;

    document.getElementById("viewAppointmentStatus").textContent =
      appointment.status;

    const dateTime = new Date(appointment.appointmentDate);

    document.getElementById("viewAppointmentDate").textContent =
      dateTime.toLocaleDateString();

    document.getElementById("viewAppointmentTime").textContent =
      dateTime.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });

    document.getElementById("viewAppointmentCreatedAt").textContent = new Date(
      appointment.createdAt,
    ).toLocaleString();
  } catch (error) {
    showToast(error.message || "Failed to load appointment.", "error");
  }
}
