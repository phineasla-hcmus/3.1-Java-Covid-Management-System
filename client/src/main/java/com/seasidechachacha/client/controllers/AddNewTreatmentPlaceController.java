package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddNewTreatmentPlaceController implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddNewTreatmentPlaceController.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListTreatmentPlace", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

}
