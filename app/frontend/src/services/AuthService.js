import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

class AuthService {

    login(username, password) {
        return axios
            .post(API_URL + "/login", {
                username,
                password
            })
            .then(response => {
                this.setUser(response);
                return response.data;
            });
    }

    logout() {
        localStorage.removeItem("user");
    }

    register(username, email, password) {
        return axios.post(API_URL + "/register", {
            username,
            email,
            password
        });
    }

    setUser(response) {
        if (response.data.accessToken) {
            localStorage.setItem("user", JSON.stringify(response.data));
        }
    }

    getUser() {
        return JSON.parse(localStorage.getItem('user'));
    }

}

export default new AuthService();
