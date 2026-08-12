import http from 'k6/http';
import { check } from 'k6';

export function makeRequest(url, method = 'GET', body = null, headers = {}) {
  const params = {
    headers: Object.assign({ 'Content-Type': 'application/json' }, headers),
  };

  let res;
  if (method === 'POST') {
    res = http.post(url, body, params);
  } else {
    res = http.get(url, params);
  }

  check(res, {
    'status is 200 or 401': (r) => r.status === 200 || r.status === 401 || r.status === 400,
    'response duration < 2000ms': (r) => r.timings.duration < 2000,
  });

  return res;
}
