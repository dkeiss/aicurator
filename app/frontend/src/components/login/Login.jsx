import React from "react";
import {useForm} from "react-hook-form";
import {withLogin} from "./WithLogin";
import {withRouter} from "../../common/WithRouter";

const Login = ({handleLogin, loading, message}) => {
    const {register, handleSubmit, formState: {errors}} = useForm();

    return (
        <div className="col-md-12">
            <div className="card card-container">
                <img src="/logo192.png" alt="Taxishare Logo" className="profile-img-card"/>
                <form onSubmit={handleSubmit(handleLogin)}>
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            {...register("username", {required: "Username is required"})}
                        />
                        {errors.username && (
                            <div className="alert alert-danger" role="alert">
                                {errors.username.message}
                            </div>
                        )}
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            {...register("password", {required: "Password is required"})}
                        />
                        {errors.password && (
                            <div className="alert alert-danger" role="alert">
                                {errors.password.message}
                            </div>
                        )}
                    </div>
                    <button className="btn btn-primary btn-block" disabled={loading}>
                        {loading && <span className="spinner-border spinner-border-sm"></span>}
                        <span>Login</span>
                    </button>
                    {message && <div className="alert alert-danger" role="alert">{message}</div>}
                </form>
            </div>
        </div>
    );
};

export default withRouter(withLogin(Login));
