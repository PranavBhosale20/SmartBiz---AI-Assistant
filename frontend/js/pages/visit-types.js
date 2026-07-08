/* ==========================================================
   VISIT TYPES PAGE
========================================================== */

async function initializeVisitTypes() {
  const tableBody = document.getElementById("visitTypeTableBody");

  if (!tableBody) return;

  loadVisitTypes();
}

/* ==========================================================
   LOAD VISIT TYPES
========================================================== */

async function loadVisitTypes() {
  const tableBody = document.getElementById("visitTypeTableBody");

  showLoading(tableBody, "Loading visit types...");

  try {
    const visitTypes = await apiCall("/api/visit-types");

    renderVisitTypes(visitTypes);
  } catch (error) {
    console.error(error);

    showError(tableBody, "Failed to load visit types.");
  }
}

/* ==========================================================
   RENDER VISIT TYPES
========================================================== */

function renderVisitTypes(visitTypes) {
  const tableBody = document.getElementById("visitTypeTableBody");

  if (!visitTypes || visitTypes.length === 0) {
    showEmptyState(tableBody, "No visit types found.");
    return;
  }

  tableBody.innerHTML = "";

  visitTypes.forEach((visitType) => {
    tableBody.innerHTML += `
      <tr>

        <td>${visitType.name}</td>

        <td>₹${visitType.firstVisitPrice}</td>

        <td>₹${visitType.repeatPrice}</td>

        <td>

          <div class="table-actions">

            <button
              class="action-btn open-modal"
              data-modal="view-visit-type"
              data-id="${visitType.id}">

              <i data-lucide="eye"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="add-visit-type"
              data-id="${visitType.id}">

              <i data-lucide="square-pen"></i>

            </button>

            <button
              class="action-btn open-modal"
              data-modal="delete-confirmation"
              data-entity="visit-type"
              data-id="${visitType.id}">

              <i data-lucide="trash-2"></i>

            </button>

          </div>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();

  console.log("✓ Visit Types Loaded");
}
