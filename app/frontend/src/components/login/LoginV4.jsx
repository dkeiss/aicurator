import React from "react";
import {useForm} from "react-hook-form";
import {withLogin} from "./WithLogin";
import {withRouter} from "../../common/WithRouter";

const LoginV4 = ({handleLogin, loading, message}) => {
    const {register, handleSubmit, formState: {errors}} = useForm();

    return (
        <div className="fancy-card">
            <div className="profile-img-container">
                <img src="/logo192.png" alt="Taxishare Logo" className="profile-img-card"/>
            </div>
            <form onSubmit={handleSubmit(handleLogin)} className="fancy-form">
                <div className="form-content">
                    <input
                        type="text"
                        className="fancy-input"
                        placeholder="Username"
                        {...register("username", {required: "Username is required"})}
                    />
                    {errors.username && (
                        <div className="alert alert-danger">{errors.username.message}</div>
                    )}
                    <input
                        type="password"
                        className="fancy-input"
                        placeholder="Password"
                        {...register("password", {required: "Password is required"})}
                    />
                    {errors.password && (
                        <div className="alert alert-danger">{errors.password.message}</div>
                    )}
                    <button id={"login-button"} className="fancy-button" disabled={loading}>
                        {loading && <span className="spinner-border spinner-border-sm"></span>}
                        <span>Login</span>
                    </button>
                </div>
                {message && <div className="alert alert-danger">{message}</div>}
            </form>

            <button
                className="fancy-button-google"
                disabled={loading}
                onClick={() => alert("Not Implemented")}
            >
                {loading && (
                    <span className="spinner-border spinner-border-sm"></span>
                )}
                <span>
                    <span style={{color: "#4285F4"}}>G</span>
                    <span style={{color: "#EA4335"}}>o</span>
                    <span style={{color: "#FBBC05"}}>o</span>
                    <span style={{color: "#4285F4"}}>g</span>
                    <span style={{color: "#34A853"}}>l</span>
                    <span style={{color: "#EA4335"}}>e</span>
                    -Login
                </span>
            </button>
        </div>
    );
};

export default withRouter(withLogin(LoginV4));
