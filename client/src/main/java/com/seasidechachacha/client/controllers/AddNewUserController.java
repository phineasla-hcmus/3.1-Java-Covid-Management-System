package com.seasidechachacha.client.controllers;

//import java.net.URL;

import com.seasidechachacha.client.App;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

//import java.util.ResourceBundle;
//import javafx.fxml.Initializable;
public class AddNewUserController {

    @FXML
    private TextField tfFullName, tfBirthYear, tfIdentityCard;
    
    @FXML
    private ComboBox cbCurrentStatus, cbCity, cbDistrict, cbWard;
    
    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
        } catch (IOException ex) {
            Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void initialize() {
        cbCurrentStatus.getItems().addAll("F0", "F1", "F2", "F3");
        cbCity.getItems().addAll("TPHCM", "Hà Nội");
        cbDistrict.getItems().addAll("Quận 1", "Quận 2", "Quận Hoàn Kiếm", "Quận Đống Đa");
        cbWard.getItems().addAll("Phường Bến Nghé", "Phường Bến Thành", "Phường Chương Dương", "Phường Cửa Đông");
        
    }
    
//    public void setup() {
//        cbCurrentStatus.getItems().addAll("F0", "F1", "F2", "F3");
//        cbCity.getItems().addAll("TPHCM", "Hà Nội");
//        cbDistrict.getItems().addAll("Quận 1", "Quận 2", "Quận Hoàn Kiếm", "Quận Đống Đa");
//        cbWard.getItems().addAll("Phường Bến Nghé", "Phường Bến Thành", "Phường Chương Dương", "Phường Cửa Đông");
//    }
}
