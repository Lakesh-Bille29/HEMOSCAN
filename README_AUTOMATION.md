# 🧠 HemoScan Enterprise QA, Performance, & Security Testing Ecosystem

Welcome to the enterprise-grade automated testing ecosystem for **HemoScan**.

---

## 🏛️ Ecosystem Overview

```text
HEMOSCAN/
├── Android-App/                          # Native Android Application (Java + SDK 35 + TFLite)
├── Backend/                              # PHP REST API + Python TFLite AI Inference
├── Web/                                  # React 19 + TypeScript + Vite 8 Web Application
├── Web-Dashboard/                        # HTML/CSS/JS Doctor Portal
│
├── automation/                           # 🧪 Core Test Automation Frameworks
│   ├── appium/                           # 📱 Appium 2.x UiAutomator2 Framework (350 Native Android Tests)
│   ├── selenium/                         # 🌐 Selenium WebDriver Framework (350 Web Tests)
│   └── api/                              # ⚙️ PHP REST API Integration Framework (350 Endpoint Tests)
│
├── load-tests/                           # ⚡ Grafana k6 Performance Suite (Baseline, Normal, Peak, Stress, Soak)
├── security/                             # 🛡️ OWASP Security (Gitleaks, Semgrep, Trivy, ZAP Configs)
├── reports/                              # 📊 Executive HTML & ExcelJS Reporting Engine
└── .github/workflows/                    # 🏆 GitHub Actions CI/CD Quality Gate Workflows
```

---

## 📊 Summary of Test Coverage (Target: 300 - 400 Tests / Module)

| Testing Module | Framework & Tooling | Scenario Target | Output Reports |
| :--- | :--- | :--- | :--- |
| **📱 Native Android App** | Appium 2.x + UiAutomator2 + Mocha + Chai | **350 Scenarios** | `reports/appium/index.html`<br>`HemoScan_Appium_Report.xlsx` |
| **🌐 React Web App** | Selenium WebDriver + Mocha + Chai | **350 Scenarios** | `reports/selenium/index.html`<br>`HemoScan_Web_Report.xlsx` |
| **⚙️ PHP REST Backend** | Node.js + Axios + AJV Schema Validator | **350 Scenarios** | `reports/api/index.html` |
| **⚡ Load & Performance** | Grafana k6 Engine (500 Peak VUs) | **300+ Iterations** | `reports/k6/summary.json` |
| **🛡️ Vulnerability & SAST** | Gitleaks + Semgrep + Trivy + npm audit | **350 Rules** | `reports/security/trivy-report.json` |
| **🏆 Quality Gate** | Consolidated HTML Dashboard | **Full Pipeline** | `HemoScan_Enterprise_QA_Dashboard.html` |

---

## 🚀 Execution Guide

### 1. 📱 Appium Native Android Framework
```bash
cd automation/appium
npm install
npm test
```

### 2. 🌐 Selenium Web Framework
```bash
cd automation/selenium
npm install
npm test
```

### 3. ⚙️ PHP REST API Integration Suite
```bash
cd automation/api
npm install
npm test
```

### 4. ⚡ k6 Load & Performance Suite
```bash
# Smoke test
k6 run load-tests/smoke.js

# Full Peak Load Test
k6 run load-tests/ai-analysis.js
```

### 5. 📊 Generate Consolidated Executive QA Dashboard
```bash
node reports/generate_dashboard.js
```

---

## 🏆 GitHub Actions CI/CD Quality Gate Workflows

The repository contains 6 dedicated production workflows under `.github/workflows/`:

1. `android-appium.yml` — Builds APK, starts emulator, runs 350 Appium tests, uploads reports & APK.
2. `web-selenium.yml` — Builds Vite web app, runs 350 Selenium tests, uploads HTML/Excel reports.
3. `api-tests.yml` — Imports MySQL schema, starts PHP API server, runs 350 API integration tests.
4. `load-test.yml` — Installs k6, executes load testing scenarios, validates SLA thresholds.
5. `vulnerability-scan.yml` — Scans for secrets, code vulnerabilities, and dependency CVEs.
6. `full-quality-gate.yml` — Orchestrates the full pipeline and generates `HemoScan_Enterprise_QA_Dashboard.html`.

---

<div align="center">

Built with ❤️ for HemoScan Quality Assurance & Security Excellence

</div>
