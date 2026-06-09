import React, { useState } from "react";
import { useNavigate, Link } from "react-router";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../../common/hooks/useToast";
import Spinner from "../../common/components/Spinner";
import { OtpModal } from "../components/OtpModal";
// import "../styles/Register.scss";
const Register = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { loading, handleSendOtp } = useAuth();
  const { showToast } = useToast();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showOtpModal, setShowOtpModal] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      await handleSendOtp( {email} );
      showToast("OTP sent successfully! Please check your email.", "success");
      setShowOtpModal(true);
    } catch (error) {
      showToast(error?.message || "Failed to send OTP", "error");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main>
      
      {showOtpModal ? <OtpModal email={email} password={password} /> : <div className="form-container">
        <h1>Register</h1>

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input
              onChange={(e) => {
                setEmail(e.target.value);
              }}
              type="email"
              id="email"
              name="email"
              placeholder="Enter email address"
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input
              onChange={(e) => {
                setPassword(e.target.value);
              }}
              type="password"
              id="password"
              name="password"
              placeholder="Enter password"
            />
          </div>

          <button
            className="button primary-button"
            disabled={isSubmitting || loading}
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "0.5rem",
            }}
          >
            {isSubmitting || loading ? <Spinner size="small" /> : null}
            Register
          </button>
        </form>

        <p>
          Already have an account? <Link to={"/login"}>Login</Link>{" "}
        </p>
      </div>}
    </main>
  );
};

export default Register;
