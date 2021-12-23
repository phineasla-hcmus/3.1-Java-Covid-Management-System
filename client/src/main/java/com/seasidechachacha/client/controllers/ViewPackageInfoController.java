package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.Package;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class ViewPackageInfoController {

    private static final Logger logger = LogManager.getLogger(ViewPackageInfoController.class);

    @FXML
    private Label labelName, labelLimit, labelDay, labelPrice;

    @FXML
    private Button btnChangeName, btnChangeLimit, btnChangeDay, btnChangePrice;

    private int packageID;

    private Executor exec;
//    private Package currentPack;

    ManagerDao Tam = new ManagerDao("mod-19127268");

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

    private void getPackageThread() {
        Task<Package> dataTask = new Task<Package>() {
            @Override
            public Package call() {
                return ManagerDao.getPackageByID(packageID);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolvePackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    public void resolvePackage(WorkerStateEvent e, Package pack) throws IOException {
        labelName.setText(pack.getName());
        labelLimit.setText(String.valueOf(pack.getLimitPerPerson()));
        labelDay.setText(String.valueOf(pack.getDayCooldown()));
        labelPrice.setText(String.valueOf(pack.getPrice()));
        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getName());
            dialog.setTitle("Thay đổi tên gói");
            dialog.setHeaderText("Tên gói mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageName(packageID, result.get())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin nhu yếu phẩm");
                    alert.setContentText("Thay đổi tên gói thành công!");

                    alert.showAndWait();
                }
                labelName.setText(result.get());
            }
        });
        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getLimitPerPerson()));
            dialog.setTitle("Thay đổi mức giới hạn");
            dialog.setHeaderText("Mức giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageLimitPerPerson(packageID, Integer.valueOf(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin nhu yếu phẩm");
                    alert.setContentText("Thay đổi mức giới hạn thành công!");

                    alert.showAndWait();
                }

                labelLimit.setText(result.get());
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getDayCooldown()));
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageDayCooldown(packageID, Integer.valueOf(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin nhu yếu phẩm");
                    alert.setContentText("Thay đổi thời gian giới hạn thành công!");

                    alert.showAndWait();
                }
                labelDay.setText(result.get());
            }
        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getPrice()));
            dialog.setTitle("Thay đổi đơn giá");
            dialog.setHeaderText("Đơn giá mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackagePrice(packageID, Integer.valueOf(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Cập nhật thông tin nhu yếu phẩm");
                    alert.setContentText("Thay đổi đơn giá thành công!");

                    alert.showAndWait();
                }
                labelPrice.setText(result.get());
            }
        });
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListPackage", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    public void setup(Package pack) {
//        this.currentPack = pack;
        packageID = pack.getPackageID();
        getPackageThread();
    }

}
