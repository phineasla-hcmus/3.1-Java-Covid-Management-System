package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("view/primary");
    }
}