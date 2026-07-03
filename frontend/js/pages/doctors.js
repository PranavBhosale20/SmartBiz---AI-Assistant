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
    // TODO: Replace with:
    // const doctors = await apiCall("/api/doctors");

    const doctors = [
      {
        id: "DR001",
        name: "Dr. Mehta",
        specialization: "Cardiologist",
        experience: "12 Yrs",
        phone: "+91 9876543201",
        status: "Available",
        avatar: "assets/avatars/doctor.png",
      },
      {
        id: "DR002",
        name: "Dr. Patil",
        specialization: "Dentist",
        experience: "8 Yrs",
        phone: "+91 9876543202",
        status: "Available",
        avatar: "assets/avatars/doctor.png",
      },
      {
        id: "DR003",
        name: "Dr. Kulkarni",
        specialization: "Orthopedic",
        experience: "15 Yrs",
        phone: "+91 9876543203",
        status: "Unavailable",
        avatar: "assets/avatars/doctor.png",
      },
    ];

    renderDoctors(doctors);
  } catch (error) {
    showError(tableBody, "Failed to load doctors.");
  }
}

/* ==========================================================
   RENDER DOCTORS
========================================================== */

function renderDoctors(doctors) {
  const tableBody = document.getElementById("doctorTableBody");

  if (doctors.length === 0) {
    showEmptyState(tableBody, "No doctors found.");
    return;
  }

  tableBody.innerHTML = "";

  doctors.forEach((doctor) => {
    tableBody.innerHTML += `
      <tr>

        <td>${doctor.id}</td>

<td>
  <div class="patient-info">
    <img src="${doctor.avatar}" alt="${doctor.name}" />

    <div>
      <h4>${doctor.name}</h4>
    </div>
  </div>
</td>

<td>${doctor.specialization}</td>

<td>${doctor.experience}</td>

        <td>${doctor.phone}</td>

        <td>
          <span class="status ${doctor.status.toLowerCase()}">
            ${doctor.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-doctor">

              <i data-lucide="eye"></i>

            </button>

            <button class="action-btn edit-doctor">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation">

              <i data-lucide="trash-2"></i>

            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();
}
