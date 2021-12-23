package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.models.ActivityHistory;
import com.seasidechachacha.client.models.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;


public class ManageModeratorController {

    @FXML
    private Button viewButton, updateButton;
    
    @FXML
    private ChoiceBox ModeratorUsername;
    
    @FXML
    private CheckBox activeChoice, inactiveChoice;
    
    @FXML
    private TableView<ActivityHistory> activitiesTable;
    
    @FXML
    private AnchorPane ManagePane;
    
    @FXML
    private TableColumn<ActivityHistory,String> time;
    
    @FXML
    private TableColumn<ActivityHistory,String> logMsg;
    
    private ObservableList<ActivityHistory> listAct;
    
    private Executor exec;
    
    @FXML
    private void initialize() {
        
        exec = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        
        getModeratorThread();
      
        
        inactiveChoice.setOnAction(e -> {
            if(activeChoice.isSelected()==true)
        {
            inactiveChoice.setSelected(false);
        }
            
        });

        activeChoice.setOnAction(e -> {
            if(inactiveChoice.isSelected()==true)
        {
            activeChoice.setSelected(false);
        }
            
        });
        
        viewButton.setOnAction(e->{
            showModeratorThread();
        });
        
        updateButton.setOnAction(e->{
           if(inactiveChoice.isSelected()==true)
           {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);  
                alert.setTitle("Xác nhận BLOCK");
                alert.setHeaderText("BLOCK người quản lý: " + ModeratorUsername.getSelectionModel().getSelectedItem().toString());
                alert.setContentText("Hãy xác nhận BLOCK");

                
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == null) {
                } else if (option.get() == ButtonType.OK) { 
                    BanModeratorThread(ModeratorUsername.getSelectionModel().getSelectedItem().toString());
                } else if (option.get() == ButtonType.CANCEL) { 
                }
           }
           if(activeChoice.isSelected()==true)
           {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);  
                alert.setTitle("Xác nhận gỡ BLOCK");
                alert.setHeaderText("Gỡ BLOCK người quản lý: " + ModeratorUsername.getSelectionModel().getSelectedItem().toString());
                alert.setContentText("Hãy xác nhận gỡ BLOCK");

                
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == null) {
                } else if (option.get() == ButtonType.OK) { 
                    FreeModeratorThread(ModeratorUsername.getSelectionModel().getSelectedItem().toString());
                } else if (option.get() == ButtonType.CANCEL) { 
                }   
           }
        });
        
    }
 
    private void BanModeratorThread(String userId) {
        Task<Boolean> ban = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return AdminDao.banModerator(userId);
            }
        };
        ban.setOnSucceeded(e -> {
            try {
                resolveBanModerator(e, ban.getValue());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        exec.execute(ban);
    }
    
    public void resolveBanModerator(WorkerStateEvent e, boolean ban) throws IOException {
        if(ban==true)
        {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("BLOCK thành công !!!");
            a.show();
        }
    }
    
    
    private void FreeModeratorThread(String userId) {
        Task<Boolean> free = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return AdminDao.freeModerator(userId);
            }
        };
        free.setOnSucceeded(e -> {
            try {
                resolveFreeModerator(e, free.getValue());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        exec.execute(free);
    }
    
    public void resolveFreeModerator(WorkerStateEvent e, boolean ban) throws IOException {
        if(ban==true)
        {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Gỡ BLOCK thành công !!!");
            a.show();
        }
    }
    

    
    private void getModeratorThread(){
        Task<List<User>> dataTask = new Task<List<User>>() {
            @Override
            public List<User> call() {
                return AdminDao.getModerator();
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveGetModerator(e, dataTask.getValue());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        exec.execute(dataTask);
    }
    
    public void resolveGetModerator(WorkerStateEvent e, List<User> list) throws IOException {
        for(int i=0;i<list.size();i++)
        {
            ModeratorUsername.getItems().add(list.get(i).getUserId());
          
        }
       
    }
    
    private void showModeratorThread(){
        Task<List<ActivityHistory>> dataTask = new Task<List<ActivityHistory>>() {
            @Override
            public List<ActivityHistory> call() {
                return AdminDao.getManagerActivityHistory(ModeratorUsername.getSelectionModel().getSelectedItem().toString());
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveShowModerator(e, dataTask.getValue());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        exec.execute(dataTask);
    }
    
    public void resolveShowModerator(WorkerStateEvent e, List<ActivityHistory> list) throws IOException {
        ManagePane.setVisible(true);
        
        if(AdminDao.checkBanlist(ModeratorUsername.getSelectionModel().getSelectedItem().toString())==false){
            activeChoice.setSelected(true);
        }
        else
            inactiveChoice.setSelected(true);
        
        listAct = FXCollections.observableArrayList(list);

        time.setCellValueFactory(new PropertyValueFactory<ActivityHistory, String>("logTime"));
        logMsg.setCellValueFactory(new PropertyValueFactory<ActivityHistory, String>("logMsg"));
  
        activitiesTable.setItems(listAct);
       
    }
    

    
}


