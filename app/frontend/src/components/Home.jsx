import React, {useState} from "react";
import logo from "../logo.svg";

const Home = () => {
    const [content] = useState("Welcome to Taxishare!");

    return (
        <div className="container">
            <header className="jumbotron">
                <img src={logo} alt="Taxishare Logo" style={{width: "200px", height: "auto"}}/>
                <h3>{content}</h3>
                <p>Taxishare allows users to share taxi rides, saving money and reducing carbon emissions. Join us and
                    start sharing your rides today!</p>
            </header>
        </div>
    );
};

export default Home;