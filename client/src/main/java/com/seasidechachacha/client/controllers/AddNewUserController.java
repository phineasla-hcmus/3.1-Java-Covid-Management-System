package com.seasidechachacha.client.controllers;

//import java.net.URL;
import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.ManagedUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        // for testing purpose
        ManagerDao Tam = new ManagerDao("mod-19127268");
        btnAddNewPerson.setOnAction(event -> {
//            ManagedUser user = new ManagedUser("123456", tfFullName.getText(), Integer.valueOf(tfBirthYear.getText()), "1", 0,
//                    "00001", "abc");
            try {
                if (Tam.addNewUser(getCurrentInput())) {
                    // TODO
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý người liên quan Covid19");
                    alert.setContentText("Thêm mới người dùng thành công!");

                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý người liên quan Covid19");
                    alert.setContentText("Người dùng đã tồn tại!");

                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                logger.fatal(ex);
            }
        });
        cbCurrentStatus.getItems().addAll("F0", "F1", "F2");
        cbCity.getItems().addAll("TPHCM", "Hà Nội");
        cbDistrict.getItems().addAll("Quận 1", "Quận 2", "Quận Hoàn Kiếm", "Quận Đống Đa");
        cbWard.getItems().addAll("Phường Bến Nghé", "Phường Bến Thành", "Phường Chương Dương", "Phường Cửa Đông");

    }

    private ManagedUser getCurrentInput() {
        ManagedUser user = null;
        String ID = tfIdentityCard.getText();
        String name = tfFullName.getText();
        int birthYear = Integer.valueOf(tfBirthYear.getText());
        String status = cbCurrentStatus.getValue();
        String relatedID = "079111222333";
        String wardID = "30208";
        String address = "test";

        user = new ManagedUser(ID, name, birthYear, relatedID, 0, wardID, address);

        return user;
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
