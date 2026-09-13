// =====================================================
// SmartLab360 - JWT Authentication Helper
// =====================================================


// =====================================================
// GET TOKEN
// =====================================================

function getToken() {

    return localStorage.getItem("token");
}


// =====================================================
// GET USER
// =====================================================

function getCurrentUser() {

    const userString =
        localStorage.getItem("user");

    if (!userString) {
        return null;
    }

    try {

        return JSON.parse(userString);

    } catch (error) {

        console.error(
            "Invalid user data",
            error
        );

        return null;
    }
}


// =====================================================
// CHECK LOGIN
// =====================================================

function requireLogin() {

    const token =
        getToken();

    const user =
        getCurrentUser();

    if (!token || !user) {

        window.location.href =
            "login.html";

        return false;
    }

    return true;
}


// =====================================================
// AUTHENTICATED FETCH
// =====================================================

async function authFetch(
    url,
    options = {}
) {

    const token =
        getToken();


    // No token
    if (!token) {

        window.location.href =
            "login.html";

        return null;
    }


    // Existing headers
    const headers =
        options.headers || {};


    // Add JWT
    options.headers = {

        ...headers,

        "Authorization":
            "Bearer " + token

    };


    // Add Content-Type only when body exists
    if (options.body &&
        !options.headers["Content-Type"]) {

        options.headers["Content-Type"] =
            "application/json";
    }


    const response =
        await fetch(
            url,
            options
        );


    // ===============================================
    // TOKEN EXPIRED / INVALID
    // ===============================================

    if (
        response.status === 401 ||
        response.status === 403
    ) {

        localStorage.removeItem(
            "token"
        );

        localStorage.removeItem(
            "user"
        );

        window.location.href =
            "login.html";

        return null;
    }


    return response;
}


// =====================================================
// LOGOUT
// =====================================================

function logout() {

    localStorage.removeItem(
        "token"
    );

    localStorage.removeItem(
        "user"
    );

    window.location.href =
        "login.html";
}


// =====================================================
// ADMIN CHECK
// =====================================================

function isAdmin() {

    const user =
        getCurrentUser();

    return user &&
        user.role === "ADMIN";
}


// =====================================================
// GET USER ID
// =====================================================

function getUserId() {

    const user =
        getCurrentUser();

    return user
        ? user.userId
        : null;
}


// =====================================================
// GET USER ROLE
// =====================================================

function getUserRole() {

    const user =
        getCurrentUser();

    return user
        ? user.role
        : null;
}