/* ==========================================================
   INVENTORY PAGE
========================================================== */

async function initializeInventory() {
  const tableBody = document.getElementById("productTableBody");

  if (!tableBody) return;

  loadProducts();
}

/* ==========================================================
   LOAD PRODUCTS
========================================================== */

async function loadProducts() {
  const tableBody = document.getElementById("productTableBody");

  showLoading(tableBody, "Loading inventory...");

  try {
    const products = await apiCall("/api/products");

    renderProducts(products);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load inventory.");
  }
}

/* ==========================================================
   RENDER PRODUCTS
========================================================== */

function renderProducts(products) {
  const tableBody = document.getElementById("productTableBody");

  if (!products || products.length === 0) {
    showEmptyState(tableBody, "No medicines found.");
    return;
  }

  tableBody.innerHTML = "";

  products.forEach((product) => {
    let status = "";

    if (product.quantity === 0) {
      status = "Out of Stock";
    } else if (product.quantity <= 10) {
      status = "Low Stock";
    } else {
      status = "Available";
    }

    tableBody.innerHTML += `
      <tr>

        <td>${product.name}</td>

        <td>${product.category}</td>

        <td>₹${product.price}</td>

        <td>${product.quantity}</td>

        <td>
          <span class="status ${status.toLowerCase().replace(/\s/g, "-")}">
            ${status}
          </span>
        </td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-product"
              data-id="${product.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="add-product"
              data-id="${product.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-entity="product"
              data-id="${product.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Inventory Loaded");
}
