/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.database.PaymentDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.CartItem;
import com.seasidechachacha.client.models.Invoice;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Admin
 */
public class ViewCartItemController {

    @FXML
    private TableView<CartItem> CartItems;

    @FXML
    private TableColumn<CartItem, String> packageName;

    @FXML
    private TableColumn<CartItem, String> packageQuantity;

    @FXML
    private TableColumn<CartItem, String> packagePrice;

    @FXML
    private TableColumn<CartItem, String> totalPrice;

    @FXML
    private Button orderButton, cancelButton;

    @FXML
    private Label totalCost;

    private ObservableList<CartItem> listCart;

    private Double totalCostOfUnpaidOrder;

    @FXML
    private void initialize() {

        getCartItemsThread();

        orderButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("X??c nh???n");
            alert.setHeaderText("H??y x??c nh???n ?????t h??ng");
            alert.setContentText("V???i t???ng gi?? : " + totalCost.getText());

            // option != null.
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == null) {

            } else if (option.get() == ButtonType.OK) { // ?????ng ?? ?????t h??ng
                getPendingPaymentTotalPriceThread(Session.getUserId());
            } else if (option.get() == ButtonType.CANCEL) { // kh??ng c?? g?? x???y ra

            }

        });

        cancelButton.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("X??c nh???n");
            alert.setHeaderText("H??y x??c nh???n x??a ????n ?????t h??ng");

            // option != null.
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == null) {

            } else if (option.get() == ButtonType.OK) { // ?????ng ?? x??a
                clearCartThread();
            } else if (option.get() == ButtonType.CANCEL) { // kh??ng c?? g?? x???y ra

            }
        });
    }

    private void getPendingPaymentTotalPriceThread(String userId) {
        Task<Double> getPendingPaymentTotalPriceTask = new Task<Double>() {
            @Override
            public Double call() throws SQLException {
                return PaymentDao.getPendingPaymentTotalPrice(userId);
            }
        };

        getPendingPaymentTotalPriceTask.setOnSucceeded((WorkerStateEvent e) -> {
            totalCostOfUnpaidOrder = getPendingPaymentTotalPriceTask.getValue();
            if (totalCostOfUnpaidOrder >= 1000000) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Th??ng b??o");
                alert.setHeaderText("C??c ????n h??ng tr?????c ch??a thanh to??n c???a b???n l???n h??n 1 tri???u!!!\nH??y thanh to??n tr?????c khi ?????t ????n h??ng m???i.");
                alert.show();
            } else {
                logCartThread();
            }
        });
        getPendingPaymentTotalPriceTask.setOnFailed(e -> {

        });
        TaskExecutor.execute(getPendingPaymentTotalPriceTask);
    }

    private void getCartItemsThread() {
        Task<List<CartItem>> cart = new Task<List<CartItem>>() {
            @Override
            public List<CartItem> call() throws SQLException {
                return PaymentDao.getCart(Session.getUser().getUserId());
            }
        };
        cart.setOnSucceeded(e -> {
            resolveGetCartItem(e, cart.getValue());
        });
        TaskExecutor.execute(cart);
    }

    public void resolveGetCartItem(WorkerStateEvent e, List<CartItem> list) {
        listCart = FXCollections.observableArrayList(list);

        float sum = 0;

        for (int i = 0; i < list.size(); i++) {
            sum += Integer.parseInt(list.get(i).getTotalPrice().substring(0, list.get(i).getTotalPrice().length() - 2));
        }

        packageName.setCellValueFactory(new PropertyValueFactory<CartItem, String>("name"));
        packageQuantity.setCellValueFactory(new PropertyValueFactory<CartItem, String>("quantity"));
        packagePrice.setCellValueFactory(new PropertyValueFactory<CartItem, String>("price"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<CartItem, String>("totalPrice"));

        totalCost.setText(Float.toString(sum) + " VND");
        CartItems.setItems(listCart);
    }

    private void logCartThread() {
        Task<Long> cart = new Task<Long>() {
            @Override
            public Long call() throws SQLException {
                return PaymentDao.logCart(Session.getUser().getUserId());
            }
        };
        cart.setOnSucceeded(e -> {
            resolveLogCart(e, cart.getValue());
        });
        TaskExecutor.execute(cart);
    }

    public void resolveLogCart(WorkerStateEvent e, Long list) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Th??ng b??o");
        alert.setHeaderText("?????t h??ng th??nh c??ng!!!");
        alert.show();
        PaymentDao.clearCart(Session.getUser().getUserId());
        getCartItemsThread();

    }

    private void clearCartThread() {
        Task<Boolean> clearCart = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return PaymentDao.clearCart(Session.getUser().getUserId());
            }
        };
        clearCart.setOnSucceeded(e -> {
            resolveClearCart(e, clearCart.getValue());
        });
        TaskExecutor.execute(clearCart);
    }

    public void resolveClearCart(WorkerStateEvent e, Boolean list) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Th??ng b??o");
        alert.setHeaderText("X??a ????n h??ng th??nh c??ng!!!");
        alert.show();
        getCartItemsThread();

    }
}
