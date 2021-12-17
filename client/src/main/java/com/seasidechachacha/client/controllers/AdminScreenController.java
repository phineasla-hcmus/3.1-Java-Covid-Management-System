package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import com.jfoenix.controls.JFXButton;
import com.seasidechachacha.client.App;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import org.apache.logging.log4j.LogManager;

public class AdminScreenController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private JFXButton btn_all, btn_core, btn_xeom, btn_atom;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btn_all) {
            App.setCurrentPane("pn_all", "view/AddNewModerator", null);
        } else if (event.getSource() == btn_core) {
            App.setCurrentPane("pn_all", "view/ManageModerator", null);
        } else if (event.getSource() == btn_xeom) {
            App.setCurrentPane("pn_all", "view/ViewListTreatmentPlace", null);
        }
    }
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
    private BorderPane mainBorderPane;

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            mainBorderPane.setCenter(loader.load());
        } catch (IOException e) {
        }
    }
}
