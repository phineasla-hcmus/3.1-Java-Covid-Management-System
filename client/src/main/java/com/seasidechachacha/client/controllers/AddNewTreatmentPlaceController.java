package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.Ward;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddNewTreatmentPlaceController {

    private static final Logger logger = LogManager.getLogger(AddNewTreatmentPlaceController.class);

    @FXML
    private TextField tfName, tfCapacity, tfReception;

    @FXML
    private ComboBox<String> cbCity, cbDistrict, cbWard;

    AdminDao Tam = new AdminDao("admin-123456");

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        btnAdd.setOnAction(event -> {
            if (isValid()) {
                if (Tam.addTreatmentPlace(getCurrentInput())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý địa điểm điều trị/cách ly");
                    alert.setContentText("Thêm mới địa điểm điều trị/cách ly thành công!");
                    
                    alert.showAndWait();
                    refreshInput();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Quản lý địa điểm điều trị/cách ly");
                    alert.setContentText("Địa điểm điều trị đã tồn tại!");
                    
                    alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới địa điểm điều trị/cách ly");
            alert.setContentText("Vui lòng điền đầy đủ thông tin!");

            alert.showAndWait();
            valid = false;
        } else if (cbCity.getValue().equals("") || cbDistrict.getValue().equals("") || cbWard.getValue().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới địa điểm điều trị/cách ly");
            alert.setContentText("Vui lòng chọn địa chỉ của địa điểm điều trị!");

            alert.showAndWait();
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
