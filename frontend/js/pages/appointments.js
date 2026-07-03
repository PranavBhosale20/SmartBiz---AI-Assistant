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
    // TODO: Replace with:
    // const appointments = await apiCall("/api/appointments");

    const appointments = [
      {
        patient: "Rahul Sharma",
        patientId: "PT001",
        doctor: "Dr. Mehta",
        date: "12 Jul 2026",
        time: "09:30 AM",
        status: "Confirmed",
        avatar: "assets/avatars/patient-male.png",
      },
      {
        patient: "Priya Patil",
        patientId: "PT002",
        doctor: "Dr. Joshi",
        date: "12 Jul 2026",
        time: "10:15 AM",
        status: "Confirmed",
        avatar: "assets/avatars/patient-female.png",
      },
      {
        patient: "Amit Joshi",
        patientId: "PT003",
        doctor: "Dr. Kulkarni",
        date: "13 Jul 2026",
        time: "11:45 AM",
        status: "Confirmed",
        avatar: "assets/avatars/patient-male.png",
      },
      {
        patient: "Sneha Kulkarni",
        patientId: "PT004",
        doctor: "Dr. Patil",
        date: "14 Jul 2026",
        time: "03:00 PM",
        status: "Cancelled",
        avatar: "assets/avatars/patient-female.png",
      },
    ];

    renderAppointments(appointments);
  } catch (error) {
    showError(tableBody, "Failed to load appointments.");
  }
}

/* ==========================================================
   RENDER APPOINTMENTS
========================================================== */

function renderAppointments(appointments) {
  const tableBody = document.getElementById("appointmentTableBody");

  if (appointments.length === 0) {
    showEmptyState(tableBody, "No appointments found.");
    return;
  }

  tableBody.innerHTML = "";

  appointments.forEach((appointment) => {
    tableBody.innerHTML += `
      <tr>

        <td>
          <div class="patient-info">
            <img src="${appointment.avatar}" alt="${appointment.patient}" />

            <div>
              <h4>${appointment.patient}</h4>
              <p>${appointment.patientId}</p>
            </div>
          </div>
        </td>

        <td>${appointment.doctor}</td>

        <td>${appointment.date}</td>

        <td>${appointment.time}</td>

        <td>
          <span class="status ${appointment.status.toLowerCase()}">
            ${appointment.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button class="action-btn open-modal" data-modal="view-appointment">
              <i data-lucide="eye"></i>
            </button>

            <button class="action-btn">
              <i data-lucide="square-pen"></i>
            </button>

            <button class="action-btn open-modal" data-modal="delete-confirmation">
              <i data-lucide="trash-2"></i>
            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();
}
