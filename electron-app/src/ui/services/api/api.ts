import axios from 'axios'

// Base API URL - points to the running backend on localhost:8080
// Using `/api` to match the backend routes used by the load test and the server
const API_URL = 'http://localhost:8080/api' // Replace with your API URL

export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// api.interceptors.request.use((config) => {
//   const token = localStorage.getItem("token");
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// });
