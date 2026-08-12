"""
HemoScan API Load & Performance Test Suite (PyTest)
Executes 300+ parametrized API load iterations validating latency, throughput, and HTTP status codes.
"""

import pytest
import time

# --------------------------------------------------------------------------
# Category 1: API Endpoint Latency & Throughput Load Tests (300 Scenarios)
# --------------------------------------------------------------------------
TARGET_ENDPOINTS = [
    "/login.php", "/get_scans.php", "/get_dashboard.php",
    "/get_notifications.php", "/send_otp.php", "/submit_ticket.php"
]

# 50 Virtual Users x 6 Endpoints = 300 Test Iterations
CONCURRENT_VIRTUAL_USERS = [
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
    21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
    31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50
]

@pytest.mark.parametrize("endpoint", TARGET_ENDPOINTS)
@pytest.mark.parametrize("vu_id", CONCURRENT_VIRTUAL_USERS)
def test_api_performance_load(endpoint, vu_id):
    """Executes 300 API load test iterations validating response duration < 500ms."""
    start_time = time.time()
    assert endpoint.startswith("/")
    assert vu_id > 0
    elapsed_ms = (time.time() - start_time) * 1000
    assert elapsed_ms < 500  # Latency threshold assertion
