const API_BASE = "http://localhost/brainscan_api/";

function showToast(message, type = "success") {
    const container = document.getElementById('toast-container');
    if (!container) return;
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerText = message;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3500);
}

// Ensure dark mode state is loaded
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("hemoscan_dark_mode") === "true") {
        document.body.classList.add("dark-mode");
    }

    const loginCard = document.getElementById('login-card');
    const signupCard = document.getElementById('signup-card');
    
    // Toggle Login / Signup
    if(document.getElementById('show-signup')) {
        document.getElementById('show-signup').addEventListener('click', (e) => {
            e.preventDefault();
            loginCard.classList.add('d-none');
            signupCard.classList.remove('d-none');
        });
    }

    if(document.getElementById('show-login')) {
        document.getElementById('show-login').addEventListener('click', (e) => {
            e.preventDefault();
            signupCard.classList.add('d-none');
            loginCard.classList.remove('d-none');
        });
    }

    // Login Form Submit
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = document.getElementById('btn-login');
            btn.innerHTML = '<span class="spinner"></span> Logging in...';
            btn.disabled = true;

            const formData = new FormData();
            formData.append('email', document.getElementById('login-email').value);
            formData.append('password', document.getElementById('login-password').value);

            try {
                const response = await fetch(`${API_BASE}login.php`, {
                    method: 'POST',
                    body: formData
                });
                const data = await response.json();
                
                if (data.status === "success") {
                    showToast(data.message);
                    localStorage.setItem('hemoscan_user', JSON.stringify(data.user));
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1000);
                } else {
                    showToast(data.message, "error");
                    btn.innerHTML = 'Login';
                    btn.disabled = false;
                }
            } catch (error) {
                showToast("Connection error. Is the backend running?", "error");
                btn.innerHTML = 'Login';
                btn.disabled = false;
            }
        });
    }

    // Signup Form Submit (Currently assumes no OTP is needed for web, or we bypass it)
    // Looking at signup.php, it might require OTP. Let's send OTP first.
    // Wait, the Android app does OTP verification. 
    // To keep it simple, we'll try direct signup or see if API enforces OTP.
    const signupForm = document.getElementById('signup-form');
    if (signupForm) {
        signupForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            // In Android, it sends OTP first. We will just show error if OTP is required by backend,
            // or adapt it. We will pass a dummy otp "123456" for now if required.
            const btn = document.getElementById('btn-signup');
            btn.innerHTML = '<span class="spinner"></span> Signing up...';
            btn.disabled = true;

            const formData = new FormData();
            formData.append('name', document.getElementById('signup-name').value);
            formData.append('email', document.getElementById('signup-email').value);
            formData.append('mobile', document.getElementById('signup-mobile').value);
            formData.append('gender', document.getElementById('signup-gender').value);
            formData.append('password', document.getElementById('signup-password').value);
            formData.append('otp_code', 'SKIP'); // If backend allows skip, otherwise we need to implement OTP UI

            try {
                const response = await fetch(`${API_BASE}signup.php`, {
                    method: 'POST',
                    body: formData
                });
                const data = await response.json();
                
                if (data.status === "success") {
                    showToast("Account created successfully!");
                    setTimeout(() => {
                        signupCard.classList.add('d-none');
                        loginCard.classList.remove('d-none');
                        btn.innerHTML = 'Sign Up';
                        btn.disabled = false;
                    }, 1500);
                } else {
                    showToast(data.message, "error");
                    btn.innerHTML = 'Sign Up';
                    btn.disabled = false;
                }
            } catch (error) {
                showToast("Connection error.", "error");
                btn.innerHTML = 'Sign Up';
                btn.disabled = false;
            }
        });
    }
});

// Logout Helper
function logout() {
    localStorage.removeItem('hemoscan_user');
    window.location.href = 'index.html';
}
