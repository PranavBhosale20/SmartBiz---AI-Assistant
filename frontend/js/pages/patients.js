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

  // TODO: Replace with:
  // const patients = await apiCall("/api/users");

  try {
    // Temporary Dummy Data
    const patients = [
      {
        id: "PT001",
        name: "Rahul Sharma",
        email: "rahul@email.com",
        age: 32,
        gender: "Male",
        phone: "+91 9876543210",
        status: "Active",
        avatar: "assets/avatars/patient-male.png",
      },
      {
        id: "PT002",
        name: "Priya Patil",
        email: "priya@email.com",
        age: 28,
        gender: "Female",
        phone: "+91 9876543211",
        status: "Active",
        avatar: "assets/avatars/patient-female.png",
      },
      {
        id: "PT003",
        name: "Amit Joshi",
        email: "amit@email.com",
        age: 45,
        gender: "Male",
        phone: "+91 9876543212",
        status: "Inactive",
        avatar: "assets/avatars/patient-male.png",
      },
      {
        id: "PT004",
        name: "Sneha Kulkarni",
        email: "sneha@email.com",
        age: 36,
        gender: "Female",
        phone: "+91 9876543213",
        status: "Active",
        avatar: "assets/avatars/patient-female.png",
      },
    ];

    renderPatients(patients);
  } catch (error) {
    showError(tableBody, "Failed to load patients.");
  }
}

/* ==========================================================
   RENDER PATIENTS
========================================================== */

function renderPatients(patients) {
  const tableBody = document.getElementById("patientTableBody");

  if (patients.length === 0) {
    showEmptyState(tableBody, "No patients found.");

    return;
  }

  tableBody.innerHTML = "";

  patients.forEach((patient) => {
    tableBody.innerHTML += `
      <tr>

        <td>${patient.id}</td>

<td>
  <div class="patient-info">
    <img src="${patient.avatar}" alt="${patient.name}" />

    <div>
      <h4>${patient.name}</h4>
      <p>${patient.email}</p>
    </div>
  </div>
</td>

        <td>${patient.age}</td>

        <td>${patient.gender}</td>

        <td>${patient.phone}</td>

        <td>
          <span class="status ${patient.status.toLowerCase()}">
            ${patient.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-patient">

              <i data-lucide="eye"></i>

            </button>

            <button class="action-btn edit-patient">

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
