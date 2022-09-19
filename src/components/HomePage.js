import React, { useEffect } from "react";
import {Link, useNavigate} from "react-router-dom";

import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import axios from "axios";

import {
  BsFillBookFill,
  BsFillShareFill,
  BsFillPersonPlusFill,
  BsFillCpuFill
} from "react-icons/bs";

import {RiLoginBoxLine} from "react-icons/ri";

import styles from "./styles/HomePage.module.css";

import psnLogo from "./assets/psn-logo-large.png";



function HomePage() {
  const navigate = useNavigate();

  // async function tokenValidate(){
  //   const response = await axios({
  //     method: "get",
  //     url: "https://tweetappfse.azurewebsites.net/apps/v1.0/tweets/validate",
  //     headers: {
  //       Authorization: "Bearer "+localStorage.getItem("token"),
  //       "Content-Type": "application/json",
  //       "Access-Control-Allow-Origin": "*",
  //     },
  //   });
  //   console.log(response);
  //   if(response.status!==200){
  //     localStorage.removeItem("token");
  //     localStorage.removeItem("userName");
  //     localStorage.removeItem("valid");
  //     localStorage.removeItem("firstName");
  //     localStorage.removeItem("lastName");
  //   }
  // }

  // useEffect(() => {
  //   if (localStorage.getItem("token") !== null) {
  //     tokenValidate()
  //     if(localStorage.getItem("token") !== null && localStorage.getItem("token")!=="" && localStorage.getItem("valid")!==true)
  //       navigate("/home");
  //   }
  // });

  return (
    <Container fluid>
      <Row className={styles.container}>
        <Col className={`${styles.colContainerLeft} ${styles.leftBackground}`}>
          <div>
            <Row>
              <h3 className="my-3">
                <BsFillBookFill /> Welcome to Tweet App
              </h3>
            </Row>
            <Row>
              <h3 className="my-3">
                <BsFillCpuFill /> Hear what people are talking about.
              </h3>
            </Row>
            <Row>
              <h3 className="my-3">
                <BsFillShareFill /> Join and share your Post to your friends
              </h3>
            </Row>
          </div>
        </Col>
        <Col className={styles.colContainerRight}>
          <div className={styles.colWithButtons}>
            <img src={psnLogo} alt="PSN logo" width={120} className="mb-3" />
            <Row>
              <h3 className="text-success mb-3">See what is happening in the world right now</h3>
            </Row>
            <br />
            <Row>
              <h5 className="text-success mb-3">Join Tweet App today</h5>
            </Row>{" "}
            <br />
            <Row>
              <Link to="/signin" className={styles.linkTextFormat}><Button variant="success" className={`${styles.btnHomePage} mb-3`}>Sign In <RiLoginBoxLine /></Button></Link>
            </Row>
            <Row>
            <Link to="/signup" className={styles.linkTextFormat}><Button variant="success" className={styles.btnHomePage}>Sign Up <BsFillPersonPlusFill /></Button></Link>
            </Row>
          </div>
        </Col>
      </Row>
    </Container>
  );
}

export default HomePage;
