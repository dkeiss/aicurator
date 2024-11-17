import React from "react";
import {useForm} from "react-hook-form";
import {withRegistration} from "./WithRegistration";
import {withRouter} from "../../common/WithRouter";

const Registration = ({
                          handleRegister,
                          successful,
                          message,
                          usernameValidation,
                          emailValidation,
                          passwordValidation,
                      }) => {
    const {register, handleSubmit, formState: {errors}} = useForm();

    return (
        <div className="col-md-12">
            <div className="card card-container">
                <img src="/logo192.png" alt="Taxishare Logo" className="profile-img-card"/>
                <form onSubmit={handleSubmit(handleRegister)}>
                    {!successful && (
                        <div>
                            <div className="form-group">
                                <label htmlFor="username">Username</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    {...register("username", usernameValidation)}
                                />
                                {errors.username && (
                                    <div className="alert alert-danger" role="alert">
                                        {errors.username.message}
                                    </div>
                                )}
                            </div>
                            <div className="form-group">
                                <label htmlFor="email">Email</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    {...register("email", emailValidation)}
                                />
                                {errors.email && (
                                    <div className="alert alert-danger" role="alert">
                                        {errors.email.message}
                                    </div>
                                )}
                            </div>
                            <div className="form-group">
                                <label htmlFor="password">Password</label>
                                <input
                                    type="password"
                                    className="form-control"
                                    {...register("password", passwordValidation)}
                                />
                                {errors.password && (
                                    <div className="alert alert-danger" role="alert">
                                        {errors.password.message}
                                    </div>
                                )}
                            </div>
                            <button className="btn btn-primary btn-block">Sign Up</button>
                        </div>
                    )}
                    {message && (
                        <div
                            className={successful ? "alert alert-success" : "alert alert-danger"}
                            role="alert"
                        >
                            {message}
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
};

export default withRouter(withRegistration(Registration));
