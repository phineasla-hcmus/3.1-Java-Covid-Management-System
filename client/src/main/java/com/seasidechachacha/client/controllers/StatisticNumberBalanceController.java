/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.BalanceStatistic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Admin
 */
public class StatisticNumberBalanceController {
    @FXML
    private ScrollPane pn_all;

    @FXML
    private ChoiceBox statisticType,yearBox;

    @FXML
    private Button nextButton,allButton,viewButton;

    @FXML
    private TableView<BalanceStatistic> balanceTable;
    
    @FXML
    private TableColumn<BalanceStatistic,String> time;
    
    @FXML
    private TableColumn<BalanceStatistic,String> balance;
    
    @FXML
    private ObservableList<BalanceStatistic> statisticList;
    
    @FXML
    private BarChart<?,?> barChart;
    
    @FXML
    private CategoryAxis x;
    
    @FXML
    private NumberAxis y; 
    
    @FXML
    private XYChart.Series ser ;
    
    
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
        int defaultYear=2017;
        for (int i=0;i<5;i++)
        {
            defaultYear = defaultYear + 1;
            yearBox.getItems().add(defaultYear+"");
            
        }
        
        statisticType.getItems().addAll("số lượng người ở từng trạng thái theo thời gian", "số lượng nhu yếu phẩm được tiêu thụ", "số chuyển trạng thái", "số dư nợ");
        statisticType.setValue("số dư nợ");
        
        
        nextButton.setOnAction(e -> {
            if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng người ở từng trạng thái theo thời gian")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberStatus", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số lượng nhu yếu phẩm được tiêu thụ")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberPackage", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số chuyển trạng thái")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberChangeStatus", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString().equals("số dư nợ")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberBalance", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            }
        });

        allButton.setOnAction(e -> {
            getStatisticThread();
        });
        
    }
    
    private void getStatisticThread(){
        Task<List<BalanceStatistic>> dataTask = new Task<List<BalanceStatistic>>() {
            @Override
            public List<BalanceStatistic> call() {
             
                return ManagerDao.getBalanceStatisticbyYear(yearBox.getSelectionModel().getSelectedItem().toString());
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
    
    public void resolveStatistic(WorkerStateEvent e, List<BalanceStatistic> list) throws IOException {
        
        ser = new XYChart.Series<>();
        ser.setName(yearBox.getSelectionModel().getSelectedItem().toString());
         
       
        for(int i =0;i<list.size();i++)
        {
            ser.getData().add(new XYChart.Data(list.get(i).getMonth(),Integer.parseInt(list.get(i).getTotal().substring(0, list.get(i).getTotal().length()-4))));
        }
    

        barChart.getData().addAll(ser);
 
        statisticList = FXCollections.observableArrayList(list);

        time.setCellValueFactory(new PropertyValueFactory<BalanceStatistic, String>("month"));
        balance.setCellValueFactory(new PropertyValueFactory<BalanceStatistic, String>("total"));
     
        balanceTable.setItems(statisticList);
    }
    
}
