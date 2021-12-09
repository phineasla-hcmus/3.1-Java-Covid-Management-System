
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;



public class ModeratorStatisticController {
    @FXML
    private ScrollPane pn_all;
    
    @FXML
    private ChoiceBox statisticType;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private void initialize() {
        statisticType.getItems().addAll("số lượng người ở từng trạng thái theo thời gian","số lượng nhu yếu phẩm được tiêu thụ","số chuyển trạng thái","số dư nợ");
        
    }
    
    @FXML
    private void handleButton(ActionEvent e) throws IOException{
        if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng người ở từng trạng thái theo thời gian")) {
                App.setCurrentPane("pn_all", "view/StatisticNumberStatus", null);
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng nhu yếu phẩm được tiêu thụ")) {
                App.setCurrentPane("pn_all", "view/StatisticNumberPackage", null);
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số chuyển trạng thái")) {
                App.setCurrentPane("pn_all", "view/StatisticNumberChangeStatus", null);
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số dư nợ")) {
                App.setCurrentPane("pn_all", "view/StatisticNumberBalance", null);
                pn_all.toFront();
            }
    }
}
