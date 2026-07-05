/* ==========================================================
   AUTH UTILITIES
========================================================== */

const TOKEN_KEY = "token";
const USERNAME_KEY = "username";
const FULLNAME_KEY = "fullName";
const ROLE_KEY = "role";
const GENDER_KEY = "gender";

/* ==========================================================
   SAVE LOGIN
========================================================== */

function saveLogin(token, username, fullName, role, gender = null) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);

  if (fullName) {
    localStorage.setItem(FULLNAME_KEY, fullName);
  } else {
    localStorage.removeItem(FULLNAME_KEY);
  }

  localStorage.setItem(ROLE_KEY, role);

  if (gender) {
    localStorage.setItem(GENDER_KEY, gender);
  } else {
    localStorage.removeItem(GENDER_KEY);
  }
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

function getFullName() {
  return localStorage.getItem(FULLNAME_KEY);
}

function getRole() {
  return localStorage.getItem(ROLE_KEY);
}

function getGender() {
  return localStorage.getItem(GENDER_KEY);
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
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(FULLNAME_KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(GENDER_KEY);

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
