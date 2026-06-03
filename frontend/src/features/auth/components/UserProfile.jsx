import React, { useState, useRef, useEffect } from "react";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../../common/hooks/useToast";
import Spinner from "../../common/components/Spinner";
import "./UserProfile.scss";

const UserProfile = () => {
  const { user, handleLogout } = useAuth();
  const { showToast } = useToast();
  const [isOpen, setIsOpen] = useState(false);
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  if (!user?.email) return null;

  const firstLetter = user.email.charAt(0).toUpperCase();

  const handleLogoutClick = async () => {
    try {
      setIsLoggingOut(true);
      await handleLogout();
      setIsOpen(false);
    } catch (error) {
      showToast(error?.message || "Failed to logout", "error");
    } finally {
      setIsLoggingOut(false);
    }
  };

  return (
    <div className="user-profile" ref={dropdownRef}>
      <button
        className="user-profile__avatar"
        onClick={() => setIsOpen(!isOpen)}
        title={user.email}
      >
        {firstLetter}
      </button>

      {isOpen && (
        <div className="user-profile__dropdown">
          <div className="user-profile__email">
            <span className="user-profile__email-label">Email</span>
            <span className="user-profile__email-value">{user.email}</span>
          </div>
          <div className="user-profile__divider" />
          <button
            className="user-profile__logout"
            onClick={handleLogoutClick}
            disabled={isLoggingOut}
            style={{
              opacity: isLoggingOut ? 0.6 : 1,
              cursor: isLoggingOut ? "not-allowed" : "pointer",
            }}
          >
            {isLoggingOut ? (
              <Spinner size="small" />
            ) : (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                <polyline points="16 17 21 12 16 7" />
                <line x1="21" y1="12" x2="9" y2="12" />
              </svg>
            )}
            Logout
          </button>
        </div>
      )}
    </div>
  );
};

export default UserProfile;
