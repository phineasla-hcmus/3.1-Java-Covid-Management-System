package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.util.Optional;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class ViewTreatmentPlaceInfoController {

    private static final Logger logger = LogManager.getLogger(ViewPackageInfoController.class);

    @FXML
    private Label labelName, labelAddress, labelCapacity, labelReception;

    @FXML
    private Button btnChangeName, btnChangeCapacity, btnChangeReception;

    private int treatID;

    private AdminDao admin = new AdminDao(Session.getUser().getUserId());

    private void getTreatmentPlaceThread() {
        Task<TreatmentPlace> dataTask = new Task<TreatmentPlace>() {
            @Override
            public TreatmentPlace call() {
                return ManagerDao.getTreatmentPlaceByID(treatID);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveTreatmentPlace(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveTreatmentPlace(WorkerStateEvent e, TreatmentPlace treat) throws IOException {
        labelName.setText(treat.getName());
        labelAddress.setText(treat.getAddress());
        labelCapacity.setText(String.valueOf(treat.getCapacity()));
        labelReception.setText(String.valueOf(treat.getCurrentReception()));

        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(treat.getName());
            dialog.setTitle("Thay đổi tên địa điểm");
            dialog.setHeaderText("Tên địa điểm mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Vui lòng nhập tên địa điểm điều trị!");
                } else if (!Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Tên địa điểm phải bao gồm chữ cái!");
                } else if (admin.updateTreatmentPlaceName(treatID, result.get())) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin địa điểm điều trị/cách ly", "Thay đổi tên địa điểm thành công!");
                    labelName.setText(result.get());
                }
            }
        });

        btnChangeCapacity.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(treat.getCapacity()));
            dialog.setTitle("Thay đổi sức chứa");
            dialog.setHeaderText("Sức chứa mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Vui lòng nhập sức chứa!");
                } else if (Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Sức chứa của địa điểm điều trị/cách ly phải là ký tự số!");
                } else if (Integer.valueOf(result.get()) < 100 || Integer.valueOf(result.get()) > 1000) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Sức chứa của địa điểm điều trị/cách ly phải nằm trong khoảng 100 - 1000!");
                } else if (admin.updateTreatmentPlaceCapacity(treatID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin địa điểm điều trị/cách ly", "Thay đổi sức chứa thành công!");
                    labelCapacity.setText(result.get());
                }
            }
        });

        btnChangeReception.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(treat.getCurrentReception()));
            dialog.setTitle("Thay đổi số lượng tiếp nhận hiện tại");
            dialog.setHeaderText("Số lượng tiếp nhận mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Vui lòng nhập số lượng tiếp nhận hiện tại!");
                } else if (Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Số lượng tiếp nhận hiện tại của địa điểm điều trị/cách ly phải là ký tự số!");
                } else if (Integer.valueOf(result.get()) < 0 || Integer.valueOf(result.get()) > treat.getCapacity()) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin địa điểm điều trị/cách ly", "Số lượng tiếp nhận hiện tại của địa điểm điều trị/cách ly phải lớn hơn 0 và nhỏ hơn sức chứa");
                } else if (admin.updateTreatmentPlaceCurrentReception(treatID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin địa điểm điều trị/cách ly", "Thay đổi số lượng tiếp nhận hiện tại thành công!");
                    labelReception.setText(result.get());
                }
            }
        });
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListTreatmentPlace", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    public void setup(TreatmentPlace treat) {
        treatID = treat.getTreatID();
        getTreatmentPlaceThread();

    }

}
