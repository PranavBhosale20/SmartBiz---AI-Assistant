/* ==========================================================
   ADD VISIT TYPE MODAL
========================================================== */

function initializeAddVisitTypeModal() {
  document.getElementById("visitTypeId").value = "";

  document.getElementById("visitTypeModalTitle").textContent = "Add Visit Type";

  document.getElementById("visitTypeModalSubtitle").textContent =
    "Create a new visit type.";

  document.getElementById("saveVisitTypeBtnText").textContent =
    "Save Visit Type";

  const saveBtn = document.getElementById("saveVisitTypeBtn");

  saveBtn.replaceWith(saveBtn.cloneNode(true));

  document
    .getElementById("saveVisitTypeBtn")
    .addEventListener("click", saveVisitType);
}

/* ==========================================================
   EDIT VISIT TYPE MODAL
========================================================== */

async function initializeEditVisitTypeModal(modalData) {
  if (!modalData?.id) return;

  try {
    const visitType = await apiCall(`/api/visit-types/${modalData.id}`);

    document.getElementById("visitTypeId").value = visitType.id;

    document.getElementById("visitTypeName").value = visitType.name;

    document.getElementById("firstVisitPrice").value =
      visitType.firstVisitPrice;

    document.getElementById("repeatVisitPrice").value = visitType.repeatPrice;

    document.getElementById("visitTypeModalTitle").textContent =
      "Edit Visit Type";

    document.getElementById("visitTypeModalSubtitle").textContent =
      "Update visit type details.";

    document.getElementById("saveVisitTypeBtnText").textContent =
      "Update Visit Type";

    const saveBtn = document.getElementById("saveVisitTypeBtn");

    saveBtn.replaceWith(saveBtn.cloneNode(true));

    document
      .getElementById("saveVisitTypeBtn")
      .addEventListener("click", saveVisitType);
  } catch (error) {
    showToast(error.message || "Failed to load visit type.", "error");
  }
}

/* ==========================================================
   SAVE VISIT TYPE
========================================================== */

async function saveVisitType() {
  const id = document.getElementById("visitTypeId").value;

  const visitType = {
    name: document.getElementById("visitTypeName").value.trim(),
    firstVisitPrice: Number(document.getElementById("firstVisitPrice").value),
    repeatPrice: Number(document.getElementById("repeatVisitPrice").value),
  };

  if (!visitType.name || !visitType.firstVisitPrice || !visitType.repeatPrice) {
    showToast("Please fill all fields.", "warning");
    return;
  }

  try {
    if (id) {
      await apiCall(`/api/visit-types/${id}`, "PUT", visitType);

      showToast("Visit type updated successfully.", "success");
    } else {
      await apiCall("/api/visit-types", "POST", visitType);

      showToast("Visit type added successfully.", "success");
    }

    closeModal();

    loadVisitTypes();
  } catch (error) {
    showToast(error.message || "Failed to save visit type.", "error");
  }
}

/* ==========================================================
   VIEW VISIT TYPE MODAL
========================================================== */

function initializeViewVisitTypeModal(modalData) {
  showToast("View Visit Type coming soon.", "info");
}

/* ==========================================================
   DELETE VISIT TYPE
========================================================== */

async function deleteVisitType(id) {
  try {
    await apiCall(`/api/visit-types/${id}`, "DELETE");

    closeModal();

    showToast("Visit type deleted successfully.", "success");

    loadVisitTypes();
  } catch (error) {
    showToast(error.message || "Failed to delete visit type.", "error");
  }
}
