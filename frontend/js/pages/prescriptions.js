/* ==========================================================
   PRESCRIPTIONS PAGE
========================================================== */

async function initializePrescriptions() {
  const tableBody = document.getElementById("prescriptionTableBody");

  if (!tableBody) return;

  loadPrescriptions();
}

/* ==========================================================
   LOAD PRESCRIPTIONS
========================================================== */

async function loadPrescriptions() {
  const tableBody = document.getElementById("prescriptionTableBody");

  showLoading(tableBody, "Loading prescriptions...");

  try {
    // TODO:
    // const prescriptions = await apiCall("/api/prescriptions");

    const prescriptions = [
      {
        id: "PR001",
        patient: "Rahul Sharma",
        doctor: "Dr. Mehta",
        medicineCount: 4,
        date: "12 Jul 2026",
        status: "Issued",
      },
      {
        id: "PR002",
        patient: "Priya Patil",
        doctor: "Dr. Joshi",
        medicineCount: 3,
        date: "13 Jul 2026",
        status: "Issued",
      },
      {
        id: "PR003",
        patient: "Amit Joshi",
        doctor: "Dr. Kulkarni",
        medicineCount: 5,
        date: "14 Jul 2026",
        status: "Pending",
      },
    ];

    renderPrescriptions(prescriptions);
  } catch (error) {
    showError(tableBody, "Failed to load prescriptions.");
  }
}

/* ==========================================================
   RENDER PRESCRIPTIONS
========================================================== */

function renderPrescriptions(prescriptions) {
  const tableBody = document.getElementById("prescriptionTableBody");

  if (prescriptions.length === 0) {
    showEmptyState(tableBody, "No prescriptions found.");

    return;
  }

  tableBody.innerHTML = "";

  prescriptions.forEach((prescription) => {
    tableBody.innerHTML += `
      <tr>

        <td>${prescription.id}</td>

        <td>${prescription.patient}</td>

        <td>${prescription.doctor}</td>

<td>

    <button
        class="btn btn-text open-modal"
        data-modal="view-prescription">

        View (${prescription.medicineCount})

    </button>

</td>
        <td>${prescription.date}</td>

        <td>
          <span class="status ${prescription.status.toLowerCase()}">
            ${prescription.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button class="action-btn open-modal" 
            data-modal="view-prescription">
              <i data-lucide="eye"></i>
            </button>

            <button class="action-btn">
              <i data-lucide="download"></i>
            </button>

            <button class="action-btn">
              <i data-lucide="printer"></i>
            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();
}
