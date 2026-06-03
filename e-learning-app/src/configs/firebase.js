import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";

// Đây là "Chìa khóa" kết nối hệ thống Firebase của bạn
const firebaseConfig = {
  apiKey: "AIzaSyCCY1wnnsR2BVmm4vEz1JyU5RJm-WMaa44",
  authDomain: "quanlynhahang-b5d1d.firebaseapp.com",
  databaseURL: "https://quanlynhahang-b5d1d-default-rtdb.firebaseio.com",
  projectId: "quanlynhahang-b5d1d",
  storageBucket: "quanlynhahang-b5d1d.firebasestorage.app",
  messagingSenderId: "562819063140",
  appId: "1:562819063140:web:e94bb0ff6f975135046e71",
  measurementId: "G-3VZX1Y1MP8"
};

const app = initializeApp(firebaseConfig);


export const database = getDatabase(app);