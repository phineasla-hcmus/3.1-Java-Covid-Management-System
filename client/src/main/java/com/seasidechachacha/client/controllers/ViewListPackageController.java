package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagerDao.getPackageList;
import com.seasidechachacha.client.models.Package;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

//    @FXML
//    private TableView<Package> table;
//    private static TableColumn<Package, String> numberCol, nameCol, limitCol, dayCol, priceCol, editCol, deleteCol;
    @FXML
    private Button btnAdd, btnSearch, btnSort;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox cbSort;

    @FXML
    private Pagination pagination;

    @FXML
    private void initialize() {
        data = getPackageList(6, 0);
//        setTable(table, FXCollections.observableList(data));

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

        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewPackage", null);
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        btnSearch.setOnAction(event -> {

            String keyword = tfSearch.getText();

            //for testing purpose
            data.remove(3);

            //TODO
//            table.refresh();
        });
        cbSort.getItems().addAll("ID", "Họ tên", "Năm sinh", "Trạng thái");
        btnSort.setOnAction(event -> {
            //TODO
        });

    }

    public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 4;
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
            TableView<Package> table = new TableView<Package>();
            TableColumn numCol = new TableColumn("ID");
            numCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("packageID"));

            numCol.setMinWidth(20);

            TableColumn nameCol = new TableColumn("Tên gói");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("name"));

            nameCol.setMinWidth(160);

            TableColumn limitCol = new TableColumn("Mức giới hạn");
            limitCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("limitPerPerson"));

            limitCol.setMinWidth(160);

            TableColumn dayCol = new TableColumn("Thời gian giới hạn");
            dayCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("dayCooldown"));

            dayCol.setMinWidth(160);

            TableColumn priceCol = new TableColumn("Đơn giá");
            priceCol.setCellValueFactory(
                    new PropertyValueFactory<Package, String>("price"));

//            statusCol.setMinWidth(160);
            TableColumn actionCol = new TableColumn("");
            actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory
                    = //
                    new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
                // TODO: TableCell is a raw type. References to generic type TableCell<S,T>
                // should be parameterized
                @Override
                public TableCell call(final TableColumn<Package, String> param) {
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

            actionCol.setCellFactory(cellFactory);

            table.getColumns().addAll(numCol, nameCol, limitCol, dayCol, priceCol, actionCol);
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

//    private TableColumn<Package, String> getTableColumnByName(TableView<Package> tableView, String name) {
//        for (TableColumn<Package, ?> col : tableView.getColumns()) {
//            if (col.getText().equals(name)) {
//                return (TableColumn<Package, String>) col;
//            }
//        }
//        return null;
//    }
//    public void setColumns(TableView<Package> table) {
//        numberCol = getTableColumnByName(table, "STT");
//        numberCol.setCellValueFactory(
//                new Callback<TableColumn.CellDataFeatures<Package, String>, ObservableValue<String>>() {
//                    @Override
//                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Package, String> p) {
//                        return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
//                    }
//                });
//
//        numberCol.setSortable(false);
//
//        nameCol = getTableColumnByName(table, "Tên gói");
//        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
//
//        limitCol = getTableColumnByName(table, "Mức giới hạn");
//        limitCol.setCellValueFactory(new PropertyValueFactory<>("limitPerPerson"));
//
//        dayCol = getTableColumnByName(table, "Thời gian giới hạn");
//        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayCooldown"));
//
//        priceCol = getTableColumnByName(table, "Đơn giá");
//        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
//    }
//
//    public void setTable(TableView<Package> table, ObservableList<Package> data) {
//        setColumns(table);
//        editCol = getTableColumnByName(table, "#1");
//        editCol.setCellValueFactory(new PropertyValueFactory<>(""));
//        Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory = //
//                new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
//                    @Override
//                    public TableCell call(final TableColumn<Package, String> param) {
//                        final TableCell<Object, String> cell = new TableCell<Object, String>() {
//                            final Button btn = new Button("Sửa");
//
//                            @Override
//                            public void updateItem(String item, boolean empty) {
//                                super.updateItem(item, empty);
//                                if (empty) {
//                                    setGraphic(null);
//                                    setText(null);
//                                } else {
//                                    btn.setOnAction(event -> {
//                                        try {
//                                            App.setCurrentPane("pn_all", "view/ViewPackageInfo", getTableRow());
//                                        } catch (IOException ex) {
//                                            logger.fatal(ex);
//                                        }
//                                    });
//                                    setGraphic(btn);
//                                    setText(null);
//                                }
//                            }
//                        };
//                        cell.setAlignment(Pos.CENTER);
//                        return cell;
//                    }
//                };
//
//        editCol.setCellFactory(cellFactory);
//
//        deleteCol = getTableColumnByName(table, "#2");
//        deleteCol.setCellValueFactory(new PropertyValueFactory<>(""));
//        Callback<TableColumn<Package, String>, TableCell<Package, String>> cellFactory1 = //
//                new Callback<TableColumn<Package, String>, TableCell<Package, String>>() {
//                    @Override
//                    public TableCell call(final TableColumn<Package, String> param) {
//                        final TableCell<Package, String> cell = new TableCell<Package, String>() {
//                            final Button btn = new Button("Xoá");
//
//                            @Override
//                            public void updateItem(String item, boolean empty) {
//                                super.updateItem(item, empty);
//                                if (empty) {
//                                    setGraphic(null);
//                                    setText(null);
//                                } else {
//                                    btn.setOnAction(event -> {
//                                        // try {
//                                        // Package row = getTableRow().getItem();
//                                        // App.setCurrentPane("pn_all", "view/ViewPersonalInfo", row);
//                                        // } catch (IOException ex) {
//                                        // }
//                                    });
//                                    setGraphic(btn);
//                                    setText(null);
//                                }
//                            }
//                        };
//                        cell.setAlignment(Pos.CENTER);
//                        return cell;
//                    }
//                };
//
//        deleteCol.setCellFactory(cellFactory1);
//
//        table.setItems(data);
//
//    }
}
