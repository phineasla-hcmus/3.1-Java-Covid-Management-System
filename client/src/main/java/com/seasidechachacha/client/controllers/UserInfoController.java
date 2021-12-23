package com.seasidechachacha.client.controllers;

import static com.seasidechachacha.client.database.ManagedUserDao.get;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentState;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentTreatmentPlace;
import static com.seasidechachacha.client.database.ManagerDao.getDebt;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.database.InvoiceDao;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.ManagedUserHistory;
import com.seasidechachacha.client.models.Invoice;
import com.seasidechachacha.client.models.PaymentHistory;
import com.seasidechachacha.client.models.TreatmentPlace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class UserInfoController {

    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);
    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace,
            labelDebt;

    private Executor exec;

    private String userId = "079157952250";

    @FXML
    private Pagination paginationManaged, paginationConsumption, paginationPayment;

    private List<ManagedUserHistory> dataManaged;
    private List<Invoice> dataOrder;
    private List<PaymentHistory> dataPayment;

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
        getManagedHistoryThread(userId);

        ManagedUser user = get(userId);
        labelFullName.setText(user.getName());
        labelIdentityCard.setText(user.getUserId());
        labelBirthYear.setText(String.valueOf(user.getBirthYear()));
        labelAddress.setText(user.getAddress());
        String currentStatus = "F" + getCurrentState(user.getUserId());
        labelStatus.setText(currentStatus);
        TreatmentPlace treat = getCurrentTreatmentPlace(user.getUserId());
        String currentPlace = treat.getName();
        if (treat != null) {
            labelTreatmentPlace.setText(treat.getName());
        }
        labelDebt.setText(String.valueOf(getDebt(userId)));
    }

    private void getManagedHistoryThread(String userID) {
        Task<List<ManagedUserHistory>> dataTask = new Task<List<ManagedUserHistory>>() {
            @Override
            public List<ManagedUserHistory> call() {
                return ManagerDao.getManagedUserHistory(userID);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveManagedHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    public void resolveManagedHistory(WorkerStateEvent e, List<ManagedUserHistory> list) throws IOException {
        dataManaged = list;
        if (dataManaged.size() % rowsPerPage() == 0) {
            paginationManaged.setPageCount(dataManaged.size() / rowsPerPage());

        } else {
            paginationManaged.setPageCount(dataManaged.size() / rowsPerPage() + 1);

        }
        paginationManaged.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > dataManaged.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createManagedPage(pageIndex);
                }
            }
        });
        getOrderHistoryThread(userId);
    }

    private void getOrderHistoryThread(String userID) {
        Task<List<Invoice>> dataTask = new Task<List<Invoice>>() {
            @Override
            public List<Invoice> call() {
                return InvoiceDao.getList(userID, 100, 0);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveOrderHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    public void resolveOrderHistory(WorkerStateEvent e, List<Invoice> list) throws IOException {
        dataOrder = list;
        if (dataOrder.size() % rowsPerPage() == 0) {
            paginationConsumption.setPageCount(dataOrder.size() / rowsPerPage());

        } else {
            paginationConsumption.setPageCount(dataOrder.size() / rowsPerPage() + 1);

        }
        paginationConsumption.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > dataOrder.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createOrderPage(pageIndex);
                }
            }
        });

        getPaymentHistoryThread(userId);
    }

    private void getPaymentHistoryThread(String userID) {
        Task<List<PaymentHistory>> dataTask = new Task<List<PaymentHistory>>() {
            @Override
            public List<PaymentHistory> call() {
                return ManagerDao.getPaymentHistoryList(userID);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolvePaymentHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    public void resolvePaymentHistory(WorkerStateEvent e, List<PaymentHistory> list) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        dataPayment = list;
        if (dataPayment.size() % rowsPerPage() == 0) {
            paginationPayment.setPageCount(dataPayment.size() / rowsPerPage());

        } else {
            paginationPayment.setPageCount(dataPayment.size() / rowsPerPage() + 1);

        }
        paginationPayment.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > dataPayment.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createPaymentPage(pageIndex);
                }
            }
        });
    }

    public VBox createPaymentPage(int pageIndex) {
        int lastIndex = 0;
        int displace = dataPayment.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = dataPayment.size() / rowsPerPage();
        } else {
            lastIndex = dataPayment.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<PaymentHistory> table = new TableView<PaymentHistory>();
            TableColumn paymentCol = new TableColumn("Ngày thanh toán");
            paymentCol.setCellValueFactory(
                    new PropertyValueFactory<PaymentHistory, String>("paymentTime"));

            paymentCol.setMinWidth(100);

            TableColumn moneyCol = new TableColumn("Tổng tiền");
            moneyCol.setCellValueFactory(
                    new PropertyValueFactory<PaymentHistory, Float>("totalMoney"));

            moneyCol.setMinWidth(160);

            table.getColumns().addAll(paymentCol, moneyCol);
            table.setItems(FXCollections.observableArrayList(dataPayment));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(
                        dataPayment.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        dataPayment.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    public VBox createOrderPage(int pageIndex) {
        int lastIndex = 0;
        int displace = dataOrder.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = dataOrder.size() / rowsPerPage();
        } else {
            lastIndex = dataOrder.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<Invoice> table = new TableView<Invoice>();
            TableColumn orderCol = new TableColumn("Mã đơn hàng");
            orderCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, Integer>("orderID"));

            orderCol.setMinWidth(100);

            TableColumn dateCol = new TableColumn("Ngày mua");
            dateCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, String>("timeOrder"));

            dateCol.setMinWidth(160);

            TableColumn totalCol = new TableColumn("Tổng số lượng mua");
            totalCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, Integer>("totalItems"));

            totalCol.setMinWidth(300);

            TableColumn moneyCol = new TableColumn("Tổng tiền");
            moneyCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, Float>("totalOrderMoney"));

            moneyCol.setMinWidth(300);

            // TableColumn actionCol = new TableColumn("");
            // actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            // Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>
            // cellFactory
            // = //
            // new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser,
            // String>>() {
            // @Override
            // public TableCell call(final TableColumn<ManagedUser, String> param) {
            // final TableCell<Object, String> cell = new TableCell<Object, String>() {
            // final Button btn = new Button("Xem chi tiết");
            //
            // @Override
            // public void updateItem(String item, boolean empty) {
            // super.updateItem(item, empty);
            // if (empty) {
            // setGraphic(null);
            // setText(null);
            // } else {
            // btn.setOnAction(event -> {
            // try {
            // App.setCurrentPane("pn_all", "view/ViewPersonalInfo", getTableRow());
            // } catch (IOException ex) {
            // logger.fatal(ex);
            // }
            // });
            // setGraphic(btn);
            // setText(null);
            // }
            // }
            // };
            // cell.setAlignment(Pos.CENTER);
            // return cell;
            // }
            // };
            //
            // actionCol.setCellFactory(cellFactory);
            table.getColumns().addAll(orderCol, dateCol, totalCol, moneyCol);
            table.setItems(FXCollections.observableArrayList(dataOrder));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(
                        dataOrder.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        dataOrder.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    public VBox createManagedPage(int pageIndex) {
        int lastIndex = 0;
        int displace = dataManaged.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = dataManaged.size() / rowsPerPage();
        } else {
            lastIndex = dataManaged.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<ManagedUserHistory> table = new TableView<ManagedUserHistory>();
            TableColumn dateCol = new TableColumn("Ngày");
            dateCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUserHistory, String>("date"));

            dateCol.setMinWidth(100);

            TableColumn stateCol = new TableColumn("Trạng thái");
            stateCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUserHistory, Integer>("state"));

            stateCol.setMinWidth(160);

            TableColumn placeCol = new TableColumn("Địa điểm điều trị/cách ly");
            placeCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUserHistory, String>("treatmentPlaceName"));

            placeCol.setMinWidth(300);

            table.getColumns().addAll(dateCol, stateCol, placeCol);
            table.setItems(FXCollections.observableArrayList(dataManaged));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(
                        dataManaged.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        dataManaged.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 10;
    }
}
