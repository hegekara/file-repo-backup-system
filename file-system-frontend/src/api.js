import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/rest/api",
});

API.interceptors.request.use((req) => {
  const token = localStorage.getItem("jwtToken");
  if (token) {
    req.headers.Authorization = `Bearer ${token}`;
    console.log(req.headers.Authorization);
  }
  return req;
});

export default API;