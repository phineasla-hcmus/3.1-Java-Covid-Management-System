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
            if (cbSecond.getValue().toString().equals("ng??y")) {
                day += Integer.valueOf(tfSecond.getText());
            } else if (cbSecond.getValue().toString().equals("tu???n")) {
                day += 7 * Integer.valueOf(tfSecond.getText());
            }
        }
        int first = Integer.valueOf(tfFirst.getText());
        switch (cbFirst.getValue().toString()) {
            case "th??ng":
                day += 30 * first;
                break;
            case "tu???n":
                day += 7 * first;
                break;
            case "ng??y":
                day += first;
                break;
            default:
                break;
        }
        return day;
    }
    
     public String parseDayCooldown(int day) {
        String result = "";
        if (day < 7) {
            result += day + " ng??y";
        } else if (day >= 7 && day < 30) {
            result += (day / 7) + " tu???n ";
            if (day % 7 > 0) {
                result += (day % 7) + " ng??y";
            }
        } else {
            result += (day / 30) + " th??ng ";
            day = day % 30;
            if (day >= 7 && day < 30) {
                result += (day / 7) + " tu???n ";
                if (day % 7 > 0) {
                    result += (day % 7) + " ng??y";
                }
            }
        }
        return result;
    }

    public void resolvePackage(WorkerStateEvent e, Package pack) throws IOException {
        labelName.setText(pack.getName());
        labelLimit.setText(String.valueOf(pack.getLimitPerPerson()));
        labelDay.setText(parseDayCooldown(pack.getDayCooldown()));
        labelPrice.setText(String.valueOf(pack.getPrice()));
        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getName());
            dialog.setTitle("Thay ?????i t??n g??i");
            dialog.setHeaderText("T??n g??i m???i");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(pack.getName())) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Vui l??ng nh???p t??n g??i m???i!");
                } else if (!Validation.isCharacterExisted(result.get())) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "T??n g??i nhu y???u ph???m ph???i bao g???m ch??? c??i!");
                } else if (manager.updatePackageName(packageID, result.get())) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin nhu y???u ph???m", "Thay ?????i t??n g??i th??nh c??ng!");
                    labelName.setText(result.get());
                    getPackageThread();
                }
            }
        });
        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getLimitPerPerson()));
            dialog.setTitle("Thay ?????i m???c gi???i h???n");
            dialog.setHeaderText("M???c gi???i h???n m???i");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(String.valueOf(pack.getLimitPerPerson()))) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Vui l??ng nh???p m???c gi???i h???n m???i!");
                } else if (Validation.isCharacterExisted(result.get()) || Integer.valueOf(result.get()) <= 0) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "M???c gi???i h???n ph???i l?? k?? t??? s??? v?? l???n h??n 0!");
                } else if (manager.updatePackageLimitPerPerson(packageID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin nhu y???u ph???m", "Thay ?????i m???c gi???i h???n th??nh c??ng!");
                    labelLimit.setText(result.get());
                    getPackageThread();
                }
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new Dialog<>();
            dialog.setTitle("Thay ?????i th???i gian gi???i h???n");
            dialog.setHeaderText("Th???i gian gi???i h???n m???i");
            DialogPane dialogPane = dialog.getDialogPane();
            TextField tfFirst = new TextField();
            ComboBox<String> cbFirst = new ComboBox<>();
            cbFirst.getItems().addAll("ng??y", "tu???n", "th??ng");
            TextField tfSecond = new TextField();
            ComboBox<String> cbSecond = new ComboBox<>();
            cbSecond.getItems().addAll("ng??y", "tu???n");
            TextField tfThird = new TextField();
            ComboBox<String> cbThird = new ComboBox<>();
            cbThird.getItems().addAll("ng??y");

            int day = pack.getDayCooldown();
            if (day >= 30) {
                tfFirst.setText(String.valueOf(day / 30));
                cbFirst.setValue("th??ng");
                day = day % 30;
                cbSecond.getItems().clear();
                cbSecond.getItems().addAll("ng??y", "tu???n");
                tfSecond.setText(String.valueOf(day / 7));
                cbSecond.setValue("tu???n");
                day = day % 7;
                tfThird.setText(String.valueOf(day));
                cbThird.setValue("ng??y");
            } else if (day >= 7 && day < 30) {
                tfFirst.setText(String.valueOf(day / 7));
                cbFirst.setValue("tu???n");
                day = day % 7;
                if (day != 0) {
                    tfSecond.setText(String.valueOf(day));
                    cbSecond.getItems().clear();
                    cbSecond.getItems().addAll("ng??y");
                    cbSecond.setValue("ng??y");
                    tfThird.setVisible(false);
                    cbThird.setVisible(false);
                }
            } else {
                tfFirst.setText(String.valueOf(day));
                cbFirst.setValue("ng??y");
                tfSecond.setVisible(false);
                cbSecond.setVisible(false);
                tfThird.setVisible(false);
                cbThird.setVisible(false);
            }

            cbFirst.getSelectionModel().selectedItemProperty().addListener((opts, oldVal, newVal) -> {
                cbSecond.getItems().clear();
                if (newVal != null) {
                    if (newVal.equals("th??ng") || newVal.equals("tu???n")) {
                        cbSecond.setVisible(true);
                        tfSecond.setVisible(true);
                        if (newVal.equals("th??ng")) {
                            cbSecond.getItems().addAll("ng??y", "tu???n");
                        } else if (newVal.equals("tu???n")) {
                            cbSecond.getItems().addAll("ng??y");
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
                    if (newVal.equals("tu???n")) {
                        cbThird.setVisible(true);
                        tfThird.setVisible(true);
                        cbThird.getItems().addAll("ng??y");
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
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Vui l??ng nh???p th???i gian gi???i h???n m???i!");
                    valid = false;
                } else if (Validation.isCharacterExisted(tfFirst.getText())
                        || Validation.isCharacterExisted(tfSecond.getText()) || Validation.isCharacterExisted(tfThird.getText())) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Th???i gian gi???i h???n ch??? bao g???m k?? t??? s???!");
                    valid = false;
                } else if ((tfFirst.isVisible() && Integer.valueOf(tfFirst.getText()) < 0)
                        || (tfSecond.isVisible() && Integer.valueOf(tfSecond.getText()) < 0)
                        || (tfThird.isVisible() && Integer.valueOf(tfThird.getText()) < 0)) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Th???i gian gi???i h???n ph???i l???n h??n ho???c b???ng 0!");
                    valid = false;
                }
                if (valid == false) {
                    return;
                }
                int newday = getDayCooldown(cbFirst, cbSecond, cbThird, tfFirst, tfSecond, tfThird);

                if (manager.updatePackageDayCooldown(packageID, newday)) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin nhu y???u ph???m", "Thay ?????i th???i gian gi???i h???n th??nh c??ng!");
                    labelDay.setText(String.valueOf(newday));
                    getPackageThread();
                }
            }

        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getPrice()));
            dialog.setTitle("Thay ?????i ????n gi??");
            dialog.setHeaderText("????n gi?? m???i");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().equals(String.valueOf(pack.getPrice()))) {
                if (result.get().equals("")) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "Vui l??ng nh???p ????n gi?? m???i!");
                } else if (Validation.isCharacterExisted(result.get()) || Integer.valueOf(result.get()) < 10000 || Integer.valueOf(result.get()) > 1000000) {
                    Alert.showAlert(AlertType.WARNING, "C???p nh???t th??ng tin nhu y???u ph???m", "????n gi?? ph???i l?? k?? t??? s??? v?? n???m trong kho???ng 10000 - 1000000!");
                } else if (manager.updatePackagePrice(packageID, Integer.valueOf(result.get()))) {
                    Alert.showAlert(AlertType.INFORMATION, "C???p nh???t th??ng tin nhu y???u ph???m", "Thay ?????i ????n gi?? th??nh c??ng!");
                    labelPrice.setText(result.get());
                    getPackageThread();
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
