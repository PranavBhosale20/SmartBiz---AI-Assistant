document.addEventListener("DOMContentLoaded", async () => {
  // Load reusable HTML components
  await loadComponents();

  // Initialize reusable components
  initializeSidebar();
  initializeHeader();
  initializeFooter();
  initializeModal();

  // Render Lucide icons
  lucide.createIcons();

  // Initialize current page (if available)
  const page = document.documentElement.dataset.page;

  switch (page) {
    case "dashboard":
      if (typeof initializeDashboard === "function") {
        initializeDashboard();
      }
      break;

    case "patients":
      if (typeof initializePatients === "function") {
        initializePatients();
      }
      break;

    case "doctors":
      if (typeof initializeDoctors === "function") {
        initializeDoctors();
      }
      break;

    case "appointments":
      if (typeof initializeAppointments === "function") {
        initializeAppointments();
      }
      break;

    case "inventory":
      if (typeof initializeInventory === "function") {
        initializeInventory();
      }
      break;

    case "billing":
      if (typeof initializeBilling === "function") {
        initializeBilling();
      }
      break;

    case "prescriptions":
      if (typeof initializePrescriptions === "function") {
        initializePrescriptions();
      }
      break;

    case "command-console":
      if (typeof initializeCommandConsole === "function") {
        initializeCommandConsole();
      }
      break;
  }
});
