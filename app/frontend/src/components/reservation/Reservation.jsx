import React from "react";
import {useForm} from "react-hook-form";
import {withReservation} from "./WithReservation";
import {withRouter} from "../../common/WithRouter";

const Reservation = ({
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
    const {register, handleSubmit, formState: {errors}} = useForm();

    if (redirect) {
        return <Navigate to={redirect}/>;
    }

    return (
        <div className="container">
            <div className="row">
                <div className="col-lg-12">
                    <h1>Reservation</h1>
                    <br/>
                </div>
            </div>
            <form onSubmit={handleSubmit(handleSearch)}>
                <div className="row">
                    <div className="col-lg-4">
                        <div className="form-group">
                            <label htmlFor="date">Date</label>
                            <input
                                type="date"
                                className="form-control"
                                {...register("date", {required: "Date is required"})}
                            />
                            {errors.date && <div className="alert alert-danger">{errors.date.message}</div>}
                        </div>
                    </div>
                    <div className="col-lg-4">
                        <div className="form-group">
                            <label htmlFor="earliestStartTime">Earliest start time</label>
                            <input
                                type="time"
                                className="form-control"
                                {...register("earliestStartTime", {
                                    required: "Earliest start time is required",
                                    pattern: {
                                        value: /^([0-1]\d|2[0-3]):([0-5]\d)$/,
                                        message: "Invalid time format. Use HH:mm.",
                                    },
                                })}
                            />
                            {errors.earliestStartTime && (
                                <div className="alert alert-danger">
                                    {errors.earliestStartTime.message}
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="col-lg-4">
                        <div className="form-group">
                            <label htmlFor="latestStartTime">Latest start time</label>
                            <input
                                type="time"
                                className="form-control"
                                {...register("latestStartTime", {
                                    required: "Latest start time is required",
                                    pattern: {
                                        value: /^([0-1]\d|2[0-3]):([0-5]\d)$/,
                                        message: "Invalid time format. Use HH:mm.",
                                    },
                                })}
                            />
                            {errors.latestStartTime && (
                                <div className="alert alert-danger">
                                    {errors.latestStartTime.message}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col-lg-6">
                        <div className="form-group">
                            <label htmlFor="departure">Point of departure</label>
                            <input
                                type="text"
                                className="form-control"
                                {...register("departure", {required: "Departure is required"})}
                            />
                            {errors.departure && (
                                <div className="alert alert-danger">{errors.departure.message}</div>
                            )}
                        </div>
                    </div>
                    <div className="col-lg-6">
                        <div className="form-group">
                            <label htmlFor="destination">Destination</label>
                            <input
                                type="text"
                                className="form-control"
                                {...register("destination", {required: "Destination is required"})}
                            />
                            {errors.destination && (
                                <div className="alert alert-danger">{errors.destination.message}</div>
                            )}
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col-lg-1">
                        <button id="searchButton" type="submit" className="btn btn-primary btn-lg">
                            Search
                        </button>
                    </div>
                    <div className="col-lg-1">
                        <button id="reserveButton"
                                type="button"
                                className={`btn btn-success btn-lg ${
                                    isReserveButtonDisabled ? "hidden" : ""
                                }`}
                                disabled={isReserveButtonDisabled}
                                onClick={handleSubmit(handleReserve)}
                        >
                            Reserve
                        </button>
                    </div>
                </div>
            </form>

            {
                message && (
                    <div className="form-group">
                        <div
                            className={`alert ${
                                successful ? "alert-success" : "alert-danger"
                            }`}
                        >
                            {message}
                        </div>
                    </div>
                )
            }

            {
                updateMessage && (
                    <div className="form-group">
                        <div className="alert alert-info">{updateMessage}</div>
                    </div>
                )
            }

            {
                reservations.length > 0 && (
                    <div className="table-responsive">
                        <table className="table table-hover">
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
                                <tr key={index} data-reservation-id={reservation.id}>
                                    <td>
                                        {reservation.participants &&
                                            !reservation.participants.includes(currentUser?.username) && (
                                                <button
                                                    id={`joinButton${reservation.id}`}
                                                    className="btn btn-primary"
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
                                    <td>
                                        {reservation.participants
                                            ? reservation.participants.join(", ")
                                            : "N/A"}
                                    </td>
                                    <td>{reservation.price || "N/A"}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )
            }
        </div>
    )
        ;
};

export default withRouter(withReservation(Reservation));
