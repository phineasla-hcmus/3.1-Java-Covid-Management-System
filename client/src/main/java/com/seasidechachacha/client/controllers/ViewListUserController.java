package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class ViewListUserController {
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("view/secondary");
    }
}
