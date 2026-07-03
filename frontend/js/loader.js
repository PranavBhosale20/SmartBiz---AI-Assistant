async function loadComponent(id, file) {
  const element = document.getElementById(id);

  if (!element) {
    return;
  }

  try {
    const response = await fetch(file);

    if (!response.ok) {
      throw new Error(`Failed to load ${file}`);
    }

    const html = await response.text();

    element.innerHTML = html;

    /* Initialize Lucide icons */

    if (window.lucide) {
      lucide.createIcons();
    }
  } catch (error) {
    console.error(`Error loading ${file}:`, error);
  }
}

async function loadComponents() {
  await loadComponent("sidebar", "components/sidebar.html");

  await loadComponent("header", "components/header.html");

  await loadComponent("footer", "components/footer.html");

  await loadComponent("modalContainer", "components/modal.html");
}
