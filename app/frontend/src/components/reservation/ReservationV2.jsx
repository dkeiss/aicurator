import React from "react";
import { useForm } from "react-hook-form";
import { withReservation } from "./WithReservation";
import { Navigate } from "react-router-dom";
import { withRouter } from "../../common/WithRouter";

const ReservationV2 = ({
                           redirect,
                           reservations,
                           currentUser,
                           isReserveButtonDisabled,
                           message,
                           updateMessage,
                           successful,
                           handleSearch,
                           handleReserve,
                           handleJoin,
                       }) => {
    const { register, handleSubmit, formState: { errors } } = useForm();

    if (redirect) {
        return <Navigate to={redirect} />;
    }

    return (
        <div className="container fancy-container">
            <div className="row">
                <div className="col-lg-12">
                    <h1 className="fancy-title">Reservation</h1>
                </div>
            </div>
            <form onSubmit={handleSubmit(handleSearch)}>
                <div className="row">
                    <div className="col-lg-4">
                        <div className="form-group fancy-form-group">
                            <input
                                type="date"
                                className="form-control fancy-input"
                                placeholder="Date"
                                {...register("date", { required: "Date is required" })}
                            />
                            {errors.date && <div className="alert alert-danger fancy-alert">{errors.date.message}</div>}
                        </div>
                    </div>
                    <div className="col-lg-4">
                        <div className="form-group fancy-form-group">
                            <input
                                type="time"
                                className="form-control fancy-input"
                                placeholder="Earliest start time"
                                {...register("earliestStartTime", {
                                    required: "Earliest start time is required",
                                    pattern: {
                                        value: /^([0-1]\d|2[0-3]):([0-5]\d)$/,
                                        message: "Invalid time format. Use HH:mm.",
                                    },
                                })}
                            />
                            {errors.earliestStartTime && (
                                <div className="alert alert-danger fancy-alert">{errors.earliestStartTime.message}</div>
                            )}
                        </div>
                    </div>
                    <div className="col-lg-4">
                        <div className="form-group fancy-form-group">
                            <input
                                type="time"
                                className="form-control fancy-input"
                                placeholder="Latest start time"
                                {...register("latestStartTime", {
                                    required: "Latest start time is required",
                                    pattern: {
                                        value: /^([0-1]\d|2[0-3]):([0-5]\d)$/,
                                        message: "Invalid time format. Use HH:mm.",
                                    },
                                })}
                            />
                            {errors.latestStartTime && (
                                <div className="alert alert-danger fancy-alert">{errors.latestStartTime.message}</div>
                            )}
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col-lg-6">
                        <div className="form-group fancy-form-group">
                            <input
                                type="text"
                                className="form-control fancy-input"
                                placeholder="Point of departure"
                                {...register("departure", { required: "Departure is required" })}
                            />
                            {errors.departure && (
                                <div className="alert alert-danger fancy-alert">{errors.departure.message}</div>
                            )}
                        </div>
                    </div>
                    <div className="col-lg-6">
                        <div className="form-group fancy-form-group">
                            <input
                                type="text"
                                className="form-control fancy-input"
                                placeholder="Destination"
                                {...register("destination", { required: "Destination is required" })}
                            />
                            {errors.destination && (
                                <div className="alert alert-danger fancy-alert">{errors.destination.message}</div>
                            )}
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col-lg-1">
                        <button type="submit" className="btn btn-primary btn-lg fancy-button">
                            Search
                        </button>
                    </div>
                    <div className="col-lg-1">
                        <button
                            type="button"
                            className={`btn btn-success btn-lg fancy-button ${isReserveButtonDisabled ? "hidden" : ""}`}
                            onClick={handleSubmit(handleReserve)}
                            disabled={isReserveButtonDisabled}
                        >
                            Reserve
                        </button>
                    </div>
                </div>
            </form>

            {message && (
                <div className="form-group">
                    <div className={`alert ${successful ? "alert-success" : "alert-danger"} fancy-alert`} role="alert">
                        {message}
                    </div>
                </div>
            )}

            {updateMessage && (
                <div className="form-group">
                    <div className="alert alert-info fancy-alert" role="alert">
                        {updateMessage}
                    </div>
                </div>
            )}

            {reservations.length > 0 && (
                <div className="table-responsive">
                    <table className="table table-hover fancy-table">
                        <thead>
                        <tr>
                            <th></th>
                            <th>Date</th>
                            <th>Departure</th>
                            <th>Destination</th>
                            <th>Time</th>
                            <th>Participants</th>
                            <th>Price</th>
                        </tr>
                        </thead>
                        <tbody>
                        {reservations.map((reservation, index) => (
                            reservation && (
                                <tr key={index} data-reservation-id={reservation.id}>
                                    <td>
                                        {!reservation.participants?.includes(currentUser?.username) && (
                                            <button
                                                id={`joinButton${reservation.id}`}
                                                className="btn btn-primary fancy-button"
                                                onClick={() => handleJoin(reservation.id)}
                                            >
                                                Join
                                            </button>
                                        )}
                                    </td>
                                    <td>{reservation.date}</td>
                                    <td>{reservation.departure}</td>
                                    <td>{reservation.destination}</td>
                                    <td>{reservation.startTime}</td>
                                    <td>{reservation.participants?.join(", ") || "N/A"}</td>
                                    <td>{reservation.price || "N/A"}</td>
                                </tr>
                            )
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default withRouter(withReservation(ReservationV2));