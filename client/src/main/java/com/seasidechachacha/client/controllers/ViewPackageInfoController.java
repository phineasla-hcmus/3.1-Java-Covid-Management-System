package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getPackageByID;
import com.seasidechachacha.client.models.Package;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
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

    @FXML
    private void initialize() {
        // TODO
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
        String packageID = pack.getPackageID();
        Package currentPack = getPackageByID(packageID);

        labelName.setText(currentPack.getName());
        labelLimit.setText(String.valueOf(currentPack.getLimitPerPerson()));
        labelDay.setText(String.valueOf(currentPack.getDayCooldown()));
        labelPrice.setText(String.valueOf(currentPack.getPrice()));

        ManagerDao Tam = new ManagerDao("mod-19127268");
        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(currentPack.getName());
            dialog.setTitle("Thay đổi tên gói");
            dialog.setHeaderText("Tên gói mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageName(packageID, result.get())) {
                    System.out.println("Update successfully");
                }
                labelName.setText(result.get());
            }
        });

        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(currentPack.getLimitPerPerson()));
            dialog.setTitle("Thay đổi mức giới hạn");
            dialog.setHeaderText("Mức giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageLimitPerPerson(packageID, Integer.valueOf(result.get()))) {
                    System.out.println("Update successfully");
                }

                labelLimit.setText(result.get());
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(currentPack.getDayCooldown()));
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackageDayCooldown(packageID, Integer.valueOf(result.get()))) {
                    System.out.println("Update successfully");
                }
                labelDay.setText(result.get());
            }
        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(currentPack.getPrice()));
            dialog.setTitle("Thay đổi đơn giá");
            dialog.setHeaderText("Đơn giá mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (Tam.updatePackagePrice(packageID, Integer.valueOf(result.get()))) {
                    System.out.println("Update successfully");
                }
                labelPrice.setText(result.get());
            }
        });

    }

}
