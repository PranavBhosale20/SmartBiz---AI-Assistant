/* ==========================================================
   AUTH UTILITIES
========================================================== */

const TOKEN_KEY = "token";
const USERNAME_KEY = "username";
const ROLE_KEY = "role";

/* ==========================================================
   SAVE LOGIN
========================================================== */

function saveLogin(token, username, role) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);
  localStorage.setItem(ROLE_KEY, role);
}

/* ==========================================================
   GETTERS
========================================================== */

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}

function getRole() {
  return localStorage.getItem(ROLE_KEY);
}

/* ==========================================================
   LOGIN STATUS
========================================================== */

function isLoggedIn() {
  return !!getToken();
}

/* ==========================================================
   LOGOUT
========================================================== */

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

/* ==========================================================
   JWT PARSER
========================================================== */

function parseJwt(token) {
  try {
    const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");

    return JSON.parse(atob(base64));
  } catch (error) {
    return null;
  }
}

/* ==========================================================
   TOKEN EXPIRY
========================================================== */

function isTokenExpired() {
  const token = getToken();

  if (!token) return true;

  const payload = parseJwt(token);

  if (!payload || !payload.exp) return true;

  return Date.now() >= payload.exp * 1000;
}
