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
    // TODO:
    // const bills = await apiCall("/api/bills");

    const bills = [
      {
        id: "B001",
        patient: "Rahul Sharma",
        doctor: "Dr. Mehta",
        amount: "₹520",
        date: "12 Jul 2026",
        status: "Paid",
      },
      {
        id: "B002",
        patient: "Priya Patil",
        doctor: "Dr. Joshi",
        amount: "₹750",
        date: "13 Jul 2026",
        status: "Pending",
      },
      {
        id: "B003",
        patient: "Amit Joshi",
        doctor: "Dr. Kulkarni",
        amount: "₹320",
        date: "14 Jul 2026",
        status: "Paid",
      },
    ];

    renderBills(bills);
  } catch (error) {
    showError(tableBody, "Failed to load bills.");
  }
}

/* ==========================================================
   RENDER BILLS
========================================================== */

function renderBills(bills) {
  const tableBody = document.getElementById("billingTableBody");

  if (bills.length === 0) {
    showEmptyState(tableBody, "No bills found.");

    return;
  }

  tableBody.innerHTML = "";

  bills.forEach((bill) => {
    tableBody.innerHTML += `
      <tr>

        <td>${bill.id}</td>

        <td>${bill.patient}</td>

        <td>${bill.amount}</td>

        <td>${bill.date}</td>

        <td>
          <span class="status ${bill.status.toLowerCase()}">
            ${bill.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button class="action-btn open-modal" data-modal="view-bill">
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
