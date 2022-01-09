package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagedUserDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.utils.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ViewListUserController {

    private static final Logger logger = LogManager.getLogger(ViewListUserController.class);
    private static List<ManagedUser> data = new ArrayList<ManagedUser>();

    @FXML
    private Button btnAdd, btnSearch;
    
    @FXML
    private Label labelEmpty;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox cbSort;

    @FXML
    private Pagination pagination;

    private String keyword = "";

    @FXML
    private void initialize() {
        getListManagedUserThread();
        
        if (data == null || data.isEmpty()) {
            pagination.setVisible(false);
        }
        else {
            labelEmpty.setVisible(false);
        }

        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewUser", null);
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        btnSearch.setOnAction(event -> {
            keyword = tfSearch.getText();
            if (!keyword.equals("")) {
                getSearchResult(keyword);
            }
        });
        cbSort.getItems().addAll("ID", "Họ tên", "Năm sinh", "Trạng thái");
        cbSort.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue.equals("ID")) {
                getSortedListManagedUserThread("idCard", keyword);
            } else if (newValue.equals("Họ tên")) {
                getSortedListManagedUserThread("fullName", keyword);
            } else if (newValue.equals("Năm sinh")) {
                getSortedListManagedUserThread("yob", keyword);
            } else if (newValue.equals("Trạng thái")) {
                getSortedListManagedUserThread("state", keyword);
            }
        });
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                keyword = "";
                getListManagedUserThread();
            }
        });
    }

    private void getSearchResult(String keyword) {
        Task<List<ManagedUser>> dataTask = new Task<List<ManagedUser>>() {
            @Override
            public List<ManagedUser> call() {
                return ManagerDao.getManagedUserByFullName(keyword);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListManagedUser(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getSortedListManagedUserThread(String label, String keyword) {
        Task<List<ManagedUser>> dataTask = new Task<List<ManagedUser>>() {
            @Override
            public List<ManagedUser> call() {
                if (label.equals("idCard")) {
                    return ManagerDao.getSortedListByID(keyword);
                } else if (label.equals("fullName")) {
                    return ManagerDao.getSortedListByName(keyword);
                } else if (label.equals("yob")) {
                    return ManagerDao.getSortedListByBirthYear(keyword);
                } else if (label.equals("state")) {
                    return ManagerDao.getSortedListByState(keyword);
                }
                return null;

            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListManagedUser(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getListManagedUserThread() {
        Task<List<ManagedUser>> dataTask = new Task<List<ManagedUser>>() {
            @Override
            public List<ManagedUser> call() {
                return ManagedUserDao.getList(100, 0);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListManagedUser(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveListManagedUser(WorkerStateEvent e, List<ManagedUser> list) throws IOException {
        if (list == null || list.isEmpty()) {
//            Alert.showAlert(AlertType.WARNING, "Quản lý người liên quan Covid19", "Không tìm thấy người dùng phù hợp!");
            return;
        }
        data = list;
        if (data.size() % rowsPerPage() == 0) {
            pagination.setPageCount(data.size() / rowsPerPage());

        } else {
            pagination.setPageCount(data.size() / rowsPerPage() + 1);

        }
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > data.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });
    }

    public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 5;
    }

    public VBox createPage(int pageIndex) {
        int lastIndex = 0;
        int displace = data.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = data.size() / rowsPerPage();
        } else {
            lastIndex = data.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<ManagedUser> table = new TableView<ManagedUser>();
            TableColumn numCol = new TableColumn("ID");
            numCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUser, String>("userId"));

            numCol.setMinWidth(20);

            TableColumn nameCol = new TableColumn("Họ tên");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUser, String>("name"));

            nameCol.setMinWidth(160);

            TableColumn birthYearCol = new TableColumn("Năm sinh");
            birthYearCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUser, String>("birthYear"));

            birthYearCol.setMinWidth(80);

            TableColumn addrCol = new TableColumn("Địa chỉ");
            addrCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUser, String>("address"));

            addrCol.setMinWidth(240);

            TableColumn statusCol = new TableColumn("Trạng thái");
            Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>> cellFactory1
                    = //
                    new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>() {
                @Override
                public TableCell call(final TableColumn<ManagedUser, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                if (getTableRow() != null) {
                                    ManagedUser user = (ManagedUser) getTableRow().getItem();
                                    setText("F" + String.valueOf(ManagerDao.getCurrentState(user.getUserId())));
                                }

                            }
                        }
                    };
                    cell.setAlignment(Pos.CENTER);
                    return cell;
                }
            };
            statusCol.setCellFactory(cellFactory1);

            TableColumn actionCol = new TableColumn("");
            actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>> cellFactory
                    = //
                    new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>() {
                @Override
                public TableCell call(final TableColumn<ManagedUser, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {
                        final Button btn = new Button("Xem chi tiết");

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btn.setOnAction(event -> {
                                    try {
                                        App.setCurrentPane("pn_all", "view/ViewPersonalInfo", getTableRow());
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

            table.getColumns().addAll(numCol, nameCol, birthYearCol, addrCol, statusCol, actionCol);
            table.setItems(FXCollections.observableArrayList(data));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(
                        data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

}
