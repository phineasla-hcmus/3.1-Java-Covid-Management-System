package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getTreatmentPlaceByID;
import static com.seasidechachacha.client.database.ManagerDao.getTreatmentPlaceIDByName;
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
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewUserController {

    private static final Logger logger = LogManager.getLogger(AddNewUserController.class);

    @FXML
    private TextField tfFullName, tfBirthYear, tfIdentityCard;

    @FXML
    private ComboBox<String> cbCity, cbDistrict, cbWard, cbRelated, cbPlace;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

    @FXML
    private Button btnAddNewPerson;

    private int currentState, treatID;

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
                    if (manager.addNewUser(getCurrentInput(), currentState, treatID)) {
                        Alert.showAlert(AlertType.INFORMATION, "Quản lý người liên quan Covid19",
                                "Thêm mới người dùng thành công!");
                        refreshInput();
                    } else {
                        Alert.showAlert(AlertType.WARNING, "Quản lý người liên quan Covid19", "Người dùng đã tồn tại!");
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

        List<String> relatedList = ManagerDao.getUserIDList();
        for (int i = 0; i < relatedList.size(); i++) {
            cbRelated.getItems().add(relatedList.get(i));
        }

        List<TreatmentPlace> placeList = ManagerDao.getTreatmentPlaceList();
        for (int i = 0; i < placeList.size(); i++) {
            cbPlace.getItems().add(placeList.get(i).getName());
        }
    }

    private void addNewUserThread(ManagedUser user, int state, int treatID) {
        Task<Boolean> addNewUserTask;
    }

    private boolean isValid() {
        boolean valid = true;
        String header = "Thêm mới người liên quan Covid19";
        if (tfFullName.getText().equals("") || tfIdentityCard.getText().equals("")
                || tfBirthYear.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, header, "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (cbCity.getValue() == null || cbDistrict.getValue() == null || cbWard.getValue() == null
                || cbRelated.getValue() == null || cbPlace.getValue() == null) {
            Alert.showAlert(AlertType.WARNING, header,
                    "Vui lòng chọn trạng thái, địa chỉ nơi ở, người liên quan và địa điểm điều trị!");
            valid = false;
        } else if (Validation.isNumberExisted(tfFullName.getText())) {
            Alert.showAlert(AlertType.WARNING, header, "Vui lòng chỉ điền chữ cho họ tên!");
            valid = false;
        } else if (Validation.isCharacterExisted(tfIdentityCard.getText())
                || Validation.isCharacterExisted(tfBirthYear.getText())) {
            Alert.showAlert(AlertType.WARNING, header, "Vui lòng chỉ điền số cho chứng minh nhân dân và năm sinh!");
            valid = false;
        } else if (!checkIdentityCard(header, tfIdentityCard.getText())
                || !checkBirthYear(header, Integer.valueOf(tfBirthYear.getText()))
                || !checkTreatmentCapacity(header, getTreatmentPlaceIDByName(cbPlace.getValue().toString()))) {
            valid = false;
        }
        return valid;
    }

    private boolean checkTreatmentCapacity(String header, int treatID) {
        TreatmentPlace t = getTreatmentPlaceByID(treatID);
        if (t.isFull()) {
            Alert.showAlert(AlertType.WARNING, header, "Địa điểm điều trị này đã hết chỗ!");
            return false;
        }
        return true;
    }

    private boolean checkIdentityCard(String header, String idCard) {
        // CMND phải là 9 hoặc 12 chữ số
        if (idCard.length() != 9 && idCard.length() != 12) {
            Alert.showAlert(AlertType.WARNING, header, "Chứng minh nhân dân phải có 9 hoặc 12 chữ số!");
            return false;
        }
        return true;
    }

    private boolean checkBirthYear(String header, Integer birthYear) {
        if (birthYear >= 1903 && birthYear <= 2021) {
            return true;
        } else {
            Alert.showAlert(AlertType.WARNING, header, "Năm sinh phải nằm trong khoảng 1903 - 2021");
            return false;
        }
    }

    private void refreshInput() {
        tfFullName.setText("");
        tfBirthYear.setText("");
        tfIdentityCard.setText("");
        cbCity.getSelectionModel().clearSelection();
        cbDistrict.getSelectionModel().clearSelection();
        cbWard.getSelectionModel().clearSelection();
        cbRelated.getSelectionModel().clearSelection();
        cbPlace.getSelectionModel().clearSelection();
    }

    private ManagedUser getCurrentInput() {
        ManagedUser user = null;
        String ID = tfIdentityCard.getText();
        String name = tfFullName.getText();
        int birthYear = Integer.valueOf(tfBirthYear.getText());
        String address = cbCity.getValue() + ", " + cbDistrict.getValue() + ", " + cbWard.getValue();

        currentState = ManagerDao.getCurrentState(cbRelated.getValue()) + 1;
        user = new ManagedUser(ID, name, birthYear, cbRelated.getValue(), 0, address, currentState);

        treatID = ManagerDao.getTreatmentPlaceIDByName(cbPlace.getValue());

        return user;
    }

}
