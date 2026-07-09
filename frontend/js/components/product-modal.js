/* ==========================================================
   ADD PRODUCT MODAL
========================================================== */

function initializeAddProductModal() {
  document.getElementById("productId").value = "";

  document.getElementById("medicineModalTitle").textContent = "Add Medicine";

  document.getElementById("medicineModalSubtitle").textContent =
    "Add a medicine to the inventory.";

  document.getElementById("saveProductBtnText").textContent = "Save Medicine";

  const saveBtn = document.getElementById("saveProductBtn");

  saveBtn.replaceWith(saveBtn.cloneNode(true));

  document
    .getElementById("saveProductBtn")
    .addEventListener("click", saveProduct);
}

/* ==========================================================
   EDIT PRODUCT MODAL
========================================================== */

async function initializeEditProductModal(modalData) {
  if (!modalData?.id) return;

  try {
    const product = await apiCall(`/api/products/${modalData.id}`);

    document.getElementById("productId").value = product.id;

    document.getElementById("medicineName").value = product.name;

    document.getElementById("medicineCategory").value = product.category;

    document.getElementById("medicinePrice").value = product.price;

    document.getElementById("medicineQuantity").value = product.quantity;

    document.getElementById("medicineModalTitle").textContent = "Edit Medicine";

    document.getElementById("medicineModalSubtitle").textContent =
      "Update medicine details.";

    document.getElementById("saveProductBtnText").textContent =
      "Update Medicine";

    const saveBtn = document.getElementById("saveProductBtn");

    saveBtn.replaceWith(saveBtn.cloneNode(true));

    document
      .getElementById("saveProductBtn")
      .addEventListener("click", saveProduct);
  } catch (error) {
    showToast(error.message || "Failed to load medicine.", "error");
  }
}

/* ==========================================================
   SAVE PRODUCT
========================================================== */

async function saveProduct() {
  const id = document.getElementById("productId").value;

  const product = {
    name: document.getElementById("medicineName").value.trim(),
    category: document.getElementById("medicineCategory").value.trim(),
    quantity: Number(document.getElementById("medicineQuantity").value),
    price: Number(document.getElementById("medicinePrice").value),
  };

  if (
    !product.name ||
    !product.category ||
    product.quantity < 0 ||
    product.price < 0
  ) {
    showToast("Please fill all medicine details.", "warning");
    return;
  }

  try {
    if (id) {
      await apiCall(`/api/products/${id}`, "PUT", product);

      showToast("Medicine updated successfully.", "success");
    } else {
      await apiCall("/api/products", "POST", product);

      showToast("Medicine added successfully.", "success");
    }

    closeModal();

    loadProducts();
  } catch (error) {
    showToast(error.message || "Failed to save medicine.", "error");
  }
}

/* ==========================================================
   VIEW PRODUCT MODAL
========================================================== */

function initializeViewProductModal(modalData) {
  if (!modalData?.id) return;

  viewProduct(modalData.id);
}

/* ==========================================================
   VIEW PRODUCT
========================================================== */

async function viewProduct(id) {
  try {
    const product = await apiCall(`/api/products/${id}`);

    showToast(
      `Medicine: ${product.name}
Category: ${product.category}
Price: ₹${product.price}
Quantity: ${product.quantity}`,
      "info",
    );
  } catch (error) {
    showToast(error.message || "Failed to load medicine.", "error");
  }
}

/* ==========================================================
   DELETE PRODUCT
========================================================== */

async function deleteProduct(id) {
  try {
    await apiCall(`/api/products/${id}`, "DELETE");

    closeModal();

    showToast("Medicine deleted successfully.", "success");

    loadProducts();
  } catch (error) {
    showToast(error.message || "Failed to delete medicine.", "error");
  }
}
