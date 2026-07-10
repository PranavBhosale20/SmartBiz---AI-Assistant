/* ==========================================================
   BILL MODAL
========================================================== */

async function initializeCreateBillModal() {
  const appointmentSelect = document.getElementById("billAppointment");

  if (!appointmentSelect) return;

  appointmentSelect.innerHTML = `<option value="">Select Appointment</option>`;

  try {
    const appointments = await apiCall("/api/appointments");

    appointments.forEach((appointment) => {
      appointmentSelect.innerHTML += `
        <option value="${appointment.id}">
          #${appointment.id}
          - ${appointment.userName}
          - ${appointment.doctorName}
        </option>
      `;
    });

    const generateBtn = document.getElementById("generateBillBtn");

    generateBtn.replaceWith(generateBtn.cloneNode(true));

    document
      .getElementById("generateBillBtn")
      .addEventListener("click", generateBill);
  } catch (error) {
    showToast(error.message || "Failed to load appointments.", "error");
  }
}

/* ==========================================================
   GENERATE BILL
========================================================== */

async function generateBill() {
  const appointmentId = document.getElementById("billAppointment").value;

  if (!appointmentId) {
    showToast("Please select an appointment.", "warning");
    return;
  }

  try {
    await apiCall(`/api/bills/generate/${appointmentId}`, "POST");

    closeModal();

    showToast("Bill generated successfully.", "success");

    loadBills();
  } catch (error) {
    showToast(error.message || "Failed to generate bill.", "error");
  }
}

/* ==========================================================
   VIEW BILL MODAL
========================================================== */

async function initializeViewBillModal(modalData) {
  console.log(modalData);

  if (!modalData?.appointment) return;

  viewBill(modalData.appointment);
}

/* ==========================================================
   VIEW BILL
========================================================== */

async function viewBill(appointmentId) {
  try {
    const bill = await apiCall(`/api/bills/appointment/${appointmentId}`);

    document.getElementById("viewBillId").textContent = "#" + bill.id;

    document.getElementById("viewBillAppointment").textContent =
      "#" + bill.appointmentId;

    document.getElementById("viewBillPatient").textContent = bill.userName;

    document.getElementById("viewBillDoctor").textContent = bill.doctorName;

    document.getElementById("viewVisitFee").textContent =
      "₹" + bill.visitFee.toFixed(2);

    document.getElementById("viewMedicineCost").textContent =
      "₹" + bill.medicineCost.toFixed(2);

    document.getElementById("viewGrandTotal").textContent =
      "₹" + bill.grandTotal.toFixed(2);

    document.getElementById("viewBillStatus").textContent = bill.status;

    document.getElementById("viewBillDate").textContent = new Date(
      bill.createdAt,
    ).toLocaleString();
  } catch (error) {
    showToast(error.message || "Failed to load bill.", "error");
  }
}

/* ==========================================================
   PAY BILL MODAL
========================================================== */

function initializePayBillModal(modalData) {
  if (!modalData?.id) return;

  const confirmBtn = document.getElementById("confirmPayBillBtn");

  if (!confirmBtn) return;

  confirmBtn.replaceWith(confirmBtn.cloneNode(true));

  document
    .getElementById("confirmPayBillBtn")
    .addEventListener("click", () => markBillAsPaid(modalData.id));
}

/* ==========================================================
   MARK BILL AS PAID
========================================================== */

async function markBillAsPaid(id) {
  try {
    await apiCall(`/api/bills/${id}/pay`, "PUT");

    closeModal();

    showToast("Bill marked as paid.", "success");

    loadBills();
  } catch (error) {
    showToast(error.message || "Failed to update bill.", "error");
  }
}
