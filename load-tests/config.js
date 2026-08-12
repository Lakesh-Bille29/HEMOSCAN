// k6 Performance Testing Scenarios & Thresholds Configuration
export const options = {
  scenarios: {
    baseline: {
      executor: 'constant-vus',
      vus: 100,
      duration: '1m',
    },
    normal_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '1m', target: 200 },
        { duration: '3m', target: 200 },
        { duration: '1m', target: 0 },
      ],
      gracefulRampDown: '10s',
    },
    peak_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 500 },
        { duration: '6m', target: 500 },
        { duration: '2m', target: 0 },
      ],
    },
    stress_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 1000 },
        { duration: '5m', target: 1000 },
        { duration: '2m', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],    // Error rate must be under 1%
    http_req_duration: ['p(95)<2000'], // P95 latency must be under 2000ms
    checks: ['rate>0.99'],             // Assertions pass rate > 99%
  },
};

export const BASE_URL = __ENV.TEST_TARGET_URL || 'http://127.0.0.1:8000';
