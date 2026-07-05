/* ==========================================================
   SMARTBIZ THEME MANAGER
========================================================== */

const THEME_KEY = "theme";

/* ==========================================================
   INITIALIZE
========================================================== */

function initializeTheme() {
  const savedTheme = localStorage.getItem(THEME_KEY) || "light";

  applyTheme(savedTheme, false);
}

/* ==========================================================
   APPLY THEME
========================================================== */

function applyTheme(theme, showMessage = false) {
  document.documentElement.setAttribute("data-theme", theme);

  localStorage.setItem(THEME_KEY, theme);

  updateThemeButton(theme);

  if (showMessage) {
    showToast(
      `${theme.charAt(0).toUpperCase() + theme.slice(1)} mode enabled.`,
      "success",
    );
  }
}

/* ==========================================================
   TOGGLE THEME
========================================================== */

function toggleTheme() {
  const currentTheme = getTheme();

  const newTheme = currentTheme === "light" ? "dark" : "light";

  applyTheme(newTheme, true);
}

/* ==========================================================
   GET THEME
========================================================== */

function getTheme() {
  return document.documentElement.getAttribute("data-theme") || "light";
}

/* ==========================================================
   UPDATE ICON
========================================================== */

function updateThemeButton(theme) {
  const button = document.getElementById("themeButton");

  if (!button) return;

  const icon = button.querySelector("i");

  if (!icon) return;

  icon.setAttribute("data-lucide", theme === "dark" ? "sun" : "moon");

  if (window.lucide) {
    lucide.createIcons();
  }
}

/* ==========================================================
   AUTO INIT
========================================================== */

document.addEventListener("DOMContentLoaded", initializeTheme);
