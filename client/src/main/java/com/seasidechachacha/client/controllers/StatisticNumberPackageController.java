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
import com.seasidechachacha.client.models.PackageStatistic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
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
    private TableColumn<PackageStatistic, String> stt;

    @FXML
    private ObservableList<PackageStatistic> statisticList;

    @FXML
    private PieChart piechart;

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

        statisticType.getItems().addAll("s??? l?????ng ng?????i ??? t???ng tr???ng th??i theo th???i gian",
                "s??? l?????ng nhu y???u ph???m ???????c ti??u th???", "s??? chuy???n tr???ng th??i", "s??? d?? n???");
        statisticType.setValue("s??? l?????ng nhu y???u ph???m ???????c ti??u th???");

        nextButton.setOnAction(e -> {
            if (statisticType.getSelectionModel().getSelectedItem().toString()
                    .equals("s??? l?????ng ng?????i ??? t???ng tr???ng th??i theo th???i gian")) {
                try {
                    App.setCurrentPane("pn_all", "view/StatisticNumberStatus", null);
                } catch (IOException ex) {
                    Logger.getLogger(StatisticNumberStatusController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pn_all.toFront();
            } else if (statisticType.getSelectionModel().getSelectedItem().toString()
                    .equals("s??? l?????ng nhu y???u ph???m ???????c ti??u th???")) {
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

    private void getStatisticThread(int type) {
        Task<List<PackageStatistic>> dataTask = new Task<List<PackageStatistic>>() {
            @Override
            public List<PackageStatistic> call() {
                if (type == 1) {
                    return ManagerDao.getStatisticPackageAll();
                } else if (type == 2) {
                    return ManagerDao.getStatisticPackagebyDay(dateInput.getValue().toString());
                } else {
                    return ManagerDao.getStatisticPackagebyMonth(String.valueOf(monthInput.getValue().getMonthValue()));
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

        ObservableList<PieChart.Data> piechartData = FXCollections.observableArrayList();

        for (int i = 0; i < list.size(); i++) {
            int x = i + 1;
            piechartData.add(new PieChart.Data(x + "", Integer.parseInt(list.get(i).getQuantity())));
        }

        piechart.setData(piechartData);
        statisticList = FXCollections.observableArrayList(list);

        stt.setCellValueFactory(new PropertyValueFactory<PackageStatistic, String>("id"));
        packageName.setCellValueFactory(new PropertyValueFactory<PackageStatistic, String>("name"));
        Quantity.setCellValueFactory(new PropertyValueFactory<PackageStatistic, String>("quantity"));

        packageTable.setItems(statisticList);
    }

}
