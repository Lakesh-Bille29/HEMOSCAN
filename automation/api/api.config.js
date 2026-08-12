module.exports = {
  baseUrl: process.env.API_BASE_URL || 'http://127.0.0.1:8000',
  endpoints: [
    '/login.php',
    '/signup.php',
    '/upload_scan.php',
    '/analyze.php',
    '/get_scans.php',
    '/send_otp.php',
    '/verify_otp.php',
    '/reset_password.php',
    '/update_profile.php',
    '/change_password.php',
    '/delete_account.php',
    '/submit_ticket.php',
    '/get_tickets.php',
    '/save_fcm_token.php',
    '/send_notification.php'
  ],
  slaThresholds: {
    login: 2000,
    scans: 3000,
    otp: 5000,
    aiInference: 10000
  },
  targets: {
    totalTestsTarget: 350
  }
};
