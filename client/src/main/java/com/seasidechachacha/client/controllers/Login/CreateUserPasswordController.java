package com.seasidechachacha.client.controllers.Login;

import com.seasidechachacha.client.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class CreateUserPasswordController {
    @FXML
    private TextField pass1;
    
    @FXML
    private TextField pass2;
    
    @FXML
    private void back() throws IOException{ // khi người dùng ấn button "quay lại"
        App.setRoot("view/Login");
    }
    
    
    public void createPassword(ActionEvent e) throws IOException{ // khi người dùng ấn button "tiếp tục"
        if(pass1.getText().compareTo(pass2.getText())!=0){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Mật khẩu giữa 2 lần nhập không giống nhau.\n Xin hãy nhập lại!");
            a.show();
        }
        else
        {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Khởi tạo mật khẩu thành công!!!");
            a.show();
            // khởi tạo thành công, cập nhật database và cho vô screen User
            App.initializeMainScreen();  // này là screen moderator :))
        }
    }
}
