<div align="center">

<img src="web/public/logo.png" alt="HemoScan Logo" width="120" />

# 🧠 HemoScan — AI-Powered Brain Hemorrhage Detection

[![Android](https://img.shields.io/badge/Android-App-3DDC84?style=for-the-badge&logo=android&logoColor=white)](./app)
[![PHP](https://img.shields.io/badge/Backend-PHP-777BB4?style=for-the-badge&logo=php&logoColor=white)](./backend)
[![React](https://img.shields.io/badge/Web-React+Vite-61DAFB?style=for-the-badge&logo=react&logoColor=black)](./web)
[![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge&logo=docker&logoColor=white)](./docker-compose.yml)
[![Firebase](https://img.shields.io/badge/Firebase-FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)

> **HemoScan** is an AI-driven clinical tool for detecting brain hemorrhage subtypes from CT scan images. It combines a native Android app, a PHP REST API backend with Python TFLite AI inference, and a React web dashboard — all dockerized for rapid local and cloud deployment.

</div>

---

## 🌟 Features

- 🔬 **AI CT Scan Analysis** — Detects and classifies brain hemorrhage subtypes using YOLO + TFLite models
- 📱 **Android App** — Full-featured patient management, scan upload, and result visualization
- 🌐 **Web Dashboard** — Admin & doctor portal built with React 19 + Vite
- 🐳 **Dockerized Deployment** — Complete Docker & Docker Compose setup (Web + PHP API + MariaDB)
- 🔔 **Push Notifications** — Firebase Cloud Messaging (FCM) for real-time alerts
- 🔐 **Secure Auth** — OTP-based password reset via email (PHPMailer + Gmail SMTP)
- 🌍 **Multilingual** — Supports English, Hindi, Tamil, Telugu, Kannada, Gujarati, Marathi, Bengali, Spanish
- 📊 **History & Timeline** — Patient scan history with detailed result timelines

---

## 📁 Repository Structure

```text
HEMOSCAN/
├── app/                  # Native Android application (Java, SDK 35)
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/brainhemorrhage/   # Java source code
│   │       ├── res/                                 # Layouts, drawables, strings (9 languages)
│   │       └── assets/                              # TFLite models
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradle/
│
├── web/                  # React 19 + Vite 8 web application
│   ├── src/
│   │   ├── App.tsx             # Main app component
│   │   ├── components/         # Reusable UI components
│   │   ├── services/           # API service layer + tests
│   │   └── i18n/               # Internationalization (i18next)
│   ├── public/
│   ├── Dockerfile              # Multi-stage Web Dockerfile (Node -> Nginx)
│   ├── nginx.conf              # Production Nginx routing & reverse proxy
│   ├── package.json
│   └── vite.config.ts
│
├── backend/              # PHP REST API + Python AI inference
│   ├── config.example.php  # ⚠️ Copy to config.php and fill credentials
│   ├── db.php              # Database connection (supports Docker envs)
│   ├── analyze.php         # AI inference controller
│   ├── inference.py        # Python TFLite inference engine
│   ├── Dockerfile          # PHP 8.2 Apache + Python 3 TFLite Dockerfile
│   ├── models/             # TFLite AI model files
│   ├── phpmailer/          # PHPMailer library
│   ├── setup_db.sql        # Database schema
│   └── uploads/            # Runtime uploads (.gitkeep preserved)
│
├── docker-compose.yml    # Root Docker Compose orchestration
├── .gitignore            # Clean gitignore
├── .dockerignore         # Docker context exclusions
└── README.md
```

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Android App** | Java | Android SDK 35 |
| **AI / ML** | TensorFlow Lite (YOLO + Classifier) | Latest |
| **Backend API** | PHP | 8.2 |
| **AI Inference** | Python (TFLite Runtime / ai-edge-litert) | 3.11 |
| **Database** | MariaDB / MySQL | 10.11 / 8.0 |
| **Web Frontend** | React 19 + TypeScript | 19.x |
| **Web Build Tool** | Vite | 8.x |
| **Containerization** | Docker & Docker Compose | 3.8+ |

---

## 🐳 Docker Quickstart (Recommended)

Run the entire HemoScan ecosystem (Web App, PHP Backend API, and MariaDB Database) with a single command:

```bash
# 1. Clone the repository
git clone https://github.com/Lakesh-Bille29/HEMOSCAN.git
cd HEMOSCAN

# 2. Build and launch all containers
docker compose up -d --build

# 3. Access your services:
#    - Web Application: http://localhost:3000
#    - Backend REST API: http://localhost:8000
#    - Database (MariaDB): localhost:3306
```

To stop the services:
```bash
docker compose down
```

---

## 🚀 Local Development Setup

### 1️⃣ Backend Setup (PHP + Python + XAMPP)

```bash
# Copy backend code to your web server htdocs
cp -r backend/ /path/to/xampp/htdocs/brainscan_api

cd /path/to/xampp/htdocs/brainscan_api
cp config.example.php config.php
# Edit config.php with your credentials

# Import database schema
mysql -u root -p < setup_db.sql

# Install Python dependencies for AI inference
pip install ai-edge-litert numpy Pillow
```

### 2️⃣ Web App Setup

```bash
cd web/
npm install
npm run dev     # Starts Vite dev server at http://localhost:5173
```

### 3️⃣ Android App Setup

Open `app/` in Android Studio, sync Gradle, and build the APK:
```bash
cd app/
./gradlew assembleDebug
```

---

## 🔌 API Endpoints Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/login.php` | User authentication |
| `POST` | `/signup.php` | New doctor registration |
| `POST` | `/upload_scan.php` | Upload CT scan image |
| `POST` | `/analyze.php` | Run AI inference pipeline |
| `GET`  | `/get_scans.php` | Fetch patient scan history |
| `POST` | `/send_otp.php` | Send OTP email for password reset |
| `POST` | `/verify_otp.php` | Verify OTP code |
| `POST` | `/reset_password.php` | Reset password |

---

## 🧑‍💻 Authors

| Name | Role |
|------|------|
| **B. Lakesh** | Full-stack Developer (Android + Backend + Web) |

---

## 📜 License

Academic research project. All rights reserved.
