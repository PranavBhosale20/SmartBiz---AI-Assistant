/* ==========================================================
   SMARTBIZ MODAL MANAGER
========================================================== */

const modalContainer = document.getElementById("modalContainer");

let currentModal = null;

/* ==========================================================
   OPEN MODAL
========================================================== */

async function openModal(modalName) {
  try {
    const response = await fetch(`components/modals/${modalName}.html`);

    const html = await response.text();
    console.log(html);

    modalContainer.innerHTML = html;

    currentModal = modalContainer.querySelector(".modal-overlay");

    if (currentModal) {
      currentModal.classList.add("show");
    }

    lucide.createIcons();

    bindModalEvents();
  } catch (error) {
    console.error("Unable to load modal :", error);
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

  if (!button) {
    return;
  }

  const modalName = button.dataset.modal;

  if (modalName) {
    openModal(modalName);
  }
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
