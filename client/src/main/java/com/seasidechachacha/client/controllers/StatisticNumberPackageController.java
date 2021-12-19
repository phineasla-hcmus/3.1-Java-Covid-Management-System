/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.PackageStatistic;

import java.io.IOException;
import static java.lang.String.valueOf;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


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
    private Button nextButton, dateButton, monthButton, allButton;

    @FXML
    private DatePicker dateInput, monthInput;

    @FXML
    private TableView<PackageStatistic> packageTable;

    @FXML
    private TableColumn<PackageStatistic, String> packageName;

    @FXML
    private TableColumn<PackageStatistic, String> Quantity;

    @FXML
    private ObservableList<PackageStatistic> statisticList;
    
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
        
        statisticType.getItems().addAll("số lượng người ở từng trạng thái theo thời gian", "số lượng nhu yếu phẩm được tiêu thụ", "số chuyển trạng thái", "số dư nợ");
        statisticType.setValue("số lượng nhu yếu phẩm được tiêu thụ");
        
        
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

        dateButton.setOnAction(e -> {
            getStatisticThread(2);
        });

        monthButton.setOnAction(e -> {
            getStatisticThread(3);
        });

        allButton.setOnAction(e -> {
            getStatisticThread(1);
        });
    }
    
    private void getStatisticThread(int type){
        Task<List<PackageStatistic>> dataTask = new Task<List<PackageStatistic>>() {
            @Override
            public List<PackageStatistic> call() {
                if (type == 1) {
                    return ManagerDao.getStatisticPackageAll();
                } else if (type == 2) {
                    return ManagerDao.getStatisticPackagebyDay(dateInput.getValue().toString());
                } else {
                    return ManagerDao.getStatisticPackagebyMonth(valueOf(monthInput.getValue().getMonthValue()));
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
    
    public void resolveStatistic(WorkerStateEvent e, List<PackageStatistic> list) throws IOException {
        statisticList = FXCollections.observableArrayList(list);

        packageName.setCellValueFactory(new PropertyValueFactory<PackageStatistic, String>("name"));
        Quantity.setCellValueFactory(new PropertyValueFactory<PackageStatistic, String>("quantity"));

        packageTable.setItems(statisticList);
    }
    
}
