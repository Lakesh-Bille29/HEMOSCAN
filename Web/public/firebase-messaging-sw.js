// firebase-messaging-sw.js
// Service Worker for FCM background push notifications.
// This file MUST be at the root /public/ folder to be served at the root URL.
//
// ────────────────────────────────────────────────────────────────────────────────
// SETUP: Replace the firebaseConfig values below with your project's values from:
//   Firebase Console → Project Settings → General → Your apps → Web app → SDK setup
// ────────────────────────────────────────────────────────────────────────────────

importScripts('https://www.gstatic.com/firebasejs/10.14.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.14.0/firebase-messaging-compat.js');

// ── Firebase config (replace with your project values) ───────────────────────
firebase.initializeApp({
  apiKey:            "YOUR_API_KEY",
  authDomain:        "YOUR_PROJECT_ID.firebaseapp.com",
  projectId:         "YOUR_PROJECT_ID",
  storageBucket:     "YOUR_PROJECT_ID.appspot.com",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId:             "YOUR_APP_ID"
});

const messaging = firebase.messaging();

// ── Background message handler ───────────────────────────────────────────────
// Called when a message arrives and the HemoScan tab is NOT in the foreground.
messaging.onBackgroundMessage(function(payload) {
  console.log('[HemoScan SW] Background message received:', payload);

  const notificationTitle = payload.notification?.title || 'HemoScan';
  const notificationOptions = {
    body:    payload.notification?.body || 'You have a new notification.',
    icon:    '/favicon.ico',
    badge:   '/favicon.ico',
    tag:     payload.data?.type || 'hemoscan-notification',
    data:    payload.data,
    actions: [
      { action: 'open',    title: 'Open HemoScan' },
      { action: 'dismiss', title: 'Dismiss' }
    ]
  };

  return self.registration.showNotification(notificationTitle, notificationOptions);
});

// ── Notification click handler ───────────────────────────────────────────────
self.addEventListener('notificationclick', function(event) {
  event.notification.close();

  if (event.action === 'dismiss') return;

  // Navigate to the app (or specific page based on payload)
  const urlToOpen = self.location.origin + '/';

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then(function(clientList) {
      for (const client of clientList) {
        if (client.url === urlToOpen && 'focus' in client) {
          return client.focus();
        }
      }
      return clients.openWindow(urlToOpen);
    })
  );
});
