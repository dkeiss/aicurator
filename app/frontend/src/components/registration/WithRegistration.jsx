import React, {useState} from "react";
import AuthService from "../../services/AuthService";

export const withRegistration = (WrappedComponent) => {
    return (props) => {
        const [successful, setSuccessful] = useState(false);
        const [message, setMessage] = useState("");

        const handleRegister = (data) => {
            setMessage("");
            setSuccessful(false);

            AuthService.register(data.username, data.email, data.password)
                .then((response) => {
                    setMessage(response.data.message);
                    setSuccessful(true);
                })
                .catch((error) => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    setMessage(resMessage);
                    setSuccessful(false);
                });
        };

        const usernameValidation = {
            required: "Username is required",
            minLength: {value: 3, message: "The username must be at least 3 characters"},
            maxLength: {value: 20, message: "The username must be less than 20 characters"},
        };

        const emailValidation = {
            required: "Email is required",
            pattern: {
                value: /^\S+@\S+$/i,
                message: "This is not a valid email",
            },
        };

        const passwordValidation = {
            required: "Password is required",
            minLength: {value: 6, message: "The password must be at least 6 characters"},
            maxLength: {value: 40, message: "The password must be less than 40 characters"},
        };

        return (
            <WrappedComponent
                {...props}
                successful={successful}
                message={message}
                handleRegister={handleRegister}
                usernameValidation={usernameValidation}
                emailValidation={emailValidation}
                passwordValidation={passwordValidation}
            />
        );
    };
};
