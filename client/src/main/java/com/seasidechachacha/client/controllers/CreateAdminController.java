package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CreateAdminController {
    @FXML
    private TextField username;
    
    @FXML
    private TextField pass1;
    
    @FXML
    private TextField pass2;
    
    
    @FXML
    private void back() throws IOException{ // khi người dùng ấn button "quay lại"
        App.setRoot("view/Firstlogin");
    }
    
    
    public void createAdmin(ActionEvent e) throws IOException{ // khi người dùng ấn button "tiếp tục"
        if(pass1.getText().compareTo(pass2.getText())!=0){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        }
        else
        {
            App.setRoot("view/Login"); // này để test màn hình login khi mới tạo tkhoan admin , mốt có database thì cho vô admin luôn
        }
    }
}
