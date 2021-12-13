package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getPackageByID;
import static com.seasidechachacha.client.database.ManagerDao.getTreatmentPlaceByID;
import com.seasidechachacha.client.models.TreatmentPlace;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewTreatmentPlaceInfoController {

    private static final Logger logger = LogManager.getLogger(ViewPackageInfoController.class);

      @FXML
    private Label labelName, labelCapacity, labelReception;

    @FXML
    private Button btnChangeName, btnChangeCapacity, btnChangeReception;
    @FXML
    private void initialize() {
        // TODO
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
        int treatID = treat.getTreatID();
        TreatmentPlace currentTreat = getTreatmentPlaceByID(treatID);

        labelName.setText(currentTreat.getName());
        labelCapacity.setText(String.valueOf(currentTreat.getCapacity()));
        labelReception.setText(String.valueOf(currentTreat.getCurrentReception()));
       

        ManagerDao Tam = new ManagerDao("mod-19127268");
        btnChangeName.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(currentTreat.getName());
            dialog.setTitle("Thay đổi tên địa điểm");
            dialog.setHeaderText("Tên địa điểm mới");
            Optional<String> result = dialog.showAndWait();

//            if (result.isPresent()) {
//                if (Tam.updatePackageName(packageID, result.get())) {
//                    System.out.println("Update successfully");
//                }
//                labelName.setText(result.get());
//            }
        });

        btnChangeCapacity.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(currentTreat.getCapacity()));
            dialog.setTitle("Thay đổi sức chứa");
            dialog.setHeaderText("Sức chứa mới");
            Optional<String> result = dialog.showAndWait();

//            if (result.isPresent()) {
//                if (Tam.updatePackageLimitPerPerson(packageID, Integer.valueOf(result.get()))) {
//                    System.out.println("Update successfully");
//                }
//
//                labelLimit.setText(result.get());
//            }
        });

        btnChangeReception.setOnAction(event -> {
            Dialog dialog = new TextInputDialog(String.valueOf(currentTreat.getCurrentReception()));
            dialog.setTitle("Thay đổi số lượng tiếp nhận hiện tại");
            dialog.setHeaderText("Số lượng tiếp nhận mới");
            Optional<String> result = dialog.showAndWait();

//            if (result.isPresent()) {
//                if (Tam.updatePackageDayCooldown(packageID, Integer.valueOf(result.get()))) {
//                    System.out.println("Update successfully");
//                }
//                labelDay.setText(result.get());
//            }
        });

    }

}
