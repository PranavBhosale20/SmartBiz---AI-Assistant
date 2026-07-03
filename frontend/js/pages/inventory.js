/* ==========================================================
   INVENTORY PAGE
========================================================== */

async function initializeInventory() {
  const tableBody = document.getElementById("inventoryTableBody");

  if (!tableBody) return;

  loadInventory();
}

/* ==========================================================
   LOAD INVENTORY
========================================================== */

async function loadInventory() {
  const tableBody = document.getElementById("inventoryTableBody");

  showLoading(tableBody, "Loading inventory...");

  try {
    // TODO:
    // const products = await apiCall("/api/products");

    const products = [
      {
        id: "MED001",
        name: "Paracetamol 500mg",
        category: "Tablet",
        quantity: 120,
        price: "₹20",
        status: "In Stock",
      },
      {
        id: "MED002",
        name: "Amoxicillin",
        category: "Capsule",
        quantity: 8,
        price: "₹95",
        status: "Low Stock",
      },
      {
        id: "MED003",
        name: "Vitamin C",
        category: "Tablet",
        quantity: 0,
        price: "₹150",
        status: "Out of Stock",
      },
    ];

    renderInventory(products);
  } catch (error) {
    showError(tableBody, "Failed to load inventory.");
  }
}

/* ==========================================================
   RENDER INVENTORY
========================================================== */

function renderInventory(products) {
  const tableBody = document.getElementById("inventoryTableBody");

  if (products.length === 0) {
    showEmptyState(tableBody, "No products found.");

    return;
  }

  tableBody.innerHTML = "";

  products.forEach((product) => {
    tableBody.innerHTML += `
      <tr>

        <td>${product.name}</td>

        <td>${product.category}</td>

        <td>${product.quantity}</td>

        <td>${product.price}</td>

        <td>
          <span class="status ${product.status.toLowerCase().replace(/\s/g, "-")}">
            ${product.status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button class="action-btn open-modal" data-modal="view-medicine">
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
