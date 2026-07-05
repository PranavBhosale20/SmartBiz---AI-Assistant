/* ==========================================================
   SMARTBIZ UI UTILITY
========================================================== */

let toastContainer = null;

/* ==========================================================
   GET TOAST CONTAINER
========================================================== */

function getToastContainer() {
  if (!toastContainer) {
    toastContainer = document.createElement("div");

    toastContainer.className = "toast-container";

    document.body.appendChild(toastContainer);
  }

  return toastContainer;
}

/* ==========================================================
   SHOW TOAST
========================================================== */

function showToast(message, type = "info", title = "") {
  const container = getToastContainer();

  const toast = document.createElement("div");

  toast.className = `toast ${type} show`;

  let icon = "info";

  let defaultTitle = "Information";

  switch (type) {
    case "success":
      icon = "check-circle";
      defaultTitle = "Success";
      break;

    case "error":
      icon = "circle-x";
      defaultTitle = "Error";
      break;

    case "warning":
      icon = "triangle-alert";
      defaultTitle = "Warning";
      break;

    default:
      icon = "info";
      defaultTitle = "Information";
  }

  toast.innerHTML = `
    <div class="toast-icon">
      <i data-lucide="${icon}"></i>
    </div>

    <div class="toast-content">
      <div class="toast-title">${title || defaultTitle}</div>

      <div class="toast-message">${message}</div>
    </div>

    <div class="toast-close">
      <i data-lucide="x"></i>
    </div>
  `;

  container.appendChild(toast);

  if (window.lucide) {
    lucide.createIcons();
  }

  const removeToast = () => {
    toast.classList.remove("show");

    toast.classList.add("hide");

    setTimeout(() => {
      toast.remove();
    }, 300);
  };

  toast.querySelector(".toast-close").addEventListener("click", removeToast);

  setTimeout(removeToast, 3500);
}

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
