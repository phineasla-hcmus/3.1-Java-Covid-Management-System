package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.fxml.FXML;


public class FirstLoginController {
    
    @FXML
    private void SwitchtoCreateAdmin() throws IOException{ // khi người dùng ấn button "có"
        App.setRoot("view/CreateAdmin");
    }
    
    @FXML
    private void ExitApp(){ // khi người dùng ấn button "không"
        App.close();
    }
}
