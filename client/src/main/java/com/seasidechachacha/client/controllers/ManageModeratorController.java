package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.models.ActivityHistory;
import com.seasidechachacha.client.models.User;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
           if(activeChoice.isSelected()==true)
           {
               
           }
           if(inactiveChoice.isSelected()==true)
           {
               
           }
        });
        
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


