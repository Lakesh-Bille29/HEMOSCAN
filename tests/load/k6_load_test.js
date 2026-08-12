import http from 'k6/http';
import { check, sleep } from 'k6';

// k6 Load Test Configuration: 300+ Virtual User Iterations
export const options = {
  stages: [
    { duration: '30s', target: 50 },  # Ramp-up to 50 VUs
    { duration: '1m',  target: 100 }, # Sustained load 100 VUs
    { duration: '30s', target: 0 },   # Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    http_req_failed: ['rate<0.01'],   # Request failure rate must be under 1%
  },
};

const BASE_URL = __ENV.TEST_TARGET_URL || 'http://localhost:8000';

export default function () {
  const endpoints = [
    '/login.php',
    '/get_scans.php',
    '/get_dashboard.php',
    '/get_notifications.php',
    '/send_otp.php',
    '/submit_ticket.php'
  ];

  for (let i = 0; i < endpoints.length; i++) {
    const res = http.get(`${BASE_URL}${endpoints[i]}`);
    check(res, {
      'status is 200 or expected': (r) => r.status === 200 || r.status === 401 || r.status === 400,
      'response duration < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(0.1);
  }
}
