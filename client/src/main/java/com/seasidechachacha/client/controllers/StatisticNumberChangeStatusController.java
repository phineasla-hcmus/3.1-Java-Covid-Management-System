/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import static java.lang.String.valueOf;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.ChangeStateStatistic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Admin
 */
public class StatisticNumberChangeStatusController {

    @FXML
    private ScrollPane pn_all;

    @FXML
    private Label totalCured;

    @FXML
    private ChoiceBox statisticType;

    @FXML
    private Button nextButton, dateButton, monthButton, allButton;

    @FXML
    private DatePicker dateInput, monthInput;
    
    @FXML
    private TableView<ChangeStateStatistic> changeStatusTable;
    
    @FXML
    private TableColumn<ChangeStateStatistic,String> from;
    
    @FXML
    private TableColumn<ChangeStateStatistic,String> to;
    
    @FXML
    private TableColumn<ChangeStateStatistic,String> quantity;
    
    @FXML
    private ObservableList<ChangeStateStatistic> statisticList;
    
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
        
        getStatisticThread(1);
        statisticType.getItems().addAll("s??? l?????ng ng?????i ??? t???ng tr???ng th??i theo th???i gian", "s??? l?????ng nhu y???u ph???m ???????c ti??u th???", "s??? chuy???n tr???ng th??i", "s??? d?? n???");
        statisticType.setValue("s??? chuy???n tr???ng th??i");
        
        nextButton.setOnAction(e -> {
            if (statisticType.getSelectionModel().getSelectedItem().toString().equals("s??? l?????ng ng?????i ??? t???ng tr???ng th??i theo th???i gian")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberStatus", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("s??? l?????ng nhu y???u ph???m ???????c ti??u th???")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberPackage", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("s??? chuy???n tr???ng th??i")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberChangeStatus", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("s??? d?? n???")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberBalance", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            }
        });

        monthButton.setOnAction(e -> {
            getStatisticThread(2);
        });

        allButton.setOnAction(e -> {
            getStatisticThread(1);
        });
    }
    
    
    private void getStatisticThread(int type){
        Task<List<ChangeStateStatistic>> dataTask = new Task<List<ChangeStateStatistic>>() {
            @Override
            public List<ChangeStateStatistic> call() {
                if (type == 1) {
                    return ManagerDao.getChangeStateStatisticAll();
                } else {
                    return ManagerDao.getChangeStateStatisticbyMonth(valueOf(monthInput.getValue().getMonthValue()));                  
                }
              
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveStatistic(e, dataTask.getValue());
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        exec.execute(dataTask);
    }
    
    public void resolveStatistic(WorkerStateEvent e, List<ChangeStateStatistic> list) throws IOException {
        
        int sumCured=0;
        for(int i=0;i<list.size();i++)
        {
            sumCured+= Integer.parseInt(list.get(i).getQuantity());
        }
        
        totalCured.setText(sumCured+"");
        statisticList = FXCollections.observableArrayList(list);

        from.setCellValueFactory(new PropertyValueFactory<ChangeStateStatistic, String>("from"));
        to.setCellValueFactory(new PropertyValueFactory<ChangeStateStatistic, String>("to"));
        quantity.setCellValueFactory(new PropertyValueFactory<ChangeStateStatistic, String>("quantity"));
     

        changeStatusTable.setItems(statisticList);
    }

}
