import React from "react";
import "./Spinner.scss";

const Spinner = ({ size = "small" }) => (
  <div className={`spinner spinner--${size}`}>
    <div className="spinner__inner" />
  </div>
);

export default Spinner;
