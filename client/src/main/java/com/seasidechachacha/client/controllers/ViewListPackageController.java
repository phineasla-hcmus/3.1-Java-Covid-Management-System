package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.utils.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
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
    private Button btnAdd, btnSearch, btnFilter;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox cbSort;

    @FXML
    private Pagination pagination;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

    private String keyword = "";

    @FXML
    private void initialize() {
        btnFilter.setVisible(false);
        btnFilter.setOnAction(event -> {
            showFilterDialog();
        });

        getListPackageThread();

        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewPackage", null);
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        btnSearch.setOnAction(event -> {
            keyword = tfSearch.getText();
            if (!keyword.equals("")) {
                getSearchResult(keyword);
                btnFilter.setVisible(true);
            }
        });
        cbSort.getItems().addAll("Tên gói", "Mức giới hạn", "Thời gian giới hạn", "Đơn giá");
        cbSort.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue.equals("Tên gói")) {
                getSortedListPackageThread("name", keyword);
            } else if (newValue.equals("Mức giới hạn")) {
                getSortedListPackageThread("limit", keyword);
            } else if (newValue.equals("Thời gian giới hạn")) {
                getSortedListPackageThread("time", keyword);
            } else if (newValue.equals("Đơn giá")) {
                getSortedListPackageThread("price", keyword);
            }
        });
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                keyword = "";
                getListPackageThread();
                btnFilter.setVisible(false);
            }
        });
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Lọc nhu yếu phẩm");
        dialog.setHeaderText("Chọn tiêu chí");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label labelDay = new Label("Thời gian giới hạn");
        ComboBox<String> cbDay = new ComboBox<>();
        cbDay.getItems().addAll("1 ngày - 7 ngày", "1 tuần - 4 tuần", "1 tháng - 5 tháng");
        Label labelPrice = new Label("Đơn giá");
        ComboBox<String> cbPrice = new ComboBox<>();
        cbPrice.getItems().addAll("10000 - 100000", "100000 - 500000", "500000 - 1000000");
        dialogPane.setContent(new VBox(8, labelDay, cbDay, labelPrice, cbPrice));
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (cbDay.getValue() == null && cbPrice.getValue() == null) {
                Alert.showAlert(AlertType.WARNING, "Lọc nhu yếu phẩm", "Vui lòng chọn tiêu chí!");
                return;
            }
            int minDay = 0, maxDay = 0;
            double minPrice = 0, maxPrice = 0;
            if (cbDay.getValue() != null) {
                switch (cbDay.getValue()) {
                    case "1 ngày - 7 ngày":
                        minDay = 1;
                        maxDay = 7;
                        break;
                    case "1 tuần - 4 tuần":
                        minDay = 7;
                        maxDay = 28;
                        break;
                    case "1 tháng - 5 tháng":
                        minDay = 31;
                        maxDay = 155;
                        break;
                    default:
                        break;
                }
            }
            if (cbPrice.getValue() != null) {
                switch (cbPrice.getValue()) {
                    case "10000 - 100000":
                        minPrice = 10000;
                        maxPrice = 100000;
                        break;
                    case "100000 - 500000":
                        minPrice = 100000;
                        maxPrice = 500000;
                        break;
                    case "500000 - 1000000":
                        minPrice = 500000;
                        maxPrice = 1000000;
                        break;
                    default:
                        break;
                }
            }
            if (cbDay.getValue() != null && cbPrice.getValue() != null) {
                getFilterPackageListByPriceAndDay(keyword, minDay, maxDay, minPrice, maxPrice);
            } else if (cbDay.getValue() != null) {
                getFilterPackageListByDay(keyword, minDay, maxDay);

            } else {
                getFilterPackageListByPrice(keyword, minPrice, maxPrice);
            }
        }
    }

    private void getFilterPackageListByPriceAndDay(String keyword, int minDay, int maxDay, double minPrice, double maxPrice) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                return ManagerDao.filterPackageByPriceAndDay(keyword, minDay, maxDay, minPrice, maxPrice);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getFilterPackageListByDay(String keyword, int min, int max) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                return ManagerDao.filterPackageByDayCooldown(keyword, min, max);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getFilterPackageListByPrice(String keyword, double min, double max) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                return ManagerDao.filterPackageByPrice(keyword, min, max);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getSearchResult(String keyword) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                return ManagerDao.getPackageByName(keyword);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListPackage(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    private void getSortedListPackageThread(String label, String keyword) {
        Task<List<Package>> dataTask = new Task<List<Package>>() {
            @Override
            public List<Package> call() {
                if (label.equals("name")) {
                    return ManagerDao.getSortedPackageListByName(keyword);
                } else if (label.equals("limit")) {
                    return ManagerDao.getSortedPackageListByLimit(keyword);
                } else if (label.equals("time")) {
                    return ManagerDao.getSortedPackageListByTime(keyword);
                } else if (label.equals("price")) {
                    return ManagerDao.getSortedPackageListByPrice(keyword);
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
        TaskExecutor.execute(dataTask);
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
        TaskExecutor.execute(dataTask);
    }

    public void resolveListPackage(WorkerStateEvent e, List<Package> list) throws IOException {
        if (list == null || list.isEmpty()) {
            Alert.showAlert(AlertType.WARNING, "Quản lý nhu yếu phẩm", "Không tìm thấy gói nhu yếu phẩm phù hợp!");
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
        lastIndex = data.size() / rowsPerPage();

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

            nameCol.setMinWidth(300);

            TableColumn limitCol = new TableColumn("Mức giới hạn");
            limitCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("limitPerPerson"));

            limitCol.setMinWidth(80);

            TableColumn dayCol = new TableColumn("Thời gian giới hạn (ngày)");
            dayCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("dayCooldown"));

            dayCol.setMinWidth(160);

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
                                    if (manager.deletePackage(packID)) {
                                        Alert.showAlert(AlertType.INFORMATION, "Quản lý nhu yếu phẩm", "Xoá nhu yếu phẩm thành công!");
                                        data.remove(pack);
                                    } else {
                                        Alert.showAlert(AlertType.WARNING, "Quản lý nhu yếu phẩm", "Không thể xoá nhu yếu phẩm này!");
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
