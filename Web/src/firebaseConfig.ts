// firebaseConfig.ts
// ────────────────────────────────────────────────────────────────────────────────
// SETUP INSTRUCTIONS:
//   1. Go to https://console.firebase.google.com
//   2. Select your project → Project Settings → General → Your apps → Web app
//   3. Copy the firebaseConfig object values into this file
//   4. Also update firebase-messaging-sw.js with the same values
// ────────────────────────────────────────────────────────────────────────────────

export const firebaseConfig = {
  apiKey:            import.meta.env.VITE_FIREBASE_API_KEY            || "YOUR_API_KEY",
  authDomain:        import.meta.env.VITE_FIREBASE_AUTH_DOMAIN        || "YOUR_PROJECT_ID.firebaseapp.com",
  projectId:         import.meta.env.VITE_FIREBASE_PROJECT_ID         || "YOUR_PROJECT_ID",
  storageBucket:     import.meta.env.VITE_FIREBASE_STORAGE_BUCKET     || "YOUR_PROJECT_ID.appspot.com",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "YOUR_MESSAGING_SENDER_ID",
  appId:             import.meta.env.VITE_FIREBASE_APP_ID             || "YOUR_APP_ID",
  // VAPID key from Firebase Console → Project Settings → Cloud Messaging → Web Push certificates
  vapidKey:          import.meta.env.VITE_FIREBASE_VAPID_KEY          || "YOUR_VAPID_KEY",
};

export const isFirebaseConfigured = (): boolean => {
  return !firebaseConfig.apiKey.startsWith("YOUR_");
};
