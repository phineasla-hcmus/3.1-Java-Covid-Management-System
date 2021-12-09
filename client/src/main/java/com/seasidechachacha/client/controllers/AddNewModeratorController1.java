package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AddNewModeratorController1 {
    @FXML
    private TextField user;
    
    @FXML
    private TextField pass1;
    
    @FXML
    private TextField pass2;
    
  
    
    public void createModerator(ActionEvent e) throws IOException{ 
        if(pass1.getText().compareTo(pass2.getText())!=0){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        }
        else
        { //nhớ lưu vô database dưới dạng bản không rõ nha , thêm phân quyền moderator vô nữa.
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Thêm mới người quản lý thành công!!!");
            a.show();
        }
    }
}
