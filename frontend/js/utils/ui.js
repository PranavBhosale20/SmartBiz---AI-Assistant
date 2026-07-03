/* ==========================================================
   SMARTBIZ UI UTILITY
========================================================== */

/* ==========================================================
   SHOW LOADING
========================================================== */

function showLoading(element, message = "Loading...") {
  element.innerHTML = `
    <div class="ui-loading">
      <p>${message}</p>
    </div>
  `;
}

/* ==========================================================
   SHOW EMPTY STATE
========================================================== */

function showEmptyState(element, message = "No data available.") {
  element.innerHTML = `
    <div class="ui-empty">
      <p>${message}</p>
    </div>
  `;
}

/* ==========================================================
   SHOW ERROR
========================================================== */

function showError(element, message = "Something went wrong.") {
  element.innerHTML = `
    <div class="ui-error">
      <p>${message}</p>
    </div>
  `;
}
