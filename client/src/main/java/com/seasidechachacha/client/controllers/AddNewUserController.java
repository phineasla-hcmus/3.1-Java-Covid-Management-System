package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.Ward;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private ComboBox<String> cbCity, cbDistrict, cbWard, cbRelated;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

    @FXML
    private Button btnAddNewPerson;

    private int currentState;

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
            try {
                if (isValid()) {
                    if (manager.addNewUser(getCurrentInput(), currentState)) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText("Quản lý người liên quan Covid19");
                        alert.setContentText("Thêm mới người dùng thành công!");

                        alert.showAndWait();
                        refreshInput();
                    } else {
                        showAlert("Quản lý người liên quan Covid19", "Người dùng đã tồn tại!");
                    }
                }
            } catch (SQLException ex) {
                logger.fatal(ex);
            }
        });
        List<City> city = getCityList();
        for (int i = 0; i < city.size(); i++) {
            cbCity.getItems().add(city.get(i).getCityName());
        }
        cbCity.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String cityId = "";
            for (int i = 0; i < city.size(); i++) {
                if (city.get(i).getCityName().equals(newValue)) {
                    cityId = city.get(i).getCityID();
                    break;
                }
            }
            cbDistrict.getItems().clear();
            List<District> district = getDistrictList(cityId);
            if (district != null) {
                for (int i = 0; i < district.size(); i++) {
                    cbDistrict.getItems().add(district.get(i).getDistrictName());
                }
                cbDistrict.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
                    String districtId = "";
                    for (int i = 0; i < district.size(); i++) {
                        if (district.get(i).getDistrictName().equals(newVal)) {
                            districtId = district.get(i).getDistrictID();
                            break;
                        }
                    }
                    cbWard.getItems().clear();
                    List<Ward> ward = getWardList(districtId);
                    if (ward != null) {
                        for (int i = 0; i < ward.size(); i++) {
                            cbWard.getItems().add(ward.get(i).getWardName());
                        }
                    }
                });
            }

        });

        List<String> relatedList = manager.getUserIDList();
        for (int i = 0; i < relatedList.size(); i++) {
            cbRelated.getItems().add(relatedList.get(i));
        }
    }

    private boolean isValid() {
        boolean valid = true;
        String header = "Thêm mới người liên quan Covid19";
        if (tfFullName.getText().equals("") || tfIdentityCard.getText().equals("")
                || tfBirthYear.getText().equals("")) {
            showAlert(header, "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (cbCity.getValue() == null || cbDistrict.getValue() == null || cbWard.getValue() == null
                || cbRelated.getValue() == null) {
            showAlert(header, "Vui lòng chọn trạng thái, địa chỉ nơi ở và người liên quan!");
            valid = false;
        } else if (isNumberExisted(tfFullName.getText())) {
            showAlert(header, "Vui lòng chỉ điền chữ cho họ tên!");
            valid = false;
        } else if (isCharacterExisted(tfIdentityCard.getText()) || isCharacterExisted(tfBirthYear.getText())) {
            showAlert(header, "Vui lòng chỉ điền số cho chứng minh nhân dân và năm sinh!");
            valid = false;
        } 
        return valid;
    }

    private boolean isCharacterExisted(String content) {
        for (int i = 0; i < content.length(); i++) {
            if (Character.isLetter(content.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumberExisted(String content) {
        for (int i = 0; i < content.length(); i++) {
            if (Character.isDigit(content.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void refreshInput() {
        tfFullName.setText("");
        tfBirthYear.setText("");
        tfIdentityCard.setText("");
        cbCity.getSelectionModel().clearSelection();
        cbDistrict.getSelectionModel().clearSelection();
        cbWard.getSelectionModel().clearSelection();
        cbRelated.getSelectionModel().clearSelection();
    }

    private ManagedUser getCurrentInput() {
        ManagedUser user = null;
        String ID = tfIdentityCard.getText();
        String name = tfFullName.getText();
        int birthYear = Integer.valueOf(tfBirthYear.getText());
        String address = cbCity.getValue() + ", " + cbDistrict.getValue() + ", " + cbWard.getValue();

        currentState = manager.getCurrentState(cbRelated.getValue()) + 1;
        user = new ManagedUser(ID, name, birthYear, cbRelated.getValue(), 0, address, currentState);

        return user;
    }

}
