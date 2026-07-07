/* ==========================================================
   SMARTBIZ MODAL MANAGER
========================================================== */

const modalContainer = document.getElementById("modalContainer");

let currentModal = null;

/* ==========================================================
   OPEN MODAL
========================================================== */

async function openModal(modalName, modalData = null) {
  try {
    const response = await fetch(`components/modals/${modalName}.html`);

    const html = await response.text();

    modalContainer.innerHTML = html;

    currentModal = modalContainer.querySelector(".modal-overlay");

    if (currentModal) {
      currentModal.classList.add("show");
    }

    lucide.createIcons();

    bindModalEvents();

    initializeModalPage(modalName, modalData);
  } catch (error) {
    console.error("Unable to load modal:", error);
  }
}

/* ==========================================================
   CLOSE MODAL
========================================================== */

function closeModal() {
  modalContainer.innerHTML = "";

  currentModal = null;
}

/* ==========================================================
   MODAL EVENTS
========================================================== */

function bindModalEvents() {
  const closeButton = modalContainer.querySelector(".modal-close");

  if (closeButton) {
    closeButton.addEventListener("click", closeModal);
  }

  const cancelButton = modalContainer.querySelector(".btn-secondary");

  if (cancelButton) {
    cancelButton.addEventListener("click", closeModal);
  }

  if (currentModal) {
    currentModal.addEventListener("click", (event) => {
      if (event.target === currentModal) {
        closeModal();
      }
    });
  }
}

/* ==========================================================
   OPEN BUTTON EVENTS
========================================================== */

document.addEventListener("click", (event) => {
  const button = event.target.closest(".open-modal");

  if (!button) return;

  openModal(button.dataset.modal, {
    id: button.dataset.id || null,
    entity: button.dataset.entity || null,
  });
});

/* ==========================================================
   ESC KEY CLOSE
========================================================== */

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && currentModal) {
    closeModal();
  }
});

/* ==========================================================
   INITIALIZE PAGE MODAL
========================================================== */

function initializeModalPage(modalName, modalData) {
  switch (modalName) {
    /* ======================================================
       DOCTORS
    ====================================================== */

    case "add-doctor":
      if (modalData?.id) {
        initializeEditDoctorModal?.(modalData);
      } else {
        initializeAddDoctorModal?.();
      }
      break;

    case "view-doctor":
      initializeViewDoctorModal?.(modalData);
      break;

    case "delete-confirmation":
      initializeDeleteConfirmationModal?.(modalData);
      break;

    /* ======================================================
   PATIENTS
====================================================== */

    case "add-patient":
      if (modalData?.id) {
        initializeEditPatientModal?.(modalData);
      } else {
        initializeAddPatientModal?.();
      }
      break;

    case "view-patient":
      initializeViewPatientModal?.(modalData);
      break;

    /* ======================================================
   APPOINTMENTS
====================================================== */
    case "add-appointment":
      if (modalData?.id) {
        initializeEditAppointmentModal?.(modalData);
      } else {
        initializeAddAppointmentModal?.();
      }
      break;
  }
}

/* ==========================================================
   GLOBAL MODAL API
========================================================== */

window.Modal = {
  open: openModal,
  close: closeModal,
};

/* ==========================================================
   INITIALIZE
========================================================== */

function initializeModal() {
  // Modal manager is initialized through global event listeners.
}

console.log("✓ Modal Manager Loaded");

/* ==========================================================
   DELETE CONFIRMATION MODAL
========================================================== */

function initializeDeleteConfirmationModal(modalData) {
  if (!modalData?.id) return;

  console.log("Delete Modal:", modalData);

  const title = document.getElementById("deleteModalTitle");
  const subtitle = document.getElementById("deleteModalSubtitle");
  const message = document.getElementById("deleteModalMessage");
  const confirmButton = document.getElementById("confirmDeleteBtn");
  const confirmText = document.getElementById("confirmDeleteText");

  switch (modalData.entity) {
    case "doctor":
      title.textContent = "Delete Doctor";
      subtitle.textContent = "This action cannot be undone.";
      message.textContent =
        "Are you sure you want to permanently delete this doctor?";
      confirmText.textContent = "Delete Doctor";
      break;

    case "patient":
      title.textContent = "Delete Patient";
      subtitle.textContent = "This action cannot be undone.";
      message.textContent =
        "Are you sure you want to permanently delete this patient?";
      confirmText.textContent = "Delete Patient";
      break;

    default:
      title.textContent = "Delete Record";
      subtitle.textContent = "This action cannot be undone.";
      message.textContent =
        "Are you sure you want to permanently delete this record?";
      confirmText.textContent = "Delete";
      break;
  }

  confirmButton.replaceWith(confirmButton.cloneNode(true));

  document
    .getElementById("confirmDeleteBtn")
    .addEventListener("click", async () => {
      console.log("Delete button pressed:", modalData);

      try {
        switch (modalData.entity) {
          case "doctor":
            await deleteDoctor(modalData.id);
            break;

          case "patient":
            await deletePatient(modalData.id);
            break;

          default:
            showToast("Delete action not implemented.", "warning");
            break;
        }
      } catch (error) {
        console.error(error);
        showToast(error.message || "Delete failed.", "error");
      }
    });
}
