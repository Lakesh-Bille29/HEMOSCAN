import { sleep } from 'k6';
import { BASE_URL, options } from './config.js';
import { makeRequest } from './helpers.js';

export { options };

export default function () {
  makeRequest(`${BASE_URL}/login.php`, 'POST', JSON.stringify({ email: 'lakeshb5037.sse@saveetha.com', password: 'ValidPass123' }));
  sleep(0.5);
}
