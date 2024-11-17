import React, {useState} from "react";
import AuthService from "../../services/AuthService";

export const withLogin = (WrappedComponent) => {
    return (props) => {
        const [loading, setLoading] = useState(false);
        const [message, setMessage] = useState("");

        const handleLogin = (credentials) => {
            setLoading(true);
            setMessage("");

            AuthService.login(credentials.username || credentials.email, credentials.password)
                .then(() => {
                    props.router.navigate("/reservation");
                    window.location.reload();
                })
                .catch((error) => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    setLoading(false);
                    setMessage(resMessage);
                });
        };

        return (
            <WrappedComponent
                {...props}
                loading={loading}
                message={message}
                handleLogin={handleLogin}
            />
        );
    };
};
