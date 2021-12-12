package com.seasidechachacha.client.controllers;

//import java.net.URL;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagerDao.addNewUser;
import com.seasidechachacha.client.models.ManagedUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewUserController {
    private static final Logger logger = LogManager.getLogger(AddNewUserController.class);

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
            logger.fatal(ex);
        }
    }

    @FXML
    private void initialize() {
        btnAddNewPerson.setOnAction(event -> {
            ManagedUser user = new ManagedUser("123456", tfFullName.getText(), Integer.valueOf(tfBirthYear.getText()), "1", 0,
                    "00001", "abc");
            try {
                if (addNewUser(user)) {
                    // TODO
                    
                }
            } catch (SQLException ex) {
                logger.fatal(ex);
            }
        });
        cbCurrentStatus.getItems().addAll("F0", "F1", "F2", "F3");
        cbCity.getItems().addAll("TPHCM", "Hà Nội");
        cbDistrict.getItems().addAll("Quận 1", "Quận 2", "Quận Hoàn Kiếm", "Quận Đống Đa");
        cbWard.getItems().addAll("Phường Bến Nghé", "Phường Bến Thành", "Phường Chương Dương", "Phường Cửa Đông");

    }

    // public void setup() {
    // cbCurrentStatus.getItems().addAll("F0", "F1", "F2", "F3");
    // cbCity.getItems().addAll("TPHCM", "Hà Nội");
    // cbDistrict.getItems().addAll("Quận 1", "Quận 2", "Quận Hoàn Kiếm", "Quận Đống
    // Đa");
    // cbWard.getItems().addAll("Phường Bến Nghé", "Phường Bến Thành", "Phường
    // Chương Dương", "Phường Cửa Đông");
    // }
}
