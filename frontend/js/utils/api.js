/* ==========================================================
   SMARTBIZ API UTILITY
========================================================== */

const API_BASE_URL = "http://localhost:8081";

/* ==========================================================
   GENERIC API CALL
========================================================== */

async function apiCall(endpoint, method = "GET", body = null) {
  const token = localStorage.getItem("token");

  const options = {
    method: method,

    headers: {
      "Content-Type": "application/json",
    },
  };

  // Attach JWT if available
  if (token) {
    options.headers["Authorization"] = `Bearer ${token}`;
  }

  // Attach request body
  if (body) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(API_BASE_URL + endpoint, options);

    // Unauthorized / Forbidden
    if (response.status === 401 || response.status === 403) {
      localStorage.clear();

      window.location.href = "login.html";

      return null;
    }

    // Other server errors
    if (!response.ok) {
      throw new Error(`API Error : ${response.status}`);
    }

    // No Content
    if (response.status === 204) {
      return null;
    }

    return await response.json();
  } catch (error) {
    console.error("API Request Failed :", error);

    throw error;
  }
}
