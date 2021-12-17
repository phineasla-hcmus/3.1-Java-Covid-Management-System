package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagedUserDao.get;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentState;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentTreatmentPlace;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.TreatmentPlace;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInfoController {

    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);
    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private void initialize() {
        String userId = "079157952250";
        ManagedUser user = get(userId);
        labelFullName.setText(user.getName());
        labelIdentityCard.setText(user.getUserId());
        labelBirthYear.setText(String.valueOf(user.getBirthYear()));
        labelAddress.setText(user.getAddress());
        String currentStatus = "F" + getCurrentState(user.getUserId());
        labelStatus.setText(currentStatus);
        TreatmentPlace treat = getCurrentTreatmentPlace(user.getUserId());
        String currentPlace = treat.getName();
        if (treat != null) {
            labelTreatmentPlace.setText(treat.getName());
        }
    }
}
