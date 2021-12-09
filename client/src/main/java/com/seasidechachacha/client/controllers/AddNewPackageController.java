package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewPackageController {
    private static final Logger logger = LogManager.getLogger(AddNewPackageController.class);

    @FXML
    private TextField tfName, tfLimit, tfPrice;

    @FXML
    private ComboBox cbDayCooldown;

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    @FXML
    public void initialize() {

    }

}
