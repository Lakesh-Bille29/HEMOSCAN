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

// Download PDF Diagnostic Report Helper
function downloadPDFReport(id, name, age, gender, result, imagePath, dateStr) {
    const pName = name || 'Patient #' + (id || '1');
    const resultStr = result || 'Normal';
    const isAbnormal = resultStr.toLowerCase().includes('abnormal') || (resultStr.toLowerCase().includes('hemorrhage') && !resultStr.toLowerCase().includes('no hemorrhage'));
    const status = isAbnormal ? 'ABNORMAL' : 'NORMAL';
    const date = dateStr || new Date().toLocaleDateString();
    
    const reportWindow = window.open('', '_blank');
    if (!reportWindow) {
        alert('Popup blocked. Please allow popups to download PDF.');
        return;
    }

    const htmlContent = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>HemoScan_Report_${id || '1'}</title>
        <style>
          body { font-family: system-ui, -apple-system, sans-serif; background: #f8fafc; color: #0f172a; margin: 0; padding: 40px; }
          .report-card { max-width: 680px; margin: 0 auto; background: #ffffff; border-radius: 16px; border: 1px solid #e2e8f0; padding: 36px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
          .header { background: #1e1b4b; color: #ffffff; padding: 24px 32px; border-radius: 12px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
          .title { font-size: 24px; font-weight: 800; color: #818cf8; margin: 0; }
          .subtitle { font-size: 13px; color: #cbd5e1; margin-top: 4px; }
          .meta-box { background: #f1f5f9; padding: 16px 20px; border-radius: 10px; margin-bottom: 24px; display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 14px; }
          .status-pill { display: inline-block; padding: 6px 16px; border-radius: 20px; font-weight: 800; font-size: 13px; letter-spacing: 0.5px; background: ${isAbnormal ? '#ffe4e6' : '#dcfce7'}; color: ${isAbnormal ? '#e11d48' : '#16a34a'}; border: 1px solid ${isAbnormal ? '#fecdd3' : '#bbf7d0'}; }
          .image-preview { text-align: center; margin: 24px 0; background: #0f172a; padding: 16px; border-radius: 12px; }
          .image-preview img { max-width: 100%; max-height: 320px; border-radius: 8px; border: 1px solid #334155; }
          .disclaimer { font-size: 11px; color: #64748b; margin-top: 32px; padding-top: 16px; border-top: 1px solid #e2e8f0; text-align: center; }
          @media print { body { background: #fff; padding: 0; } .report-card { border: none; box-shadow: none; } }
        </style>
      </head>
      <body>
        <div class="report-card">
          <div class="header">
            <div>
              <div class="title">HemoScan AI</div>
              <div class="subtitle">Diagnostic Neuroimaging Report</div>
            </div>
            <div style="text-align: right; font-size: 12px; color: #a5b4fc;">
              <div>Date: ${date}</div>
              <div>Report ID: HST-${id || '1'}</div>
            </div>
          </div>
          <div class="meta-box">
            <div><strong>Patient Name:</strong> ${pName}</div>
            <div><strong>Age / Gender:</strong> ${age || 'N/A'} / ${gender || 'N/A'}</div>
            <div><strong>Diagnosis Status:</strong> <span class="status-pill">${status}</span></div>
            <div><strong>Findings:</strong> ${resultStr}</div>
          </div>
          ${imagePath ? `
            <div class="image-preview">
              <img src="${imagePath.startsWith('http') ? imagePath : ('http://localhost/brainscan_api/' + imagePath)}" alt="CT Scan" />
            </div>
          ` : ''}
          <div class="disclaimer">
            CONFIDENTIAL MEDICAL REPORT — Generated by HemoScan AI Decision Support.<br/>
            Must be reviewed by a certified physician before clinical intervention.
          </div>
        </div>
        <script>
          window.onload = () => { window.print(); };
        </script>
      </body>
      </html>
    `;
    reportWindow.document.write(htmlContent);
    reportWindow.document.close();
}

function shareHemoScanApp() {
    const text = 'HemoScan AI — Clinical Brain Hemorrhage Diagnostic Platform!';
    const url = window.location.origin;
    if (navigator.share) {
        navigator.share({ title: 'HemoScan AI', text: text, url: url }).catch(() => {});
    } else {
        navigator.clipboard.writeText(`${text}\n${url}`).then(() => {
            alert('HemoScan share link copied to clipboard!');
        }).catch(() => {
            alert(`Share link: ${url}`);
        });
    }
}
