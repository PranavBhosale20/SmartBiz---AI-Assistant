/* ==========================================================
   STAFF APPOINTMENTS PAGE
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  initializeStaffAppointments();
});

/* ==========================================================
   INITIALIZE
========================================================== */

async function initializeStaffAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  if (!tableBody) return;

  loadStaffAppointments();
}

/* ==========================================================
   LOAD APPOINTMENTS
========================================================== */

async function loadStaffAppointments() {
  const tableBody = document.getElementById("appointmentTableBody");

  showLoading(tableBody, "Loading appointments...");

  try {
    const appointments = await apiCall("/api/appointments");

    renderStaffAppointments(appointments);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load appointments.");
  }
}

/* ==========================================================
   RENDER APPOINTMENTS
========================================================== */

function renderStaffAppointments(appointments) {
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

  console.log("✓ Staff Appointments Loaded");
}