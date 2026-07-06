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
      initializeAddPatientModal?.(modalData);
      break;

    case "view-patient":
      initializeViewPatientModal?.(modalData);
      break;

    case "edit-patient":
      initializeEditPatientModal?.(modalData);
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
