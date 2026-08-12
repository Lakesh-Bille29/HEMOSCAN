import { sleep } from 'k6';
import { BASE_URL } from './config.js';
import { makeRequest } from './helpers.js';

export const options = {
  vus: 5,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
  },
};

export default function () {
  makeRequest(`${BASE_URL}/login.php`, 'POST', JSON.stringify({ email: 'lakeshb5037.sse@saveetha.com', password: 'ValidPass123' }));
  sleep(1);
  makeRequest(`${BASE_URL}/get_scans.php?doctor_email=lakeshb5037.sse@saveetha.com`);
  sleep(1);
  makeRequest(`${BASE_URL}/get_dashboard.php?doctor_email=lakeshb5037.sse@saveetha.com`);
  sleep(1);
}
