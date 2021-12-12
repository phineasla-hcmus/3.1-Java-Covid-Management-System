package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagedUserDao;
import com.seasidechachacha.client.models.ManagedUser;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ViewListUserController {

    private static final Logger logger = LogManager.getLogger(ViewListUserController.class);
    private static List<ManagedUser> data;

//    @FXML
//    private TableView<ManagedUser> table;
//    private static TableColumn<ManagedUser, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol,
//            actionCol;
    @FXML
    private Button btnAdd, btnSearch, btnSort;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox cbSort;

    @FXML
    private Pagination pagination;

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

            birthYearCol.setMinWidth(160);

            TableColumn addrCol = new TableColumn("Địa chỉ");
            addrCol.setCellValueFactory(
                    new PropertyValueFactory<ManagedUser, String>("address"));

            addrCol.setMinWidth(160);

            TableColumn statusCol = new TableColumn("Trạng thái");
            statusCol.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ManagedUser, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ManagedUser, String> p) {
                    return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
                }
            });

//            statusCol.setMinWidth(160);

            TableColumn actionCol = new TableColumn("");
            actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>> cellFactory
                    = //
                    new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>() {
                // TODO: TableCell is a raw type. References to generic type TableCell<S,T>
                // should be parameterized
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
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    @FXML
    private void initialize() {
        data = ManagedUserDao.getList(5, 0);
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
                App.setCurrentPane("pn_all", "view/AddNewUser", null);
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

    private TableColumn<ManagedUser, String> getTableColumnByName(TableView<ManagedUser> tableView, String name) {
        for (TableColumn<ManagedUser, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                // TODO: Type safety: Unchecked cast from TableColumn<User,capture#2-of?> to
                // TableColumn<User,String>
                return (TableColumn<ManagedUser, String>) col;
            }
        }
        return null;
    }

//    public void setColumns(TableView<ManagedUser> table) {
//        numberCol = getTableColumnByName(table, "ID");
//        numberCol.setCellValueFactory(
//                new Callback<TableColumn.CellDataFeatures<ManagedUser, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<ManagedUser, String> p) {
//                return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
//            }
//        });
//
//        numberCol.setSortable(false);
//
//        fullNameCol = getTableColumnByName(table, "Họ tên");
//        fullNameCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("name"));
//
//        birthYearCol = getTableColumnByName(table, "Năm sinh");
//        birthYearCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("birthYear"));
//
//        addressCol = getTableColumnByName(table, "Địa chỉ");
//        addressCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("address"));
//
//        // statusCol = getTableColumnByName(table, "Trạng thái");
//        // statusCol.setCellValueFactory(new PropertyValueFactory<User,
//        // String>("status"));
//    }
//
//    public void setTable(TableView<ManagedUser> table, ObservableList<ManagedUser> data) {
//        setColumns(table);
//        actionCol = getTableColumnByName(table, "#");
//        actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
//        Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>> cellFactory
//                = //
//                new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>() {
//            // TODO: TableCell is a raw type. References to generic type TableCell<S,T>
//            // should be parameterized
//            @Override
//            public TableCell call(final TableColumn<ManagedUser, String> param) {
//                final TableCell<Object, String> cell = new TableCell<Object, String>() {
//                    final Button btn = new Button("Xem chi tiết");
//
//                    @Override
//                    public void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                            setText(null);
//                        } else {
//                            btn.setOnAction(event -> {
//                                try {
//                                    App.setCurrentPane("pn_all", "view/ViewPersonalInfo", getTableRow());
//                                } catch (IOException ex) {
//                                    logger.fatal(ex);
//                                }
//                            });
//                            setGraphic(btn);
//                            setText(null);
//                        }
//                    }
//                };
//                cell.setAlignment(Pos.CENTER);
//                return cell;
//            }
//        };
//
//        actionCol.setCellFactory(cellFactory);
//
//        table.setItems(data);
//
//    }
}
