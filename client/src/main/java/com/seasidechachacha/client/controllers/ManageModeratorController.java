package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;


public class ManageModeratorController {

    @FXML
    private Button viewButton, updateButton;
    
    @FXML
    private ChoiceBox ModeratorUsername;
    
    @FXML
    private CheckBox activeChoice, inactiveChoice;
    
    @FXML
    private TableView activitiesTable;
    
    @FXML
    private AnchorPane ManagePane;
    

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == viewButton) { // khi nguoi dùng bấm nút xem dữ liệu dựa vào username ở choicebox và dựa vô database
            //sẽ hiển thị trạng thái có check box + bảng lịch sử hoạt động
            if (true) // nếu trạng thái ở trong database là đang hoạt động
            {
                activeChoice.setSelected(true);
            } else // ngược lại
            {
                inactiveChoice.setSelected(true);
            }

            ManagePane.setVisible(true);

        } else if (event.getSource() == updateButton) { // khi người dùng bấm cập nhật , sẽ lấy dữ liệu từ 2 nút active và inactive để cập nhật lại trạng thái trong database

            if (activeChoice.isSelected() == true) {
                //cap nhật database
            } else {
                //cap nhật database
            }
        }
    }
    
    @FXML
    private void handleCheckBox1(ActionEvent event) throws IOException{
        if(activeChoice.isSelected()==true)
        {
            inactiveChoice.setSelected(false);
        }
    }
    
    @FXML
    private void handleCheckBox2(ActionEvent event) throws IOException{
        if(inactiveChoice.isSelected()==true)
        {
            activeChoice.setSelected(false);
        }
    }
    
}


