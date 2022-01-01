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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

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

    private int getDayCooldown(ComboBox<String> cbFirst, ComboBox<String> cbSecond, ComboBox<String> cbThird,
            TextField tfFirst, TextField tfSecond, TextField tfThird) {
        int day = 0;
        if (tfThird.isVisible()) {
            day += Integer.valueOf(tfThird.getText());
        }
        if (tfSecond.isVisible()) {
            if (cbSecond.getValue().toString().equals("ngày")) {
                day += Integer.valueOf(tfSecond.getText());
            } else if (cbSecond.getValue().toString().equals("tuần")) {
                day += 7 * Integer.valueOf(tfSecond.getText());
            }
        }
        int first = Integer.valueOf(tfFirst.getText());
        switch (cbFirst.getValue().toString()) {
            case "tháng":
                day += 30 * first;
                break;
            case "tuần":
                day += 7 * first;
                break;
            case "ngày":
                day += first;
                break;
            default:
                break;
        }
        return day;
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
            Dialog dialog = new Dialog<>();
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            DialogPane dialogPane = dialog.getDialogPane();
            TextField tfFirst = new TextField();
            ComboBox<String> cbFirst = new ComboBox<>();
            cbFirst.getItems().addAll("ngày", "tuần", "tháng");
            TextField tfSecond = new TextField();
            ComboBox<String> cbSecond = new ComboBox<>();
            cbSecond.getItems().addAll("ngày", "tuần");
            TextField tfThird = new TextField();
            ComboBox<String> cbThird = new ComboBox<>();
            cbThird.getItems().addAll("ngày");

            int day = pack.getDayCooldown();
            if (day >= 30) {
                tfFirst.setText(String.valueOf(day / 30));
                cbFirst.setValue("tháng");
                day = day % 30;
                cbSecond.getItems().clear();
                cbSecond.getItems().addAll("ngày", "tuần");
                tfSecond.setText(String.valueOf(day / 7));
                cbSecond.setValue("tuần");
                day = day % 7;
                tfThird.setText(String.valueOf(day));
                cbThird.setValue("ngày");
            } else if (day >= 7 && day < 30) {
                tfFirst.setText(String.valueOf(day / 7));
                cbFirst.setValue("tuần");
                day = day % 7;
                if (day != 0) {
                    tfSecond.setText(String.valueOf(day));
                    cbSecond.getItems().clear();
                    cbSecond.getItems().addAll("ngày");
                    cbSecond.setValue("ngày");
                    tfThird.setVisible(false);
                    cbThird.setVisible(false);
                }
            } else {
                tfFirst.setText(String.valueOf(day));
                cbFirst.setValue("ngày");
                tfSecond.setVisible(false);
                cbSecond.setVisible(false);
                tfThird.setVisible(false);
                cbThird.setVisible(false);
            }

            cbFirst.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
                cbSecond.getItems().clear();
                if (newVal != null) {
                    if (newVal.equals("tháng") || newVal.equals("tuần")) {
                        cbSecond.setVisible(true);
                        tfSecond.setVisible(true);
                        if (newVal.equals("tháng")) {
                            cbSecond.getItems().addAll("ngày", "tuần");
                        } else if (newVal.equals("tuần")) {
                            cbSecond.getItems().addAll("ngày");
                            tfThird.setVisible(false);
                            cbThird.setVisible(false);
                        }
                    } else {
                        cbSecond.setVisible(false);
                        tfSecond.setVisible(false);
                    }
                }
            });

            cbSecond.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
                cbThird.getItems().clear();
                if (newVal != null) {
                    if (newVal.equals("tuần")) {
                        cbThird.setVisible(true);
                        tfThird.setVisible(true);
                        cbThird.getItems().addAll("ngày");
                    } else {
                        cbThird.setVisible(false);
                        tfThird.setVisible(false);
                    }
                }
            });

            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialogPane.setContent(new VBox(8, new HBox(8, tfFirst, cbFirst), new HBox(8, tfSecond, cbSecond), new HBox(8, tfThird, cbThird)));
            Optional<ButtonType> result = dialog.showAndWait();

            boolean valid = true;
            if (result.get() == ButtonType.OK) {
                if ((tfFirst.isVisible() && tfFirst.getText().equals(""))
                        || (tfSecond.isVisible() && tfSecond.getText().equals("")) || (tfThird.isVisible() && tfThird.getText().equals(""))) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Vui lòng nhập thời gian giới hạn mới!");
                    valid = false;
                } else if (Validation.isCharacterExisted(tfFirst.getText())
                        || Validation.isCharacterExisted(tfSecond.getText()) || Validation.isCharacterExisted(tfThird.getText())) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Thời gian giới hạn chỉ bao gồm ký tự số!");
                    valid = false;
                } else if ((tfFirst.isVisible() && Integer.valueOf(tfFirst.getText()) < 0)
                        || (tfSecond.isVisible() && Integer.valueOf(tfSecond.getText()) < 0)
                        || (tfThird.isVisible() && Integer.valueOf(tfThird.getText()) < 0)) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin nhu yếu phẩm", "Thời gian giới hạn phải lớn hơn hoặc bằng 0!");
                    valid = false;
                }
                if (valid == false) {
                    return;
                }
                int newday = getDayCooldown(cbFirst, cbSecond, cbThird, tfFirst, tfSecond, tfThird);

                if (manager.updatePackageDayCooldown(packageID, newday)) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin nhu yếu phẩm", "Thay đổi thời gian giới hạn thành công!");
                    labelDay.setText(String.valueOf(newday));
                    getPackageThread();
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
