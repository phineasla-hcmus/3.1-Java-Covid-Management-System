package com.seasidechachacha.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class LoginController {
    @FXML
    private TextField username;
    
    @FXML
    private TextField pass;
    
    public void login(ActionEvent e){ // có database thì xét và dựa vào role để đưa vào menu
        String user = username.getText();
        String pas= pass.getText();
        
    }
}
