package com.seasidechachacha.client.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.seasidechachacha.client.database.PaymentDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.models.Package;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class BuyPackageController {

    private List<Package> listP;

    @FXML
    private TextField choosePackage;
    @FXML
    private Label totalCost;

    @FXML
    private Button searchButton, acceptButton;

    @FXML
    private TableView<Package> packageTable;

    @FXML
    private TableColumn<Package, String> numberCol, nameCol, limitCol, dayCol, priceCol;

    private ObservableList<Package> listPackage;

    @FXML
    private TextField searchText, quantity;

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

        getPackageThread();

        quantity.setOnKeyTyped(e -> {
            if (quantity.getText() != "" && quantity.getText().matches("\\d+")) {
                if (Integer.parseInt(quantity.getText()) >= 1 && Integer.parseInt(quantity.getText()) <= 1000) {

                    double price = 0;
                    for (int i = 0; i < listP.size(); i++) {
                        if (listP.get(i).getName()
                                .equalsIgnoreCase(choosePackage.getText())) {
                            price = listP.get(i).getPrice();
                        }
                    }
                    int q = Integer.parseInt(quantity.getText());
                    double total = q * price;

                    totalCost.setText(Double.toString(total) + " VND");
                }
            }

        });

        acceptButton.setOnAction(e -> {
            if (!choosePackage.getText().equalsIgnoreCase("")) {
                for (int i = 0; i < listP.size(); i++) {
                    if (listP.get(i).getName()
                            .equalsIgnoreCase(choosePackage.getText())) {

                        if (quantity.getText() != "" && quantity.getText().matches("\\d+")) {
                            if (Integer.parseInt(quantity.getText()) > listP.get(i).getLimitPerPerson()) {
                                Alert a = new Alert(Alert.AlertType.WARNING);
                                a.setContentText("Số lượng mua lớn hơn giới hạn cho phép, xin hãy nhập lại !!!");
                                a.show();
                            } else {
                                String userid = Session.getUser().getUserId();
                                int packageid = listP.get(i).getPackageID();
                                int limitperPerson = listP.get(i).getLimitPerPerson();
                                int daycooldown = listP.get(i).getDayCooldown();

                                if (limitperPerson <= PaymentDao.quantityOfBoughtPackage(userid, packageid, daycooldown)) {
                                    Alert a = new Alert(Alert.AlertType.WARNING);
                                    a.setContentText("Bạn đã mua sản phẩm này vượt quá giới hạn cho phép trong vòng " + Integer.toString(daycooldown) + " ngày!!!\nHãy đặt lại sau vài ngày nữa");
                                    a.show();
                                } else {
                                    addCartThread(packageid);
                                }
                            }
                        } else {
                            Alert a = new Alert(Alert.AlertType.WARNING);
                            a.setContentText("Xin kiểm tra lại số lượng nhập !!!");
                            a.show();
                        }
                    }
                }
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Hãy chọn gói cần mua!!!");
                a.show();
            }
        });
        packageTable.setOnMouseClicked(e -> {

            choosePackage.setText(packageTable.getSelectionModel().getSelectedItem().getName());

        });
    }

    private void getPackageThread() {
        Task<List<Package>> listPackage = new Task<List<Package>>() {
            @Override
            public List<Package> call() throws SQLException {
                return ManagerDao.getPackageList();
            }
        };
        listPackage.setOnSucceeded(e -> {
            resolveGetListPackage(e, listPackage.getValue());
        });
        exec.execute(listPackage);
    }

    public void resolveGetListPackage(WorkerStateEvent e, List<Package> list) {
        listP = list;

        listPackage = FXCollections.observableArrayList(list);

        numberCol.setCellValueFactory(new PropertyValueFactory<Package, String>("packageID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Package, String>("name"));
        limitCol.setCellValueFactory(new PropertyValueFactory<Package, String>("limitPerPerson"));
        dayCol.setCellValueFactory(new PropertyValueFactory<Package, String>("dayCooldown"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Package, String>("price"));

        packageTable.setItems(listPackage);
    }

    private void addCartThread(int packageId) {
        Task<Boolean> flag = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                double price = 0;
                for (int i = 0; i < listP.size(); i++) {
                    if (listP.get(i).getName()
                            .equalsIgnoreCase(choosePackage.getText())) {
                        price = listP.get(i).getPrice();
                    }
                }
                int quantityNum = Integer.parseInt(quantity.getText());
                String totalCostString = totalCost.getText().substring(0, totalCost.getText().length() - 6);
                double totalCostNum = Double.parseDouble(totalCostString);
                return PaymentDao.addToCart(Session.getUser().getUserId(), packageId, quantityNum, price);
            }
        };
        flag.setOnSucceeded(e -> {
            resolveAddtoCart(e, flag.getValue());
        });
        exec.execute(flag);
    }

    public void resolveAddtoCart(WorkerStateEvent e, boolean flag) {
        if (flag == true) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Thêm vào giỏ hàng thành công !!!");
            a.show();
        }
    }
}
