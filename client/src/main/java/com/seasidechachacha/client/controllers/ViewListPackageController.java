package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.models.Package;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ViewListPackageController {

    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);

    private static List<Package> data;

    @FXML
    private Button btnAdd, btnSearch;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox cbSort;

    @FXML
    private Pagination pagination;

    // for testing purpose
    private ManagerDao Tam;

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
        getListPackageThread();
        Tam = new ManagerDao("mod-19127268");

        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewPackage", null);
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        btnSearch.setOnAction(event -> {
            String keyword = tfSearch.getText();
        });
        cbSort.getItems().addAll("Tên gói", "Mức giới hạn", "Thời gian giới hạn", "Đơn giá");
        cbSort.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue.equals("Tên gói")) {
                getSortedListPackageThread("name");
            } else if (newValue.equals("Mức giới hạn")) {
                getSortedListPackageThread("limit");
            } else if (newValue.equals("Thời gian giới hạn")) {
                getSortedListPackageThread("time");
            } else if (newValue.equals("Đơn giá")) {
                getSortedListPackageThread("price");
            }
        });

    }

    private void getSortedListPackageThread(String label) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                if (label.equals("name")) {
                    return ManagerDao.getPackageListByName();
                } else if (label.equals("limit")) {
                    return ManagerDao.getPackageListByLimit();
                } else if (label.equals("time")) {
                    return ManagerDao.getPackageListByTime();
                } else if (label.equals("price")) {
                    return ManagerDao.getPackageListByPrice();
                }
                return null;

            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    private void getListPackageThread() {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                return ManagerDao.getPackageList();
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        exec.execute(dataTask);
    }

    public void resolveListPackage(WorkerStateEvent e, List<Package> list) throws IOException {
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
        return 10;
    }

    public VBox createPage(int pageIndex) {
        int lastIndex = 0;
        int displace = data.size() % rowsPerPage();
        lastIndex = data.size() / rowsPerPage();

//        if (displace > 0) {
//            lastIndex = data.size() / rowsPerPage();
//        } 
//        else {
//            lastIndex = data.size() / rowsPerPage() - 1;
//
//        }
        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<Package> table = new TableView<Package>();
            TableColumn numCol = new TableColumn("ID");
            numCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("packageID"));

            numCol.setMinWidth(20);

            TableColumn nameCol = new TableColumn("Tên gói");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("name"));

            nameCol.setMinWidth(340);

            TableColumn limitCol = new TableColumn("Mức giới hạn");
            limitCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("limitPerPerson"));

            limitCol.setMinWidth(80);

            TableColumn dayCol = new TableColumn("Thời gian giới hạn (ngày)");
            dayCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("dayCooldown"));

            dayCol.setMinWidth(80);

            TableColumn priceCol = new TableColumn("Đơn giá");
            priceCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("price"));

            TableColumn editCol = new TableColumn();
            editCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory
                    = //
                    new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
                @Override
                public TableCell call(final TableColumn<Package, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {
                        final Button btn = new Button("Sửa");

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btn.setOnAction(event -> {
                                    try {
                                        App.setCurrentPane("pn_all", "view/ViewPackageInfo", getTableRow());
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

            editCol.setCellFactory(cellFactory);
//            editCol.setMinWidth(160);

            TableColumn deleteCol = new TableColumn();
            deleteCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory1
                    = //
                    new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
                @Override
                public TableCell call(final TableColumn<Package, String> param) {
                    final TableCell<Package, String> cell = new TableCell<Package, String>() {
                        final Button btn = new Button("Xoá");

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btn.setOnAction(event -> {
                                    Package pack = getTableRow().getItem();
                                    int packID = pack.getPackageID();
                                    if (Tam.deletePackage(packID)) {

                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Thông báo");
                                        alert.setHeaderText("Quản lý nhu yếu phẩm");
                                        alert.setContentText("Xoá nhu yếu phẩm thành công!");

                                        alert.showAndWait();

                                        data.remove(pack);
//                                        table.refresh();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Thông báo");
                                        alert.setHeaderText("Quản lý nhu yếu phẩm");
                                        alert.setContentText("Không thể xoá nhu yếu phẩm này!");

                                        alert.showAndWait();
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

            deleteCol.setCellFactory(cellFactory1);
//            deleteCol.setMinWidth(160);

            table.getColumns().addAll(numCol, nameCol, limitCol, dayCol, priceCol, editCol, deleteCol);
            table.setItems(FXCollections.observableArrayList(data));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }
}
