package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagedUserDao.get;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentState;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentTreatmentPlace;
import static com.seasidechachacha.client.database.ManagerDao.getDebt;

import java.io.IOException;
import java.util.List;

import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.database.PaymentDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
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

    private String userId = Session.getUser().getUserId();

    @FXML
    private Pagination paginationManaged, paginationConsumption, paginationPayment;

    private List<ManagedUserHistory> dataManaged;
    private List<Invoice> dataOrder;
    private List<PaymentHistory> dataPayment;

    @FXML
    private void initialize() {
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
        TaskExecutor.execute(dataTask);
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
                return PaymentDao.getHistoryList(userID, 100, 0);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveOrderHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveOrderHistory(WorkerStateEvent e, List<Invoice> list) throws IOException {
        if (list == null || list.isEmpty()) {
            getPaymentHistoryThread(userId);
            return;
        }
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
        TaskExecutor.execute(dataTask);
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

            TableColumn idCol = new TableColumn("Mã thanh toán");
            idCol.setCellValueFactory(
                    new PropertyValueFactory<PaymentHistory, Integer>("transactionID"));

            idCol.setMinWidth(200);

            TableColumn paymentCol = new TableColumn("Ngày thanh toán");
            paymentCol.setCellValueFactory(
                    new PropertyValueFactory<PaymentHistory, String>("paymentTime"));

            paymentCol.setMinWidth(200);

            TableColumn moneyCol = new TableColumn("Tổng tiền");
            moneyCol.setCellValueFactory(
                    new PropertyValueFactory<PaymentHistory, Float>("totalMoney"));

            moneyCol.setMinWidth(300);

            table.getColumns().addAll(idCol, paymentCol, moneyCol);
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
                    new PropertyValueFactory<Invoice, Integer>("invoiceId"));

            orderCol.setMinWidth(140);

            TableColumn dateCol = new TableColumn("Ngày mua");
            dateCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, String>("timeOrder"));

            dateCol.setMinWidth(200);

            TableColumn totalCol = new TableColumn("Tổng số lượng mua");
            totalCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, Integer>("totalItems"));

            totalCol.setMinWidth(160);

            TableColumn moneyCol = new TableColumn("Tổng tiền");
            moneyCol.setCellValueFactory(
                    new PropertyValueFactory<Invoice, Float>("totalOrderMoney"));

            moneyCol.setMinWidth(120);

            TableColumn actionCol = new TableColumn("");
            actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<Invoice, String>, TableCell<Invoice, String>> cellFactory
                    = //
                    new Callback<TableColumn<Invoice, String>, TableCell<Invoice, String>>() {
                @Override
                public TableCell call(final TableColumn<Invoice, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {
                        final Button btn = new Button("Chi tiết");

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btn.setOnAction(event -> {
                                    try {
                                        App.setCurrentPane("pn_all", "view/ViewOrderDetail", getTableRow());
                                    } catch (IOException ex) {
                                        logger.fatal(ex);
                                    }
                                });
                                setGraphic(btn);
                                setText(null);
                            }
                        }
                    };
                    cell.setAlignment(Pos.CENTER);
                    return cell;
                }
            };

            actionCol.setCellFactory(cellFactory);
            table.getColumns().addAll(orderCol, dateCol, totalCol, moneyCol, actionCol);
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
            Callback<TableColumn<ManagedUserHistory, String>, TableCell<ManagedUserHistory, String>> cellFactory1
                    = //
                    new Callback<TableColumn<ManagedUserHistory, String>, TableCell<ManagedUserHistory, String>>() {
                @Override
                public TableCell call(final TableColumn<ManagedUserHistory, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                if (getTableRow() != null) {
                                    ManagedUserHistory history = (ManagedUserHistory) getTableRow().getItem();
                                    setText("F" + String.valueOf(ManagerDao.getCurrentStateByTime(userId, history.getDate())));
                                }

                            }
                        }
                    };
                    cell.setAlignment(Pos.CENTER);
                    return cell;
                }
            };
            stateCol.setCellFactory(cellFactory1);

            stateCol.setMinWidth(140);

            TableColumn placeCol = new TableColumn("Địa điểm điều trị/cách ly");
            placeCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUserHistory, String>("treatmentPlaceName"));

            placeCol.setMinWidth(460);

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
