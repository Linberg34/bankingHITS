importScripts('https://www.gstatic.com/firebasejs/12.12.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/12.12.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: 'AIzaSyBgUF60IYTR0z_6hWpB9RUxX_z7aAKQpr8',
  authDomain: 'bankinghits-74feb.firebaseapp.com',
  projectId: 'bankinghits-74feb',
  storageBucket: 'bankinghits-74feb.firebasestorage.app',
  messagingSenderId: '260749117408',
  appId: '1:260749117408:web:3db7c557127d3404348d85',
});

const messaging = firebase.messaging();

// Обработка фоновых уведомлений (браузер свёрнут)
messaging.onBackgroundMessage((payload) => {
  const title = payload.notification?.title ?? 'Банк';
  const body = payload.notification?.body ?? '';
  self.registration.showNotification(title, { body });
});
