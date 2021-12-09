/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

/**
 *
 * @author Admin
 */
public class StatisticNumberPackageController {

    @FXML
    private ScrollPane pn_all;

    @FXML
    private ChoiceBox statisticType;

    @FXML
    private Button nextButton, dateButton, monthButton,allButton;

    @FXML
    private DatePicker dateInput, monthInput;

    @FXML
    private TableView packageTable;

    @FXML
    private void initialize() {
        statisticType.getItems().addAll("số lượng người ở từng trạng thái theo thời gian", "số lượng nhu yếu phẩm được tiêu thụ", "số chuyển trạng thái", "số dư nợ");
        statisticType.setValue("số lượng nhu yếu phẩm được tiêu thụ");
    }

    @FXML
    private void handleButton(ActionEvent e) throws IOException {
        if (e.getSource() == nextButton) { // để đổi bảng thống kê th , ko cần qtam:))
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
        } else if (e.getSource() == dateButton) {  //nếu người dùng bấm vào nút "theo ngày", 
            //sẽ dựa vào ngày để thống kê số lượng tiêu thụ theo ngày trong database và hiển thị trong bảng

        } else if (e.getSource() == monthButton) { // tương tự trên nhưng chỉ dựa vào tháng và năm

        }
        else if (e.getSource()== allButton){ // thống kê số lượng không quan tâm tới ngày tháng năm
            
        }
    }
}
