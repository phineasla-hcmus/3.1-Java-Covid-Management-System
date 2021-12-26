/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.database.InvoiceDao;
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

    @FXML
    private void initialize() {

        getCartItemsThread();

        orderButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText("Hãy xác nhận đặt hàng");
            alert.setContentText("Với tổng giá : " + totalCost.getText());

            // option != null.
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == null) {

            } else if (option.get() == ButtonType.OK) { // đồng ý đặt hàng
                logCartThread();
            } else if (option.get() == ButtonType.CANCEL) { // không có gì xảy ra

            }

        });

        cancelButton.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText("Hãy xác nhận xóa đơn đặt hàng");

            // option != null.
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == null) {

            } else if (option.get() == ButtonType.OK) { // đồng ý xóa
                clearCartThread();
            } else if (option.get() == ButtonType.CANCEL) { // không có gì xảy ra

            }
        });
    }

    private void getCartItemsThread() {
        Task<List<CartItem>> cart = new Task<List<CartItem>>() {
            @Override
            public List<CartItem> call() throws SQLException {
                return InvoiceDao.viewCart(Session.getUser().getUserId());
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
            sum += Integer.parseInt(list.get(i).getTotalPrice().substring(0, list.get(i).getTotalPrice().length() - 4));
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
                return InvoiceDao.logCart(Session.getUser().getUserId());
            }
        };
        cart.setOnSucceeded(e -> {
            resolveLogCart(e, cart.getValue());
        });
        TaskExecutor.execute(cart);
    }

    public void resolveLogCart(WorkerStateEvent e, Long list) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Đặt hàng thành công!!!");
        alert.show();
        InvoiceDao.clearCart(Session.getUser().getUserId());
        getCartItemsThread();

    }

    private void clearCartThread() {
        Task<Boolean> cart = new Task<Boolean>() {
            @Override
            public Boolean call() throws SQLException {
                return InvoiceDao.clearCart(Session.getUser().getUserId());
            }
        };
        cart.setOnSucceeded(e -> {
            resolveClearCart(e, cart.getValue());
        });
        TaskExecutor.execute(cart);
    }

    public void resolveClearCart(WorkerStateEvent e, Boolean list) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Xóa đơn hàng thành công!!!");
        alert.show();
        getCartItemsThread();

    }
}
