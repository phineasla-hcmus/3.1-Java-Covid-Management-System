package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagerDao.updatePackageDayCooldown;
import static com.seasidechachacha.client.database.ManagerDao.updatePackageLimitPerPerson;
import static com.seasidechachacha.client.database.ManagerDao.updatePackageName;
import static com.seasidechachacha.client.database.ManagerDao.updatePackagePrice;
import com.seasidechachacha.client.models.Package;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class ViewPackageInfoController {

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
            Logger.getLogger(ViewPackageInfoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setup(Package pack) {
        String packageID = pack.getPackageID();
        // TODO
        // to get the latest update of package
        // if not, it will show the old value
//        Package curPack = getPackageById(packageID);
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
                updatePackageName(pack.getPackageID(), result.get());
                labelName.setText(result.get());
            }
        });

        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getLimitPerPerson()));
            dialog.setTitle("Thay đổi mức giới hạn");
            dialog.setHeaderText("Mức giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                updatePackageLimitPerPerson(pack.getPackageID(), Integer.valueOf(result.get()));
                labelLimit.setText(result.get());
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getDayCooldown()));
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                updatePackageDayCooldown(pack.getPackageID(), Integer.valueOf(result.get()));
                labelDay.setText(result.get());
            }
        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(pack.getPrice()));
            dialog.setTitle("Thay đổi đơn giá");
            dialog.setHeaderText("Đơn giá mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                updatePackagePrice(pack.getPackageID(), Integer.valueOf(result.get()));
                labelPrice.setText(result.get());
            }
        });

    }

}
