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
    const prescriptions = await apiCall("/api/prescriptions");

    renderPrescriptions(prescriptions);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load prescriptions.");
  }
}

/* ==========================================================
   RENDER PRESCRIPTIONS
========================================================== */

function renderPrescriptions(prescriptions) {
  const tableBody = document.getElementById("prescriptionTableBody");

  if (!prescriptions || prescriptions.length === 0) {
    showEmptyState(tableBody, "No prescriptions found.");
    return;
  }

  tableBody.innerHTML = "";

  prescriptions.forEach((prescription) => {
    const medicineCount = prescription.items ? prescription.items.length : 0;

    const createdDate = new Date(prescription.createdAt).toLocaleDateString();

    tableBody.innerHTML += `
      <tr>

        <td>#${prescription.id}</td>

        <td>${prescription.patientName}</td>

        <td>${prescription.doctorName}</td>

        <td>${medicineCount} Medicine(s)</td>

        <td>${createdDate}</td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-prescription"
              data-id="${prescription.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-entity="prescription"
              data-id="${prescription.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Prescriptions Loaded");
}
