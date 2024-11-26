import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "http://localhost:8080/api/reservations";
const CONFIG = {
    headers: authHeader()
}

class ReservationService {

    reserve(date, departure, destination, startTime) {
        const reservation = {
            date,
            departure,
            destination,
            startTime
        };

        return axios.post(`${API_URL}`, reservation, CONFIG)
            .then(response => {
                return response.data;
            })
            .catch(error => {
                console.error("There was an error creating the reservation!", error);
            });
    }

    searchReservations(date, earliestStartTime, latestStartTime, departure, destination) {
        const queryParams = new URLSearchParams({
            date: btoa(date),
            earliestStartTime: btoa(earliestStartTime),
            latestStartTime: btoa(latestStartTime),
            departure: btoa(departure),
            destination: btoa(destination)
        });

        return axios.get(`${API_URL}?${queryParams.toString()}`, CONFIG)
            .then(response => {
                return response.data;
            })
            .catch(error => {
                console.error("Error searching for reservations", error);
            });
    }

    joinReservation(reservationId) {
        return axios.put(`${API_URL}/${reservationId}`, {}, CONFIG)
            .then(response => {
                return response.data;
            })
            .catch(error => {
                console.error("Error joining the reservation", error);
            });
    }

    registerForUpdates(reservationId, callback) {
        axios.get(`${API_URL}/${reservationId}/updates`, {
            headers: {
                'Accept': 'text/event-stream',
                'Authorization': authHeader().Authorization
            },
            responseType: 'stream',
            adapter: 'fetch',
        }).then(async (response) => {
            console.log('got price updates');
            const stream = response.data;
            const reader = stream.pipeThrough(new TextDecoderStream()).getReader();
            while (true) {
                const {value, done} = await reader.read();
                if (done) {
                    break;
                }
                callback(value);
            }
        }).catch(error => {
            console.error("Error registering for updates", error);
        });
    }

}

export default new ReservationService();
