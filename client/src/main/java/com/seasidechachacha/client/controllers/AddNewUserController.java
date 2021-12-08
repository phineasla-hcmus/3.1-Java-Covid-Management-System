package com.seasidechachacha.client.controllers;

//import java.net.URL;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagerDao.addNewUser;
import com.seasidechachacha.client.models.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewUserController {

    @FXML
    private TextField tfFullName, tfBirthYear, tfIdentityCard;
    
    @FXML
    private ComboBox<String> cbCurrentStatus, cbCity, cbDistrict, cbWard;
    
    @FXML
    private Button btnAddNewPerson;
    
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
        btnAddNewPerson.setOnAction(event -> {
            User user = new User("123456", tfFullName.getText(), Integer.valueOf(tfBirthYear.getText()) , "1", 0, "00001", "abc");
            try {
                if (addNewUser(user)) {
                    // TODO
                }
            } catch (SQLException ex) {
                Logger.getLogger(AddNewUserController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
