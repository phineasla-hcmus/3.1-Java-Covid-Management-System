CREATE DATABASE payment;
use payment;

CREATE TABLE Account(
    userID varchar(12) NOT NULL,
    balance decimal(20,3),
    PRIMARY KEY (userID)
);

CREATE TABLE LogPayment(
    paymentID int NOT NULL AUTO_INCREMENT,
    userID varchar(12),
    paymentTime datetime,
    money decimal(20,3),
    PRIMARY KEY (paymentID)
);

ALTER TABLE LogPayment
ADD FOREIGN KEY (userID) REFERENCES Account(userID);