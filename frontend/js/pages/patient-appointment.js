/* ==========================================================
   PATIENT APPOINTMENTS PAGE
========================================================== */

protectPage("PATIENT");

document.addEventListener("DOMContentLoaded", () => {
  initializePatientAppointments();
});

/* ==========================================================
   INITIALIZE
========================================================== */

async function initializePatientAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  if (!tableBody) return;

  loadPatientAppointments();
}

/* ==========================================================
   LOAD PATIENT APPOINTMENTS
========================================================== */

async function loadPatientAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  showLoading(tableBody, "Loading appointments...");

  try {
    const user = await apiCall("/api/users/me");

    const appointments = await apiCall(`/api/appointments/user/${user.id}`);

    renderPatientAppointments(appointments);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load appointments.");
  }
}

/* ==========================================================
   RENDER PATIENT APPOINTMENTS
========================================================== */

function renderPatientAppointments(appointments) {
  const tableBody = document.getElementById("appointmentTableBody");

  if (!appointments || appointments.length === 0) {
    showEmptyState(tableBody, "No appointments found.");
    return;
  }

  tableBody.innerHTML = "";

  appointments.forEach((appointment) => {
    const avatar = "assets/avatars/doctor.png";

    const appointmentDate = new Date(appointment.appointmentDate);

    const date = appointmentDate.toLocaleDateString();

    const time = appointmentDate.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });

    const canCancel = appointment.status !== "CANCELLED";

    tableBody.innerHTML += `
      <tr>

        <td>

          <div class="patient-info">

            <img
              src="${avatar}"
              alt="${appointment.doctorName}"
            />

            <div>

              <h4>${appointment.doctorName}</h4>

              <p>${appointment.visitTypeName}</p>

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

            ${
              canCancel
                ? `
            <button
              class="action-btn"
              onclick="cancelPatientAppointment(${appointment.id})">

              <i data-lucide="calendar-x-2"></i>

            </button>
            `
                : ""
            }

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Patient Appointments Loaded");
}

/* ==========================================================
   CANCEL APPOINTMENT
========================================================== */

async function cancelPatientAppointment(id) {
  if (!confirm("Cancel this appointment?")) {
    return;
  }

  try {
    await apiCall(`/api/appointments/${id}/cancel`, "PUT");

    showToast("Appointment cancelled successfully.", "success");

    loadPatientAppointments();
  } catch (error) {
    showToast(error.message || "Unable to cancel appointment.", "error");
  }
}
