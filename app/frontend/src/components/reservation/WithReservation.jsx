import React, {useEffect, useState} from "react";
import AuthService from "../../services/AuthService";
import ReservationService from "../../services/ReservationService";

export const withReservation = (WrappedComponent) => {
    return (props) => {
        const [redirect, setRedirect] = useState(null);
        const [currentUser, setCurrentUser] = useState(null);
        const [reservations, setReservations] = useState([]);
        const [isReserveButtonDisabled, setReserveButtonDisabled] = useState(true);
        const [message, setMessage] = useState("");
        const [updateMessage, setUpdateMessage] = useState("");
        const [successful, setSuccessful] = useState(false);

        useEffect(() => {
            const user = AuthService.getUser();
            if (!user) {
                setRedirect("/home");
            } else {
                setCurrentUser(user);
            }
        }, []);

        const handleSearch = (data) => {
            setMessage("");
            setSuccessful(false);
            setReserveButtonDisabled(true);

            ReservationService.searchReservations(
                data.date,
                data.earliestStartTime,
                data.latestStartTime,
                data.departure,
                data.destination
            )
                .then((response) => {
                    if (response.length === 0) {
                        setReservations([]);
                        setMessage("No reservations found. Create a new one for the given route.");
                        setReserveButtonDisabled(false);
                        setSuccessful(true);
                    } else {
                        setReservations(response);
                        setMessage("Reservations retrieved successfully");
                        setSuccessful(true);
                    }
                })
                .catch((error) => {
                    const resMessage =
                        error.response?.data?.message || error.message || error.toString();
                    setMessage(resMessage);
                    setSuccessful(false);
                });
        };

        const handleReserve = (data) => {
            setMessage("");
            setSuccessful(false);

            ReservationService.reserve(
                data.date,
                data.departure,
                data.destination,
                data.earliestStartTime
            )
                .then((response) => {
                    setReservations([response]);
                    setMessage("Reservation created successfully");
                    setSuccessful(true);
                    registerForUpdates(response.id);
                })
                .catch((error) => {
                    const resMessage =
                        error.response?.data?.message || error.message || error.toString();
                    setMessage(resMessage);
                    setSuccessful(false);
                });
        };

        const handleJoin = (reservationId) => {
            ReservationService.joinReservation(reservationId)
                .then(() => {
                    setMessage("Joined reservation successfully");
                    setSuccessful(true);
                    registerForUpdates(reservationId);
                })
                .catch((error) => {
                    const resMessage =
                        error.response?.data?.message || error.message || error.toString();
                    setMessage(resMessage);
                    setSuccessful(false);
                });
        };

        const registerForUpdates = (reservationId) => {
            ReservationService.registerForUpdates(reservationId, (event) => {
                const jsonString = event.split('data:')[1];
                const data = JSON.parse(jsonString);
                setUpdateMessage(`There is an update for your reservation! Price is now ${data.price}`);
            });
        };

        return (
            <WrappedComponent
                {...props}
                redirect={redirect}
                currentUser={currentUser}
                reservations={reservations}
                isReserveButtonDisabled={isReserveButtonDisabled}
                message={message}
                updateMessage={updateMessage}
                successful={successful}
                handleSearch={handleSearch}
                handleReserve={handleReserve}
                handleJoin={handleJoin}
            />
        );
    };
};
