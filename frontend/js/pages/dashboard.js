/* ==========================================================
   STAFF DASHBOARD
========================================================== */

document.addEventListener("DOMContentLoaded", () => {
  protectPage("STAFF");

  initializeDashboard();
});

/* ==========================================================
   INITIALIZE
========================================================== */

async function initializeDashboard() {
  await Promise.all([loadDashboardSummary(), loadCurrentUser()]);
}

/* ==========================================================
   DASHBOARD SUMMARY
========================================================== */

async function loadDashboardSummary() {
  try {
    const summary = await apiCall("/api/dashboard/summary");

    setStatValue("totalPatients", summary.totalPatients);

    setStatValue("totalDoctors", summary.totalDoctors);

    setStatValue("appointmentsToday", summary.appointmentsToday);

    setStatValue("lowStockItems", summary.lowStockItems);

    console.log("✓ Dashboard Summary Loaded");
  } catch (error) {
    console.error(error);

    showToast("Unable to load dashboard summary.", "error");
  }
}

/* ==========================================================
   CURRENT USER
========================================================== */

async function loadCurrentUser() {
  try {
    const user = await apiCall("/api/users/me");

    const heroName = document.getElementById("heroName");

    if (heroName) {
      heroName.textContent = user.name;
    }

    console.log("✓ Current User Loaded");
  } catch (error) {
    console.error(error);
  }
}

/* ==========================================================
   UPDATE CARD
========================================================== */

function setStatValue(id, value) {
  const element = document.getElementById(id);

  if (!element) return;

  element.textContent = value;
}
