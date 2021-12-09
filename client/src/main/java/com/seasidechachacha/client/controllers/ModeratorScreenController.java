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
import javafx.scene.control.ScrollPane;

public class ModeratorScreenController {

    @FXML
    private ScrollPane pn_all;
    
    @FXML
    private Pane pn_core, pn_xeom, pn_atom;

    @FXML
    private JFXButton btn_all, btn_core, btn_xeom, btn_atom;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btn_all) {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
            pn_all.toFront();
        } else if (event.getSource() == btn_core) {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
//            pn_core.toFront();
        } 
        else if (event.getSource() == btn_xeom) {
            App.setCurrentPane("pn_all", "view/ModeratorStatistic", null);
        } else if (event.getSource() == btn_atom) {
            pn_atom.toFront();
        }
    }

    @FXML
    private BorderPane mainBorderPane;

//    @FXML
//    private void handleShowView1(ActionEvent e) {
//        loadFXML(getClass().getResource("ViewListUser.fxml"));
//    }
//
//    @FXML
//    private void handleShowView2(ActionEvent e) {
//        loadFXML(getClass().getResource("secondary.fxml"));
//    }

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            mainBorderPane.setCenter(loader.load());
        } catch (IOException e) {
        }
    }
}
