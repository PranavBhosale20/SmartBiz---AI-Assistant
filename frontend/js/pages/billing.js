/* ==========================================================
   BILLING PAGE
========================================================== */

async function initializeBilling() {
  const tableBody = document.getElementById("billingTableBody");

  if (!tableBody) return;

  loadBills();
}

/* ==========================================================
   LOAD BILLS
========================================================== */

async function loadBills() {
  const tableBody = document.getElementById("billingTableBody");

  showLoading(tableBody, "Loading bills...");

  try {
    const bills = await apiCall("/api/bills");

    renderBills(bills);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load bills.");
  }
}

/* ==========================================================
   RENDER BILLS
========================================================== */

function renderBills(bills) {
  const tableBody = document.getElementById("billingTableBody");

  if (!bills || bills.length === 0) {
    showEmptyState(tableBody, "No bills found.");
    return;
  }

  tableBody.innerHTML = "";

  bills.forEach((bill) => {
    const createdDate = new Date(bill.createdAt).toLocaleDateString();

    tableBody.innerHTML += `
      <tr>

        <td>#${bill.id}</td>

        <td>${bill.userName}</td>

        <td>${bill.doctorName}</td>

        <td>₹${bill.grandTotal.toFixed(2)}</td>

        <td>

          <span class="status ${bill.status.toLowerCase()}">
            ${bill.status}
          </span>

        </td>

        <td>${createdDate}</td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-bill"
              data-id="${bill.id}"
              data-appointment="${bill.appointmentId}">

              <i data-lucide="eye"></i>

            </button>

            ${
              bill.status === "UNPAID"
                ? `
            <button
              class="action-btn open-modal"
              data-modal="pay-bill"
              data-id="${bill.id}">

              <i data-lucide="badge-indian-rupee"></i>

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

  console.log("✓ Bills Loaded");
}
