
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class ModeratorStatisticController {

    @FXML
    private ChoiceBox statisticType;

    @FXML
    private Button nextButton;

    @FXML
    private void initialize() {
        statisticType.getItems().addAll(
                "số lượng người ở từng trạng thái theo thời gian",
                "số lượng nhu yếu phẩm được tiêu thụ",
                "số chuyển trạng thái",
                "số dư nợ");

    }

    @FXML
    private void handleButton(ActionEvent e) throws IOException {
        if (statisticType.getSelectionModel().getSelectedItem()==null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Hãy chọn loại thống kê!!!");
            a.show();
        } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng người ở từng trạng thái theo thời gian")) {
            App.setCurrentPane("pn_all", "view/StatisticNumberStatus", null);
        } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng nhu yếu phẩm được tiêu thụ")) {
            App.setCurrentPane("pn_all", "view/StatisticNumberPackage", null);
        } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số chuyển trạng thái")) {
            App.setCurrentPane("pn_all", "view/StatisticNumberChangeStatus", null);
        } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số dư nợ")) {
            App.setCurrentPane("pn_all", "view/StatisticNumberBalance", null);
        }
    }
}
