/* ==========================================================
   SMARTBIZ API UTILITY
========================================================== */

const API_BASE_URL = "http://localhost:8081";

/* ==========================================================
   GENERIC API CALL
========================================================== */

async function apiCall(endpoint, method = "GET", body = null) {
  const token = getToken();

  const options = {
    method,

    headers: {
      "Content-Type": "application/json",
    },
  };

  /* ======================================================
     ATTACH JWT
  ====================================================== */

  if (token) {
    options.headers.Authorization = `Bearer ${token}`;
  }

  /* ======================================================
     REQUEST BODY
  ====================================================== */

  if (body) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(API_BASE_URL + endpoint, options);

    /* ======================================================
       SESSION EXPIRED
    ====================================================== */

    if (response.status === 401) {
      logout();

      throw new Error("Session expired. Please login again.");
    }

    /* ======================================================
       FORBIDDEN
    ====================================================== */

    if (response.status === 403) {
      throw new Error("You are not authorized to perform this action.");
    }

    /* ======================================================
       NO CONTENT
    ====================================================== */

    if (response.status === 204) {
      return null;
    }

    /* ======================================================
       READ RESPONSE
    ====================================================== */

    const data = await response.json().catch(() => null);

    /* ======================================================
       API ERROR
    ====================================================== */

    if (!response.ok) {
      throw new Error(
        data?.message || data?.error || `Request failed (${response.status}).`,
      );
    }

    return data;
  } catch (error) {
    console.error("API Error:", error);

    throw error;
  }
}
