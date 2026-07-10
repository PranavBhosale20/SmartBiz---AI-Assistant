/* ==========================================================
   PRESCRIPTION MODAL
========================================================== */

let prescriptionItems = [];

/* ==========================================================
   ADD PRESCRIPTION MODAL
========================================================== */

async function initializeAddPrescriptionModal() {
  prescriptionItems = [];

  document.getElementById("prescriptionId").value = "";

  document.getElementById("prescriptionNotes").value = "";

  document.getElementById("prescriptionItemsTableBody").innerHTML = "";

  await loadAppointmentDropdown();

  await loadMedicineDropdown();

  const addBtn = document.getElementById("addMedicineBtn");

  addBtn.replaceWith(addBtn.cloneNode(true));

  document
    .getElementById("addMedicineBtn")
    .addEventListener("click", addMedicineToPrescription);

  const saveBtn = document.getElementById("savePrescriptionBtn");

  saveBtn.replaceWith(saveBtn.cloneNode(true));

  document
    .getElementById("savePrescriptionBtn")
    .addEventListener("click", savePrescription);
}

/* ==========================================================
   LOAD APPOINTMENTS
========================================================== */

async function loadAppointmentDropdown() {
  const select = document.getElementById("prescriptionAppointment");

  select.innerHTML = `<option value="">Select Appointment</option>`;

  const appointments = await apiCall("/api/appointments");

  appointments.forEach((appointment) => {
    select.innerHTML += `
      <option value="${appointment.id}">
        #${appointment.id}
        - ${appointment.userName}
        - ${appointment.doctorName}
      </option>
    `;
  });
}

/* ==========================================================
   LOAD MEDICINES
========================================================== */

async function loadMedicineDropdown() {
  const select = document.getElementById("prescriptionProduct");

  select.innerHTML = `<option value="">Select Medicine</option>`;

  const medicines = await apiCall("/api/products/available");

  medicines.forEach((medicine) => {
    select.innerHTML += `
      <option value="${medicine.id}">
        ${medicine.name}
        (${medicine.quantity} available)
      </option>
    `;
  });
}

/* ==========================================================
   ADD MEDICINE
========================================================== */

function addMedicineToPrescription() {
  const productSelect = document.getElementById("prescriptionProduct");
  const quantityInput = document.getElementById("prescriptionQuantity");
  const dosageInput = document.getElementById("prescriptionDosage");

  const productId = Number(productSelect.value);
  const quantity = Number(quantityInput.value);
  const dosage = dosageInput.value.trim();

  if (!productId || quantity <= 0 || !dosage) {
    showToast("Please select medicine, quantity and dosage.", "warning");
    return;
  }

  const productName = productSelect.options[productSelect.selectedIndex].text;

  prescriptionItems.push({
    productId,
    productName,
    quantityPrescribed: quantity,
    dosageInstructions: dosage,
  });

  renderPrescriptionItems();

  productSelect.value = "";
  quantityInput.value = "";
  dosageInput.value = "";
}

/* ==========================================================
   RENDER MEDICINES
========================================================== */

function renderPrescriptionItems() {
  const tableBody = document.getElementById("prescriptionItemsTableBody");

  tableBody.innerHTML = "";

  prescriptionItems.forEach((item, index) => {
    tableBody.innerHTML += `
      <tr>

        <td>${item.productName}</td>

        <td>${item.quantityPrescribed}</td>

        <td>${item.dosageInstructions}</td>

        <td>

          <button
            class="action-btn"
            onclick="removeMedicine(${index})">

            <i data-lucide="trash-2"></i>

          </button>

        </td>

      </tr>
    `;
  });

  lucide.createIcons();
}

/* ==========================================================
   REMOVE MEDICINE
========================================================== */

function removeMedicine(index) {
  prescriptionItems.splice(index, 1);

  renderPrescriptionItems();
}

/* ==========================================================
   SAVE PRESCRIPTION
========================================================== */

async function savePrescription() {
  const appointmentId = Number(
    document.getElementById("prescriptionAppointment").value,
  );

  const notes = document.getElementById("prescriptionNotes").value.trim();

  if (!appointmentId) {
    showToast("Please select an appointment.", "warning");
    return;
  }

  if (prescriptionItems.length === 0) {
    showToast("Please add at least one medicine.", "warning");
    return;
  }

  const prescription = {
    appointmentId,
    notes,
    items: prescriptionItems.map((item) => ({
      productId: item.productId,
      quantityPrescribed: item.quantityPrescribed,
      dosageInstructions: item.dosageInstructions,
    })),
  };

  try {
    await apiCall("/api/prescriptions", "POST", prescription);

    closeModal();

    showToast("Prescription created successfully.", "success");

    loadPrescriptions();
  } catch (error) {
    showToast(error.message || "Failed to create prescription.", "error");
  }
}

/* ==========================================================
   VIEW PRESCRIPTION MODAL
========================================================== */

function initializeViewPrescriptionModal(modalData) {
  if (!modalData?.id) return;

  viewPrescription(modalData.id);
}

/* ==========================================================
   VIEW PRESCRIPTION
========================================================== */

async function viewPrescription(id) {
  try {
    const prescription = await apiCall(`/api/prescriptions/${id}`);

    document.getElementById("viewPrescriptionId").textContent =
      "#" + prescription.id;

    document.getElementById("viewAppointmentId").textContent =
      "#" + prescription.appointmentId;

    document.getElementById("viewPrescriptionPatient").textContent =
      prescription.patientName;

    document.getElementById("viewPrescriptionDoctor").textContent =
      prescription.doctorName;

    document.getElementById("viewPrescriptionDate").textContent = new Date(
      prescription.createdAt,
    ).toLocaleString();

    document.getElementById("viewPrescriptionNotes").value =
      prescription.notes || "";

    const tableBody = document.getElementById("viewPrescriptionItemsTableBody");

    tableBody.innerHTML = "";

    prescription.items.forEach((item) => {
      tableBody.innerHTML += `
        <tr>

          <td>${item.productName}</td>

          <td>${item.quantityPrescribed}</td>

          <td>${item.dosageInstructions}</td>

        </tr>
      `;
    });
  } catch (error) {
    showToast(error.message || "Failed to load prescription.", "error");
  }
}
