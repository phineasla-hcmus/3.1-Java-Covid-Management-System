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
    private Button btnChangeName, btnChangeCapacity;

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
            dialog.setTitle("Thay ?????i t??n ?????a ??i???m");
            dialog.setHeaderText("T??n ?????a ??i???m m???i");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "Vui l??ng nh???p t??n ?????a ??i???m ??i???u tr???!");
                } else if (!Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "T??n ?????a ??i???m ph???i bao g???m ch??? c??i!");
                } else if (admin.updateTreatmentPlaceName(treatID, result.get())) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "Thay ?????i t??n ?????a ??i???m th??nh c??ng!");
                    labelName.setText(result.get());
                }
            }
        });

        btnChangeCapacity.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(treat.getCapacity()));
            dialog.setTitle("Thay ?????i s???c ch???a");
            dialog.setHeaderText("S???c ch???a m???i");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "Vui l??ng nh???p s???c ch???a!");
                } else if (Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "S???c ch???a c???a ?????a ??i???m ??i???u tr???/c??ch ly ph???i l?? k?? t??? s???!");
                } else if (Integer.valueOf(result.get()) < 100 || Integer.valueOf(result.get()) > 1000) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "S???c ch???a c???a ?????a ??i???m ??i???u tr???/c??ch ly ph???i n???m trong kho???ng 100 - 1000!");
                } else if (Integer.valueOf(result.get()) < treat.getCurrentReception()) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "S???c ch???a c???a ?????a ??i???m ??i???u tr???/c??ch ly kh??ng ???????c nh??? h??n s??? l?????ng ti???p nh???n hi???n t???i");
                } else if (admin.updateTreatmentPlaceCapacity(treatID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin ?????a ??i???m ??i???u tr???/c??ch ly", "Thay ?????i s???c ch???a th??nh c??ng!");
                    labelCapacity.setText(result.get());
                    getTreatmentPlaceThread();
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
