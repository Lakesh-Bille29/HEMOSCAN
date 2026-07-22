<div align="center">

<img src="Web/public/logo.png" alt="HemoScan Logo" width="120" />

# 🧠 HemoScan — AI-Powered Brain Hemorrhage Detection

[![Android](https://img.shields.io/badge/Android-App-3DDC84?style=for-the-badge&logo=android&logoColor=white)](./Android-App)
[![PHP](https://img.shields.io/badge/Backend-PHP-777BB4?style=for-the-badge&logo=php&logoColor=white)](./Backend)
[![React](https://img.shields.io/badge/Web-React+Vite-61DAFB?style=for-the-badge&logo=react&logoColor=black)](./Web)
[![Firebase](https://img.shields.io/badge/Firebase-FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)

> **HemoScan** is an AI-driven clinical tool for detecting brain hemorrhage subtypes from CT scan images. It combines a native Android app, a PHP REST API backend, and a React web dashboard — all powered by TensorFlow Lite deep learning models.

</div>

---

## 🌟 Features

- 🔬 **AI CT Scan Analysis** — Detects and classifies brain hemorrhage subtypes using YOLO + TFLite models
- 📱 **Android App** — Full-featured patient management, scan upload, and result visualization
- 🌐 **Web Dashboard** — Admin & doctor portal built with React 19 + Vite
- 🔔 **Push Notifications** — Firebase Cloud Messaging (FCM) for real-time alerts
- 🔐 **Secure Auth** — OTP-based password reset via email (PHPMailer + Gmail SMTP)
- 🌍 **Multilingual** — Supports English, Hindi, Tamil, Telugu, Kannada, Gujarati, Marathi, Bengali, Spanish
- 📊 **History & Timeline** — Patient scan history with detailed result timelines

---

## 📁 Repository Structure

```
HEMOSCAN/
├── Android-App/          # Native Android application (Java, SDK 35)
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/brainhemorrhage/   # 48 Java source files
│   │       ├── res/                                 # Layouts, drawables, strings (9 languages)
│   │       └── assets/                              # TFLite models
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradle/
│
├── Backend/              # PHP REST API + Python AI inference
│   ├── config.example.php  # ⚠️ Copy to config.php and fill credentials
│   ├── db.php              # Database connection
│   ├── login.php           # Auth endpoints
│   ├── signup.php
│   ├── upload_scan.php     # CT scan upload & trigger AI
│   ├── analyze.php         # AI inference controller
│   ├── inference.py        # Python TFLite inference engine
│   ├── get_scans.php       # Scan history API
│   ├── send_otp.php        # OTP email (PHPMailer)
│   ├── models/             # TFLite AI model files
│   │   ├── Hemorrhage.tflite
│   │   ├── brain_ct_classifier.tflite
│   │   └── hemorrhage_detector.tflite
│   ├── phpmailer/          # PHPMailer library
│   ├── setup_db.sql        # Database schema
│   └── uploads/            # Runtime uploads (gitignored, .gitkeep preserved)
│
├── Web/                  # React 19 + Vite 8 web application
│   ├── src/
│   │   ├── App.tsx             # Main app component
│   │   ├── firebaseConfig.ts   # Firebase configuration
│   │   ├── components/         # Reusable UI components
│   │   ├── services/           # API service layer + tests
│   │   ├── hooks/              # Custom React hooks (FCM)
│   │   └── i18n/               # Internationalization (i18next)
│   ├── public/
│   │   ├── logo.png
│   │   └── firebase-messaging-sw.js
│   ├── package.json
│   └── vite.config.ts
│
└── README.md
```

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Android App** | Java | Android SDK 35 |
| **AI / ML** | TensorFlow Lite (YOLO + Classifier) | Latest |
| **Android HTTP** | Retrofit + Gson | — |
| **Push Notifications** | Firebase Cloud Messaging (FCM) | — |
| **Backend API** | PHP | 8.x |
| **AI Inference** | Python (TFLite Runtime) | 3.x |
| **Database** | MySQL | 8.x |
| **Email / OTP** | PHPMailer + Gmail SMTP | — |
| **Web Frontend** | React 19 + TypeScript | 19.x |
| **Web Build Tool** | Vite | 8.x |
| **Web 3D Viz** | Three.js | 0.169 |
| **Animations** | Framer Motion | 11.x |
| **i18n** | i18next | 26.x |
| **Web Auth** | Firebase | 12.x |

---

## 🚀 Setup Instructions

### Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog or later |
| XAMPP / LAMP | PHP 8.x + MySQL 8.x |
| Python | 3.8+ |
| Node.js | 18+ |
| Git | Any |

---

### 1️⃣ Backend Setup (PHP + Python)

```bash
# 1. Place the Backend/ folder in your web server's htdocs (XAMPP) or www root
cp -r Backend/ /path/to/xampp/htdocs/brainscan_api

# 2. Configure credentials
cd /path/to/xampp/htdocs/brainscan_api
cp config.example.php config.php
# Edit config.php with your DB credentials and Gmail App Password

# 3. Create the database
mysql -u root -p < setup_db.sql

# 4. Install Python dependencies for AI inference
pip install tflite-runtime numpy pillow

# 5. Verify installation
php install.php
```

> **SMTP Setup**: Go to `Gmail → Google Account → Security → 2-Step Verification → App Passwords` and generate a 16-character app password. Add it to `config.php` under `SMTP_PASSWORD`.

---

### 2️⃣ Android App Setup

```bash
# 1. Open Android-App/ in Android Studio
# 2. Wait for Gradle sync to complete

# 3. Update the API base URL
# Edit: app/src/main/java/com/example/brainhemorrhage/api/ApiClient.java
# Set BASE_URL to your server IP or domain, e.g.:
#   http://192.168.1.10/brainscan_api/
#   https://yourdomain.com/brainscan_api/

# 4. Replace google-services.json with your own Firebase project file
# Download from: Firebase Console → Project Settings → Your Apps → google-services.json

# 5. Build and run on device (Android 8.0+ / API 27+)
./gradlew assembleDebug
```

> **Note**: The app uses TFLite models stored in `app/src/main/assets/`. Do not remove or rename them.

---

### 3️⃣ Web App Setup

```bash
cd Web/

# Install dependencies
npm install

# Configure Firebase
# Edit src/firebaseConfig.ts with your Firebase project credentials

# Run development server
npm run dev

# Build for production
npm run build
```

---

## 🔌 API Endpoints Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/login.php` | User login → JWT/session |
| `POST` | `/signup.php` | New user registration |
| `POST` | `/upload_scan.php` | Upload CT scan image |
| `POST` | `/analyze.php` | Run AI inference on scan |
| `GET` | `/get_scans.php` | Fetch patient scan history |
| `POST` | `/send_otp.php` | Send OTP email for password reset |
| `POST` | `/verify_otp.php` | Verify OTP code |
| `POST` | `/reset_password.php` | Reset password with verified OTP |
| `POST` | `/update_profile.php` | Update user profile |
| `POST` | `/change_password.php` | Change password (authenticated) |
| `POST` | `/delete_account.php` | Delete user account |
| `POST` | `/submit_ticket.php` | Submit support ticket |
| `GET` | `/get_tickets.php` | Get support tickets |
| `POST` | `/save_fcm_token.php` | Register FCM push token |
| `POST` | `/send_notification.php` | Send push notification (admin) |

---

## 🤖 AI Model Details

| Model File | Purpose | Size |
|------------|---------|------|
| `brain_ct_classifier.tflite` | Classifies CT scan as hemorrhage or normal | ~10.2 MB |
| `hemorrhage_detector.tflite` | YOLO-based hemorrhage region detection | ~22.4 MB |
| `Hemorrhage.tflite` | Hemorrhage subtype classifier | ~3.8 MB |

The inference pipeline (Python `inference.py`) runs as a subprocess called by `analyze.php` and uses `tflite-runtime` for fast on-server prediction.

---

## 🗄️ Database Schema

Run `Backend/setup_db.sql` to create the `brain_scan_db` database with the following tables:

- `users` — Registered doctors/users
- `scans` — CT scan records with AI results
- `tickets` — Support tickets
- `fcm_tokens` — Firebase push notification tokens

---

## 🔐 Security Notes

- **`config.php` is excluded from version control** — copy `config.example.php` and fill in your credentials
- **`uploads/`** directory is gitignored — contains patient scan images (PII)
- **OTP logs** (`otp_log.txt`) are excluded — contain sensitive OTP codes
- Use HTTPS in production; never expose the API over plain HTTP
- Replace the FCM legacy server key with a Firebase HTTP v1 service account for production

---

## 🧑‍💻 Authors

| Name | Role |
|------|------|
| **B. Lakesh** | Full-stack Developer (Android + Backend + Web) |

---

## 📜 License

This project is developed as part of an academic research project. All rights reserved.

---

<div align="center">

Built with ❤️ for improving clinical brain hemorrhage diagnosis

</div>
