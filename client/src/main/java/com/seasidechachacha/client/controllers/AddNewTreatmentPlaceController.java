package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;

import java.io.IOException;
import java.util.List;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.utils.Alert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewTreatmentPlaceController {

    private static final Logger logger = LogManager.getLogger(AddNewTreatmentPlaceController.class);

    @FXML
    private TextField tfName, tfCapacity, tfReception;

    @FXML
    private ComboBox<String> cbCity, cbDistrict, cbWard;

    private AdminDao admin = new AdminDao(Session.getUser().getUserId());

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        btnAdd.setOnAction(event -> {
            if (isValid()) {
                if (admin.addTreatmentPlace(getCurrentInput())) {
                    Alert.showAlert(AlertType.INFORMATION, "Quản lý địa điểm điều trị/cách ly", "Thêm mới địa điểm điều trị/cách ly thành công!");
                    refreshInput();
                } else {
                    Alert.showAlert(AlertType.WARNING, "Quản lý địa điểm điều trị/cách ly", "Địa điểm điều trị đã tồn tại!");
                }
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
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfName.getText().equals("") || tfReception.getText().equals("") || tfCapacity.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (cbCity.getValue().equals("") || cbDistrict.getValue().equals("") || cbWard.getValue().equals("")) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Vui lòng chọn địa chỉ của địa điểm điều trị!");
            valid = false;
        }
        return valid;
    }

    private void refreshInput() {
        tfName.setText("");
        tfCapacity.setText("");
        tfReception.setText("");
        cbCity.getItems().clear();
        cbDistrict.getItems().clear();
        cbWard.getItems().clear();
    }

    private TreatmentPlace getCurrentInput() {
        TreatmentPlace treat = null;
        String name = tfName.getText();
        int capacity = Integer.valueOf(tfCapacity.getText());
        int reception = Integer.valueOf(tfReception.getText());
        String address = cbCity.getValue() + ", " + cbDistrict.getValue() + ", " + cbWard.getValue();
        treat = new TreatmentPlace(0, name, address, capacity, reception);
        return treat;
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListTreatmentPlace", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

}
