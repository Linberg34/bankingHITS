import { initializeApp } from 'firebase/app';
import { getMessaging } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: 'AIzaSyBgUF60IYTR0z_6hWpB9RUxX_z7aAKQpr8',
  authDomain: 'bankinghits-74feb.firebaseapp.com',
  projectId: 'bankinghits-74feb',
  storageBucket: 'bankinghits-74feb.firebasestorage.app',
  messagingSenderId: '260749117408',
  appId: '1:260749117408:web:3db7c557127d3404348d85',
};

export const firebaseApp = initializeApp(firebaseConfig);
export const messaging = getMessaging(firebaseApp);
export const VAPID_KEY =
  'BPmyNgF4ZdsWjE4inl7-lM40E3xadoloUpy_baAXYkLaNt_EoKXJYaIGuspykmkIsLOd0x7p2o7X3W64sixsYYo';
