package com.seasidechachacha.client.controllers;

import java.sql.SQLException;
import java.util.List;

import com.seasidechachacha.client.database.PaymentDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.Package;
import java.io.IOException;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import com.seasidechachacha.client.utils.Alert;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuyPackageController {

    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);
    private List<Package> listP;

    @FXML
    private TextField choosePackage;
    @FXML
    private Label totalCost;

    @FXML
    private Button btnSearch, btnFilter, acceptButton;

    @FXML
    private ComboBox cbSort;

    @FXML
    private TextField tfSearch, quantity;

    private String keyword = "";

    @FXML
    private Pagination pagination;

    @FXML
    private void initialize() {
        getListPackageThread();
        btnSearch.setOnAction(event -> {
            keyword = tfSearch.getText();
            if (!keyword.equals("")) {
                getSearchResult(keyword);
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
                        String userid = Session.getUser().getUserId();
                        int packageid = listP.get(i).getPackageID();
                        int limitperPerson = listP.get(i).getLimitPerPerson();
                        int daycooldown = listP.get(i).getDayCooldown();
                        if (PaymentDao.isExistOnCart(userid, packageid)) {
                            Alert.showAlert(AlertType.WARNING, "Ch???n mua g??i nhu y???u ph???m",
                                    "S???n ph???m n??y b???n ???? ?????t trong gi??? h??ng !!!\nH??y h???y ????n h??ng ????? c?? th??? ?????t l???i s???n ph???m");
                        } else {
                            if (quantity.getText() != "" && quantity.getText().matches("\\d+") && Integer.parseInt(quantity.getText())>0) {
                                if (Integer.parseInt(quantity.getText()) > listP.get(i).getLimitPerPerson()) {
                                    Alert.showAlert(AlertType.WARNING, "Ch???n mua g??i nhu y???u ph???m",
                                            "S??? l?????ng mua l???n h??n gi???i h???n cho ph??p, xin h??y nh???p l???i !!!");
                                } else {
                                    if (limitperPerson <= PaymentDao.quantityOfBoughtPackage(userid, packageid, daycooldown)) {
                                        Alert.showAlert(AlertType.WARNING, "Ch???n mua g??i nhu y???u ph???m",
                                                "B???n ???? mua s???n ph???m n??y v?????t qu?? gi???i h???n cho ph??p trong v??ng " + Integer.toString(daycooldown) + " ng??y!!!\nH??y ?????t l???i sau v??i ng??y n???a");
                                    } else {
                                        addCartThread(packageid);
                                    }
                                }
                            } else {
                                Alert.showAlert(AlertType.WARNING, "Ch???n mua g??i nhu y???u ph???m",
                                        "Xin ki???m tra l???i s??? l?????ng nh???p !!!");
                            }
                        }
                    }
                }
            } else {
                Alert.showAlert(AlertType.WARNING, "Ch???n mua g??i nhu y???u ph???m",
                        "H??y ch???n g??i c???n mua!!!");
            }
        });

        btnFilter.setOnAction(event -> {
            showFilterDialog();
        });
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
        TaskExecutor.execute(flag);
    }

    public void resolveAddtoCart(WorkerStateEvent e, boolean flag) {
        if (flag == true) {
            Alert.showAlert(AlertType.INFORMATION, "Ch???n mua g??i nhu y???u ph???m",
                    "Th??m v??o gi??? h??ng th??nh c??ng !!!");
        }
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
                com.seasidechachacha.client.utils.Alert.showAlert(AlertType.WARNING, "L???c nhu y???u ph???m", "Vui l??ng ch???n ti??u ch??!");
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
            Alert.showAlert(AlertType.WARNING, "Qu???n l?? nhu y???u ph???m", "Kh??ng t??m th???y g??i nhu y???u ph???m ph?? h???p!");
            return;
        }
        listP = list;
        if (listP.size() % rowsPerPage() == 0) {
            pagination.setPageCount(listP.size() / rowsPerPage());

        } else {
            pagination.setPageCount(listP.size() / rowsPerPage() + 1);

        }
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > listP.size() / rowsPerPage()) {
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
        int displace = listP.size() % rowsPerPage();
        lastIndex = listP.size() / rowsPerPage();

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

            nameCol.setMinWidth(350);

            TableColumn limitCol = new TableColumn("M???c gi???i h???n");
            limitCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("limitPerPerson"));

            limitCol.setMinWidth(80);

            TableColumn dayCol = new TableColumn("Th???i gian gi???i h???n");
//            dayCol.setCellValueFactory(
//                    new PropertyValueFactory<Package, String>("dayCooldown"));

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

            priceCol.setMinWidth(100);

            table.getColumns().addAll(numCol, nameCol, limitCol, dayCol, priceCol);
            table.setItems(FXCollections.observableArrayList(listP));
            table.setOnMouseClicked(e -> {

                choosePackage.setText(table.getSelectionModel().getSelectedItem().getName());

            });
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(listP.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(listP.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }
}
