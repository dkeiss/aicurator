import React from "react";
import {useForm} from "react-hook-form";
import {withRegistration} from "./WithRegistration";
import {withRouter} from "../../common/WithRouter";

const Registration2 = ({
                           handleRegister,
                           successful,
                           message,
                           usernameValidation,
                           emailValidation,
                           passwordValidation,
                       }) => {
    const {register, handleSubmit, formState: {errors}} = useForm();

    return (
        <div className="fancy-card">
            <div className="profile-img-container">
                <img src="/logo192.png" alt="Taxishare Logo" className="profile-img-card"/>
            </div>
            <form onSubmit={handleSubmit(handleRegister)} className="fancy-form">
                {!successful && (
                    <div className="form-content">
                        <input
                            type="text"
                            className="fancy-input"
                            placeholder="Username"
                            {...register("username", usernameValidation)}
                        />
                        {errors.username && (
                            <div className="alert alert-danger" role="alert">
                                {errors.username.message}
                            </div>
                        )}
                        <input
                            type="text"
                            className="fancy-input"
                            placeholder="Email"
                            {...register("email", emailValidation)}
                        />
                        {errors.email && (
                            <div className="alert alert-danger" role="alert">
                                {errors.email.message}
                            </div>
                        )}
                        <input
                            type="password"
                            className="fancy-input"
                            placeholder="Password"
                            {...register("password", passwordValidation)}
                        />
                        {errors.password && (
                            <div className="alert alert-danger" role="alert">
                                {errors.password.message}
                            </div>
                        )}
                        <button className="fancy-button">Sign Up</button>
                    </div>
                )}
                {message && (
                    <div
                        className={successful ? "alert alert-info" : "alert alert-danger"}
                        role="alert"
                    >
                        {message}
                    </div>
                )}
            </form>
        </div>
    );
};

export default withRouter(withRegistration(Registration2));
