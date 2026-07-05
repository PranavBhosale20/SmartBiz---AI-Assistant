/* ==========================================================
   PATIENT DASHBOARD
========================================================== */

protectPage("PATIENT");

document.addEventListener("DOMContentLoaded", () => {
  initializePatientDashboard();
});

/* ==========================================================
   INITIALIZE
========================================================== */

async function initializePatientDashboard() {
  loadUpcomingAppointments();

  loadRecentBills();
}

/* ==========================================================
   UPCOMING APPOINTMENTS
========================================================== */

async function loadUpcomingAppointments() {
  const container = document.getElementById("upcomingAppointments");

  if (!container) return;

  showLoading(container, "Loading appointments...");

  try {
    // TODO
    // const appointments =
    // await apiCall(`/api/appointments/user/${userId}`);

    const appointments = [];

    if (appointments.length === 0) {
      showEmptyState(container, "No upcoming appointments.");
      return;
    }

    container.innerHTML = "";

    // Render cards here later
  } catch (error) {
    showError(container, "Unable to load appointments.");
  }
}

/* ==========================================================
   RECENT BILLS
========================================================== */

async function loadRecentBills() {
  const container = document.getElementById("recentBills");

  if (!container) return;

  showLoading(container, "Loading bills...");

  try {
    // TODO
    // const bills = await apiCall(...);

    const bills = [];

    if (bills.length === 0) {
      showEmptyState(container, "No recent bills.");
      return;
    }

    container.innerHTML = "";

    // Render bill cards later
  } catch (error) {
    showError(container, "Unable to load bills.");
  }
}
