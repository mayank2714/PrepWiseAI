import { Routes, Route } from "react-router";
import "./App.css";
import Login from "./features/auth/pages/Login";
import Register from "./features/auth/pages/Register";
import { AuthProvider } from "./features/auth/context/authContext";
import ProtectedRoute from "./features/auth/components/ProtectedRoute";
import Home from "./features/reports/pages/Home";
import { ReportProvider } from "./features/reports/context/reportContext";
import Report from "./features/reports/pages/Report";
import { ToastProvider } from "./features/common/context/ToastContext";
import ToastContainer from "./features/common/components/ToastContainer";

function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <ReportProvider>
          <ToastContainer />
          <Routes>
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Home />
                </ProtectedRoute>
              }
            />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/report/:reportId"
              element={
                <ProtectedRoute>
                  <Report />
                </ProtectedRoute>
              }
            />
          </Routes>
        </ReportProvider>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
