import React from "react";
import { useState, useEffect } from "react";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../../common/hooks/useToast";
import "./OtpModal.scss";
import Spinner from "../../common/components/Spinner";
import { useNavigate } from "react-router";

export const OtpModal = ({ email, password }) => {
  const [otp, setOtp] = useState("");
  const [isVerifyingOtp, setIsVerifyingOtp] = useState(false);
  const { handleVerifyOtpAndRegisterUser, handleSendOtp } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [resendTimer, setResendTimer] = useState(30);
  const [isResendingOtp, setIsResendingOtp] = useState(false);

  const handleOtpSubmit = async (e) => {
    e.preventDefault();

    try {
      setIsVerifyingOtp(true);

      await handleVerifyOtpAndRegisterUser({
        email,
        password,
        enteredOtp: otp,
      });

      showToast("Email verified successfully!", "success");
      navigate("/login");
    } catch (error) {
      console.log(error);
      showToast(error?.message || "Invalid OTP or OTP expired", "error");
    } finally {
      setIsVerifyingOtp(false);
    }
  };

  useEffect(() => {
    if (resendTimer <= 0) return;

    const timer = setInterval(() => {
      setResendTimer((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [resendTimer]);

  const handleResendOtp = async () => {
    try {
      setIsResendingOtp(true);

      await handleSendOtp({ email });
      showToast("OTP resent successfully!", "success");
      setResendTimer(30);
    } catch (error) {
      showToast(error?.message || "Failed to resend OTP", "error");
    } finally {
      setIsResendingOtp(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className=" otp-modal">
        <h1>Verify OTP</h1>

        <p style={{ textAlign: "center", marginBottom: "1rem" }}>
          OTP has been sent to <strong>{email}</strong>
        </p>

        <form onSubmit={handleOtpSubmit}>
          <div className="input-group">
            <label htmlFor="otp">Enter OTP</label>
            <input
              type="text"
              id="otp"
              name="otp"
              placeholder="Enter OTP"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />
          </div>

          <button
            className="button primary-button"
            disabled={isVerifyingOtp}
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "0.5rem",
            }}
          >
            {isVerifyingOtp ? <Spinner size="small" /> : null}
            Verify OTP
          </button>
        </form>

        <div className="resend-otp-container">
          {resendTimer > 0 ? (
            <div className="resend-otp-timer">
              Resend OTP in <strong>{resendTimer}s</strong>
            </div>
          ) : (
            <>
              <div
                className="resend-otp-button"
                onClick={handleResendOtp}
                disabled={isResendingOtp}
              >
                {isResendingOtp ? "Resending..." : "Resend OTP"}
              </div>

              <div className="resend-otp-hint">Didn't receive the code?</div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};
