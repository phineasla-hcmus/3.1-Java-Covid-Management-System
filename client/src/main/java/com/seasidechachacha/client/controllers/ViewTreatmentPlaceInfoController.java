package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.TreatmentPlace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private Executor exec;
    private int treatID;

    @FXML
    private void initialize() {
        exec = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
    }

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
        exec.execute(dataTask);
    }

    public void resolveTreatmentPlace(WorkerStateEvent e, TreatmentPlace treat) throws IOException {
        labelName.setText(treat.getName());
        labelAddress.setText(treat.getAddress());
        labelCapacity.setText(String.valueOf(treat.getCapacity()));
        labelReception.setText(String.valueOf(treat.getCurrentReception()));

        AdminDao Tam = new AdminDao("admin-123456");
        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(treat.getName());
            dialog.setTitle("Thay đổi tên địa điểm");
            dialog.setHeaderText("Tên địa điểm mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updateTreatmentPlaceName(treatID, result.get())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin địa điểm điều trị/cách ly");
                    alert.setContentText("Thay đổi tên địa điểm điều trị/cách ly thành công!");

                    alert.showAndWait();
                }
                labelName.setText(result.get());
            }
        });

        btnChangeCapacity.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(treat.getCapacity()));
            dialog.setTitle("Thay đổi sức chứa");
            dialog.setHeaderText("Sức chứa mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updateTreatmentPlaceCapacity(treatID, Integer.valueOf(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin địa điểm điều trị/cách ly");
                    alert.setContentText("Thay đổi sức chứa địa điểm điều trị/cách ly thành công!");

                    alert.showAndWait();
                }

                labelCapacity.setText(result.get());
            }
        });

        btnChangeReception.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(treat.getCurrentReception()));
            dialog.setTitle("Thay đổi số lượng tiếp nhận hiện tại");
            dialog.setHeaderText("Số lượng tiếp nhận mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updateTreatmentPlaceCurrentReception(treatID, Integer.valueOf(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin địa điểm điều trị/cách ly");
                    alert.setContentText("Thay đổi số lượng tiếp nhận hiện tại của địa điểm điều trị/cách ly thành công!");

                    alert.showAndWait();
                }
                labelReception.setText(result.get());
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
