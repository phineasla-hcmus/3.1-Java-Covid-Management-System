package com.seasidechachacha.client.controllers;

//import java.net.URL;
import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getCityList;
import static com.seasidechachacha.client.database.ManagerDao.getDistrictList;
import static com.seasidechachacha.client.database.ManagerDao.getWardList;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.Ward;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    private ComboBox<String> cbCurrentStatus, cbCity, cbDistrict, cbWard, cbRelated;

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
                if (isValid()) {
                    // TODO
                    if (Tam.addNewUser(getCurrentInput())) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText("Quản lý người liên quan Covid19");
                        alert.setContentText("Thêm mới người dùng thành công!");

                        alert.showAndWait();
                        refreshInput();
                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText("Quản lý người liên quan Covid19");
                        alert.setContentText("Người dùng đã tồn tại!");

                        alert.showAndWait();
                    }
                }
            } catch (SQLException ex) {
                logger.fatal(ex);
            }
        });
        cbCurrentStatus.getItems().addAll("F0", "F1", "F2");
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
        // tạm thời để test
        cbRelated.getItems().addAll("079510193347", "079932368028");
    }

    private boolean isValid() {
        boolean valid = true;
        if (tfFullName.getText().equals("") || tfIdentityCard.getText().equals("") || tfBirthYear.getText().equals((""))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới người dùng");
            alert.setContentText("Vui lòng điền đầy đủ thông tin!");

            alert.showAndWait();
            valid = false;
        } else if (cbCurrentStatus.getValue().equals("") || cbCity.getValue().equals("") || cbDistrict.getValue().equals("") || cbWard.getValue().equals("") || cbRelated.getValue().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm mới người dùng");
            alert.setContentText("Vui lòng chọn trạng thái, địa chỉ nơi ở và danh sách người liên quan!");

            alert.showAndWait();
            valid = false;
        }
        return valid;
    }
    
    private void refreshInput() {
        tfFullName.setText(""); 
        tfBirthYear.setText(""); 
        tfIdentityCard.setText("");
        cbCurrentStatus.getItems().clear();
        cbCity.getItems().clear();
        cbDistrict.getItems().clear();
        cbWard.getItems().clear();
        cbRelated.getItems().clear();
    }

    private ManagedUser getCurrentInput() {
        ManagedUser user = null;
        String ID = tfIdentityCard.getText();
        String name = tfFullName.getText();
        int birthYear = Integer.valueOf(tfBirthYear.getText());
        String status = cbCurrentStatus.getValue();
        String relatedID = "079111222333";
//        String wardID = "30208";
        String address = cbCity.getValue() + ", " + cbDistrict.getValue() + ", " + cbWard.getValue();

        user = new ManagedUser(ID, name, birthYear, relatedID, 0, address);

        return user;
    }

}
