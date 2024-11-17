import React, {Component} from "react";
import {Link, Route, Routes} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import AuthService from "./services/AuthService";

import Login from "./components/login/Login";
import LoginV2 from "./components/login/LoginV2";
import LoginV3 from "./components/login/LoginV3";
import LoginV4 from "./components/login/LoginV4";
import Register from "./components/registration/Registration";
import RegisterV2 from "./components/registration/RegistrationV2";
import Home from "./components/Home";
import EventBus from "./common/EventBus";
import Modal from "react-modal";
import Reservation from "./components/reservation/Reservation";
import ReservationV2 from "./components/reservation/ReservationV2";

const MyContext = React.createContext();

class App extends Component {
    constructor(props) {
        super(props);
        this.logout = this.logout.bind(this);

        this.state = {
            currentUser: undefined,
            isModalOpen: false,
        };
    }

    componentDidMount() {
        const user = AuthService.getUser();

        if (user) {
            this.setState({
                currentUser: user,
            });
        }

        EventBus.on("logout", () => {
            this.logout();
        });
    }

    componentWillUnmount() {
        EventBus.remove("logout");
    }

    logout() {
        AuthService.logout();
        this.setState({
            currentUser: undefined,
        });
    }

    toggleModal = () => {
        this.setState((prevState) => ({isModalOpen: !prevState.isModalOpen}));
    };

    render() {
        const {currentUser, isModalOpen} = this.state;

        return (
            <MyContext.Provider value={{currentUser, logout: this.logout}}>
                <div>
                    <nav className="navbar navbar-expand navbar-dark bg-dark">
                        <Link to={"/"} className="navbar-brand">
                            Taxishare
                        </Link>
                        <div className="navbar-nav mr-auto">
                            <li className="nav-item">
                                <Link to={"/"} className="nav-link">
                                    Home
                                </Link>
                            </li>
                        </div>

                        <MyContext.Consumer>
                            {({currentUser, logout}) =>
                                currentUser ? (
                                    <div className="navbar-nav ml-auto">
                                        <li className="nav-item">
                                            <Link to={"/reservation"} className="nav-link">
                                                Reservation
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/reservationV2"} className="nav-link">
                                                ReservationV2
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <a href="/login" className="nav-link" onClick={logout}>
                                                Logout
                                            </a>
                                        </li>
                                    </div>
                                ) : (
                                    <div className="navbar-nav ml-auto">
                                        <li className="nav-item">
                                            <Link to={"/login"} className="nav-link">
                                                Login
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/loginV2"} className="nav-link">
                                                Login v2
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/loginV3"} className="nav-link">
                                                Login v3
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/loginV4"} className="nav-link">
                                                Login v4
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/register"} className="nav-link">
                                                Registration
                                            </Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link to={"/registerV2"} className="nav-link">
                                                Registration v2
                                            </Link>
                                        </li>
                                    </div>
                                )
                            }
                        </MyContext.Consumer>

                        <Modal
                            isOpen={isModalOpen}
                            onRequestClose={this.toggleModal}
                            contentLabel="Register Modal"
                            className="custom-modal"
                            overlayClassName="custom-overlay">
                            <button onClick={this.toggleModal}>Close</button>
                            <RegisterV2/>
                        </Modal>
                    </nav>

                    <div className="container mt-3">
                        <Routes>
                            <Route path="/" element={<Home/>}/>
                            <Route path="/home" element={<Home/>}/>
                            <Route path="/login" element={<Login/>}/>
                            <Route path="/loginV2" element={<LoginV2/>}/>
                            <Route path="/loginV3" element={<LoginV3/>}/>
                            <Route path="/loginV4" element={<LoginV4/>}/>
                            <Route path="/register" element={<Register/>}/>
                            <Route path="/registerV2" element={<RegisterV2/>}/>
                            <Route path="/reservation" element={<Reservation/>}/>
                            <Route path="/reservationV2" element={<ReservationV2/>}/>
                        </Routes>
                    </div>
                </div>
            </MyContext.Provider>
        );
    }
}

export default App;