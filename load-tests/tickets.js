import { sleep } from 'k6';
import { BASE_URL, options } from './config.js';
import { makeRequest } from './helpers.js';

export { options };

export default function () {
  makeRequest(`${BASE_URL}/get_tickets.php?doctor_email=lakeshb5037.sse@saveetha.com`);
  sleep(0.5);
}
