package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.MyPackage;
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

    public void setup(MyPackage pack) {
        labelName.setText(pack.getName());
        labelLimit.setText(pack.getLimitPerPerson());
        labelDay.setText(pack.getDayCooldown());
        labelPrice.setText(pack.getUnitPrice());

        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getName());
            dialog.setTitle("Thay đổi tên gói");
            dialog.setHeaderText("Tên gói mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                labelName.setText(result.get());
            }
        });

        btnChangeLimit.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getLimitPerPerson());
            dialog.setTitle("Thay đổi mức giới hạn");
            dialog.setHeaderText("Mức giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                labelLimit.setText(result.get());
            }
        });

        btnChangeDay.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getDayCooldown());
            dialog.setTitle("Thay đổi thời gian giới hạn");
            dialog.setHeaderText("Thời gian giới hạn mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                labelDay.setText(result.get());
            }
        });

        btnChangePrice.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(pack.getUnitPrice());
            dialog.setTitle("Thay đổi đơn giá");
            dialog.setHeaderText("Đơn giá mới");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                labelPrice.setText(result.get());
            }
        });

    }

}
