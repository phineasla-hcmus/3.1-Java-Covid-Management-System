package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import com.jfoenix.controls.JFXButton;
import com.seasidechachacha.client.App;
import org.apache.logging.log4j.LogManager;

public class ModeratorScreenController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoginController.class);
    @FXML
    private JFXButton btn_all, btn_core, btn_xeom;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btn_all) {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
        } else if (event.getSource() == btn_core) {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
        } else if (event.getSource() == btn_xeom) {
            App.setCurrentPane("pn_all", "view/ModeratorStatistic", null);
        }
    }

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void initialize() {
//        try {
//            App.setCurrentPane("pn_all", "view/ViewListUser", null);
//        } catch (IOException ex) {
//            logger.fatal(ex);
//        }
    }

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            mainBorderPane.setCenter(loader.load());
        } catch (IOException e) {
        }
    }
}
