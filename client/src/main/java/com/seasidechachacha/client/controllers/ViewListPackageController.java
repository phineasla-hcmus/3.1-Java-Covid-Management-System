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

    @FXML
    private Label labelEmpty;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

    private String keyword = "";

    @FXML
    private void initialize() {
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
//                btnFilter.setVisible(true);
            }
        });
        cbSort.getItems().addAll("T??n g??i", "M???c gi???i h???n", "Th???i gian gi???i h???n", "????n gi??");
        cbSort.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue.equals("T??n g??i")) {
                getSortedListPackageThread("name", keyword);
            } else if (newValue.equals("M???c gi???i h???n")) {
                getSortedListPackageThread("limit", keyword);
            } else if (newValue.equals("Th???i gian gi???i h???n")) {
                getSortedListPackageThread("time", keyword);
            } else if (newValue.equals("????n gi??")) {
                getSortedListPackageThread("price", keyword);
            }
        });
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                keyword = "";
                getListPackageThread();
            }
        });
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("L???c nhu y???u ph???m");
        dialog.setHeaderText("Ch???n ti??u ch??");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label labelDay = new Label("Th???i gian gi???i h???n");
        ComboBox<String> cbDay = new ComboBox<>();
        cbDay.getItems().addAll("1 ng??y - 7 ng??y", "1 tu???n - 4 tu???n", "1 th??ng - 5 th??ng");
        Label labelPrice = new Label("????n gi??");
        ComboBox<String> cbPrice = new ComboBox<>();
        cbPrice.getItems().addAll("10000 - 100000", "100000 - 500000", "500000 - 1000000");
        dialogPane.setContent(new VBox(8, labelDay, cbDay, labelPrice, cbPrice));
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (cbDay.getValue() == null && cbPrice.getValue() == null) {
                Alert.showAlert(AlertType.WARNING, "L???c nhu y???u ph???m", "Vui l??ng ch???n ti??u ch??!");
                return;
            }
            int minDay = 0, maxDay = 0;
            double minPrice = 0, maxPrice = 0;
            if (cbDay.getValue() != null) {
                switch (cbDay.getValue()) {
                    case "1 ng??y - 7 ng??y":
                        minDay = 1;
                        maxDay = 7;
                        break;
                    case "1 tu???n - 1 th??ng":
                        minDay = 7;
                        maxDay = 30;
                        break;
                    case "1 th??ng - 5 th??ng":
                        minDay = 30;
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
//            Alert.showAlert(AlertType.WARNING, "Qu???n l?? nhu y???u ph???m", "Kh??ng t??m th???y g??i nhu y???u ph???m ph?? h???p!");
            labelEmpty.setVisible(true);
            pagination.setVisible(false);
            return;
        }
        data = list;
        labelEmpty.setVisible(false);
        pagination.setVisible(true);
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

    public String parseDayCooldown(int day) {
        String result = "";
        if (day < 7) {
            result += day + " ng??y";
        } else if (day >= 7 && day < 30) {
            result += (day / 7) + " tu???n ";
            if (day % 7 > 0) {
                result += (day % 7) + " ng??y";
            }
        } else {
            result += (day / 30) + " th??ng ";
            day = day % 30;
            if (day >= 7 && day < 30) {
                result += (day / 7) + " tu???n ";
                if (day % 7 > 0) {
                    result += (day % 7) + " ng??y";
                }
            }
        }
        return result;
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

            TableColumn nameCol = new TableColumn("T??n g??i");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("name"));

            nameCol.setMinWidth(300);

            TableColumn limitCol = new TableColumn("M???c gi???i h???n");
            limitCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("limitPerPerson"));

            limitCol.setMinWidth(80);

            TableColumn dayCol = new TableColumn("Th???i gian gi???i h???n");

            dayCol.setMinWidth(160);

            Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory1
                    = //
                    new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
                @Override
                public TableCell call(final TableColumn<Package, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                if (getTableRow() != null) {
                                    Package pack = (Package) getTableRow().getItem();
                                    setText(parseDayCooldown(pack.getDayCooldown()));
                                }

                            }
                        }
                    };
                    return cell;
                }
            };
            dayCol.setCellFactory(cellFactory1);

            TableColumn priceCol = new TableColumn("????n gi??");
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
                        final Button btn = new Button("S???a");

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
            Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory2
                    = //
                    new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
                @Override
                public TableCell call(final TableColumn<Package, String> param) {
                    final TableCell<Package, String> cell = new TableCell<Package, String>() {
                        final Button btn = new Button("Xo??");

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
                                        Alert.showAlert(AlertType.INFORMATION, "Qu???n l?? nhu y???u ph???m", "Xo?? nhu y???u ph???m th??nh c??ng!");
                                        getListPackageThread();
                                    } else {
                                        Alert.showAlert(AlertType.WARNING, "Qu???n l?? nhu y???u ph???m", "Kh??ng th??? xo?? nhu y???u ph???m n??y!");
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

            deleteCol.setCellFactory(cellFactory2);

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
