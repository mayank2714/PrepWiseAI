import React, { useState } from "react";
import { useNavigate, Link } from "react-router";
import "./Login.scss";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../../common/hooks/useToast";
import Spinner from "../../common/components/Spinner";

const Login = () => {
  const { loading, handleLogin } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      await handleLogin({ email, password });
      navigate("/");
    } catch (error) {
      showToast(error?.message || "Login failed", "error");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main>
      <div className="form-container">
        <h1>Login</h1>
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
            Login
          </button>
        </form>
        <p>
          Don't have an account? <Link to={"/register"}>Register</Link>{" "}
        </p>
      </div>
    </main>
  );
};

export default Login;
