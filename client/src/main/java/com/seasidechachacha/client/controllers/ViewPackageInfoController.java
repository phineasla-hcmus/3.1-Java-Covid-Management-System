package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.utils.Alert;
import com.seasidechachacha.client.utils.Validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

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
        TaskExecutor.execute(dataTask);
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

            if (result.isPresent() && !result.get().equals(pack.getName())) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Vui lòng nhập tên gói mới!");
                } else if (!Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Tên gói nhu yếu phẩm phải bao gồm chữ cái!");
                } else if (manager.updatePackageName(packageID, result.get())) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin nhu yếu phẩm", "Thay đổi tên gói thành công!");
                    labelName.setText(result.get());
                }
            }
        });
        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getLimitPerPerson()));
            dialog.setTitle("Thay đổi mức giới hạn");
            dialog.setHeaderText("Mức giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(String.valueOf(pack.getLimitPerPerson()))) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Vui lòng nhập mức giới hạn mới!");
                } else if (Validation.isCharacterExisted(result.get()) || Integer.valueOf(result.get()) <= 0) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Mức giới hạn phải là ký tự số và lớn hơn 0!");
                } else if (manager.updatePackageLimitPerPerson(packageID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin nhu yếu phẩm", "Thay đổi mức giới hạn thành công!");
                    labelLimit.setText(result.get());
                }
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getDayCooldown()));
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(String.valueOf(pack.getDayCooldown()))) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Vui lòng nhập thời gian giới hạn mới!");
                } else if (Validation.isCharacterExisted(result.get()) || Integer.valueOf(result.get()) <= 0) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Thời gian giới hạn phải là ký tự số và lớn hơn 0!");
                } else if (manager.updatePackageDayCooldown(packageID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin nhu yếu phẩm", "Thay đổi thời gian giới hạn thành công!");
                    labelDay.setText(result.get());
                }
            }
        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getPrice()));
            dialog.setTitle("Thay đổi đơn giá");
            dialog.setHeaderText("Đơn giá mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(String.valueOf(pack.getPrice()))) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Vui lòng nhập đơn giá mới!");
                } else if (Validation.isCharacterExisted(result.get()) || Integer.valueOf(result.get()) < 10000 || Integer.valueOf(result.get()) > 1000000) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Đơn giá phải là ký tự số và nằm trong khoảng 10000 - 1000000!");
                } else if (manager.updatePackagePrice(packageID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin nhu yếu phẩm", "Thay đổi đơn giá thành công!");
                    labelPrice.setText(result.get());
                }
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
        packageID = pack.getPackageID();
        getPackageThread();
    }

}
