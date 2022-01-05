package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;

import java.io.IOException;
import java.util.List;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;
import javafx.concurrent.Task;

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
    private TextField tfName, tfCapacity;

    @FXML
    private ComboBox<String> cbCity, cbDistrict, cbWard;

    private AdminDao admin = new AdminDao(Session.getUser().getUserId());

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        btnAdd.setOnAction(event -> {
            if (isValid()) {
                addNewTreatmentPlaceThread();
            }
        });

        getCityListThread();
    }

    private void addNewTreatmentPlaceThread() {
        Task<Boolean> addNewTreatmentPlaceTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                TreatmentPlace treat = getCurrentInput();
                if (!admin.addTreatmentPlace(treat)) {
                    return false;
                }
                return true;
            }
        };

        addNewTreatmentPlaceTask.setOnSucceeded(e -> {
            boolean result = addNewTreatmentPlaceTask.getValue();
            if (result) {
                Alert.showAlert(AlertType.INFORMATION, "Quản lý địa điểm điều trị/cách ly", "Thêm mới địa điểm điều trị/cách ly thành công!");
                refreshInput();
            } else {
                Alert.showAlert(AlertType.WARNING, "Quản lý địa điểm điều trị/cách ly", "Địa điểm điều trị đã tồn tại!");
            }
        });

        TaskExecutor.execute(addNewTreatmentPlaceTask);
    }

    private void getCityListThread() {
        Task<List<City>> getCityListTask = new Task<List<City>>() {
            @Override
            public List<City> call() {
                return getCityList();
            }
        };

        getCityListTask.setOnSucceeded(e -> {
            List<City> cities = getCityListTask.getValue();
            for (int i = 0; i < cities.size(); i++) {
                cbCity.getItems().add(cities.get(i).getCityName());
            }

            cbCity.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                String cityId = "";
                for (int i = 0; i < cities.size(); i++) {
                    if (cities.get(i).getCityName().equals(newValue)) {
                        cityId = cities.get(i).getCityID();
                        break;
                    }
                }

                getDistrictListThread(cityId);
            });
        });

        TaskExecutor.execute(getCityListTask);
    }

    private void getDistrictListThread(String cityId) {
        Task<List<District>> getDistrictListTask = new Task<List<District>>() {
            @Override
            public List<District> call() {
                return getDistrictList(cityId);
            }
        };

        getDistrictListTask.setOnSucceeded(e -> {
            cbDistrict.getItems().clear();
            List<District> districts = getDistrictListTask.getValue();
            for (int i = 0; i < districts.size(); i++) {
                cbDistrict.getItems().add(districts.get(i).getDistrictName());
            }
            cbDistrict.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
                String districtId = "";
                for (int i = 0; i < districts.size(); i++) {
                    if (districts.get(i).getDistrictName().equals(newVal)) {
                        districtId = districts.get(i).getDistrictID();
                        break;
                    }
                }
                getWardListThread(districtId);
            });

        });

        TaskExecutor.execute(getDistrictListTask);
    }

    private void getWardListThread(String districtId) {
        Task<List<Ward>> getWardListTask = new Task<List<Ward>>() {
            @Override
            public List<Ward> call() {
                return getWardList(districtId);
            }
        };

        getWardListTask.setOnSucceeded(e -> {
            cbWard.getItems().clear();
            List<Ward> wards = getWardListTask.getValue();
            for (int i = 0; i < wards.size(); i++) {
                cbWard.getItems().add(wards.get(i).getWardName());
            }
        });

        TaskExecutor.execute(getWardListTask);
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfName.getText().equals("") || tfCapacity.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (cbCity.getValue() == null || cbDistrict.getValue() == null || cbWard.getValue() == null) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Vui lòng chọn địa chỉ của địa điểm điều trị!");
            valid = false;
        } else if (!checkPlaceName(tfName.getText()) || !checkPlaceCapacity(tfCapacity.getText())) {
            valid = false;
        }
        return valid;
    }

    private boolean checkPlaceName(String name) {
        if (!Validation.isCharacterExisted(name)) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Tên địa điểm điều trị/cách ly phải bao gồm chữ cái!");
            return false;
        }
        return true;
    }

    private boolean checkPlaceCapacity(String capacity) {
        if (Validation.isCharacterExisted(capacity)) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Sức chứa của địa điểm điều trị/cách ly phải là ký tự số!");
            return false;
        } else if (Integer.valueOf(capacity) < 100 || Integer.valueOf(capacity) > 1000) {
            Alert.showAlert(AlertType.WARNING, "Thêm mới địa điểm điều trị/cách ly", "Sức chứa của địa điểm điều trị/cách ly phải nằm trong khoảng 100 - 1000!");
            return false;
        }
        return true;
    }

    private void refreshInput() {
        tfName.setText("");
        tfCapacity.setText("");
        cbCity.getItems().clear();
        cbDistrict.getItems().clear();
        cbWard.getItems().clear();
    }

    private TreatmentPlace getCurrentInput() {
        TreatmentPlace treat = null;
        String name = tfName.getText();
        int capacity = Integer.valueOf(tfCapacity.getText());
        String address = cbCity.getValue() + ", " + cbDistrict.getValue() + ", " + cbWard.getValue();
        treat = new TreatmentPlace(0, name, address, capacity, 0);
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
