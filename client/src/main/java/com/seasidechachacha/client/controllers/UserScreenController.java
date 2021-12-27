package com.seasidechachacha.client.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.seasidechachacha.client.App;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserScreenController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private JFXButton btnInfo, btnPackage, btnPayment ,btnViewCart;
    
    @FXML
    private Button btnLogout;

    @FXML
    private void initialize() {
        btnLogout.setOnAction(event -> {
            try {
                App.setRoot("view/Login");
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btnInfo) {
            App.setCurrentPane("pn_all", "view/UserInfo", null);
        } else if (event.getSource() == btnPackage) {
            App.setCurrentPane("pn_all", "view/BuyPackage", null);
        } else if (event.getSource() == btnViewCart) {
            App.setCurrentPane("pn_all", "view/viewCartItem", null);
        } else if(event.getSource() == btnPayment){
            App.setCurrentPane("pn_all", "view/Payment", null);
        }
    }
}
