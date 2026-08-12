import { sleep } from 'k6';
import { BASE_URL, options } from './config.js';
import { makeRequest } from './helpers.js';

export { options };

export default function () {
  makeRequest(`${BASE_URL}/analyze.php`, 'POST', JSON.stringify({ mock: true }));
  sleep(1.0);
}
