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
import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.payment.PaymentService;
import com.seasidechachacha.client.payment.RespondException;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;
import com.seasidechachacha.common.payment.ErrorResponseType;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddNewUserController {

    private static final Logger logger = LogManager.getLogger(AddNewUserController.class);

    // FREE 1 TRIỆU, THA HỒ MUA SẮM
    public static final int START_BALANCE = 1000000;

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
            if (isValid()) {
                addNewUserThread();
            }
        });
        getCityListThread();
        getUserIDListThread();
        getTreatmentPlaceListThread();
    }

    private void getUserIDListThread() {
        Task<List<String>> getRelatedListTask = new Task<List<String>>() {
            @Override
            public List<String> call() {
                return ManagerDao.getUserIDList();
            }
        };
        getRelatedListTask.setOnSucceeded(e -> {
            List<String> relatedList = getRelatedListTask.getValue();
            for (int i = 0; i < relatedList.size(); i++) {
                cbRelated.getItems().add(relatedList.get(i));
            }
        });

        TaskExecutor.execute(getRelatedListTask);
    }

    private void getTreatmentPlaceListThread() {
        Task<List<TreatmentPlace>> getRelatedListTask = new Task<List<TreatmentPlace>>() {
            @Override
            public List<TreatmentPlace> call() {
                return ManagerDao.getTreatmentPlaceList();
            }
        };
        getRelatedListTask.setOnSucceeded(e -> {
            List<TreatmentPlace> placeList = getRelatedListTask.getValue();
            for (int i = 0; i < placeList.size(); i++) {
                cbPlace.getItems().add(placeList.get(i).getName());
            }
        });

        TaskExecutor.execute(getRelatedListTask);
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

    private void addNewUserThread() {
        Task<Boolean> addNewUserTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                ManagedUser user = getCurrentInput();
                PaymentService ps = new PaymentService();
                try {
                    if (!manager.addNewUser(user, currentState, treatID)
                            || !UserDao.registerFirstLogin(user.getUserId())) {
                        return false;
                    }
                } catch (SQLException e) {
                    logger.error(e);
                }
                try {
                    ps.requestNewUser(user.getUserId(), START_BALANCE);
                } catch (RespondException e) {
                    // ErrorResponseType.ID_EXISTED
                    ErrorResponseType type = e.getType();
                    logger.error(type.name() + ": " + user.getUserId());
                    return false;
                } catch (IOException | ClassNotFoundException e) {
                    logger.error(e);
                    return false;
                }
                return true;
            }
        };

        addNewUserTask.setOnSucceeded(e -> {
            boolean result = addNewUserTask.getValue();
            if (result) {
                Alert.showAlert(AlertType.INFORMATION,
                        "Quản lý người liên quan Covid19",
                        "Thêm mới người dùng thành công!");
                refreshInput();
            } else {
                Alert.showAlert(AlertType.WARNING,
                        "Quản lý người liên quan Covid19",
                        "Người dùng đã tồn tại!");
            }
        });

        TaskExecutor.execute(addNewUserTask);
    }

    private boolean isValid() {
        boolean valid = true;
        String header = "Thêm mới người liên quan Covid19";
        if (tfFullName.getText().equals("") || tfIdentityCard.getText().equals("")
                || tfBirthYear.getText().equals("")) {
            Alert.showAlert(AlertType.WARNING, header, "Vui lòng điền đầy đủ thông tin!");
            valid = false;
        } else if (cbCity.getValue() == null || cbDistrict.getValue() == null || cbWard.getValue() == null
                || cbPlace.getValue() == null) {
            Alert.showAlert(AlertType.WARNING, header,
                    "Vui lòng chọn trạng thái, địa chỉ nơi ở và địa điểm điều trị!");
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
        if (birthYear >= 1903 && birthYear <= Calendar.getInstance().get(Calendar.YEAR)) {
            return true;
        } else {
            Alert.showAlert(AlertType.WARNING, header, "Năm sinh phải lớn hơn hoạc bằng 1903 và nhỏ hơn hoặc bằng năm hiện tại!");
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

        if (cbRelated.getValue() != null) {
            currentState = ManagerDao.getCurrentState(cbRelated.getValue()) + 1;
            user = new ManagedUser(ID, name, birthYear, cbRelated.getValue(), 0, address, currentState);
        } else {
            // ko chọn -> auto F0
            currentState = 0;
            user = new ManagedUser(ID, name, birthYear, "", 0, address, currentState);
        }

        treatID = ManagerDao.getTreatmentPlaceIDByName(cbPlace.getValue());
        System.out.println(treatID);

        return user;
    }

}
