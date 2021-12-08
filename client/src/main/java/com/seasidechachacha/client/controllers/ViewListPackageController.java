package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagerDao.getPackageList;
import com.seasidechachacha.client.models.Package;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewListPackageController {

    private static List<Package> data;

    @FXML
    private TableView<Package> table;
    private static TableColumn<Package, String> numberCol, nameCol, limitCol, dayCol, priceCol, editCol, deleteCol;

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        data = getPackageList(6, 0);
        setTable(table, FXCollections.observableList(data));
        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewPackage", null);
            } catch (IOException ex) {
                Logger.getLogger(ViewListPackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private TableColumn<Package, String> getTableColumnByName(TableView<Package> tableView, String name) {
        for (TableColumn<Package, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<Package, String>) col;
            }
        }
        return null;
    }

    public void setColumns(TableView<Package> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Package, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Package, String> p) {
                return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });

        numberCol.setSortable(false);

        nameCol = getTableColumnByName(table, "Tên gói");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        limitCol = getTableColumnByName(table, "Mức giới hạn");
        limitCol.setCellValueFactory(new PropertyValueFactory<>("limitPerPerson"));

        dayCol = getTableColumnByName(table, "Thời gian giới hạn");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("dayCooldown"));

        priceCol = getTableColumnByName(table, "Đơn giá");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void setTable(TableView<Package> table, ObservableList<Package> data) {
        setColumns(table);
        editCol = getTableColumnByName(table, "#1");
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
                                    Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
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

        deleteCol = getTableColumnByName(table, "#2");
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
//                                try {
//                                    Package row = getTableRow().getItem();
//                                    App.setCurrentPane("pn_all", "view/ViewPersonalInfo", row);
//                                } catch (IOException ex) {
//                                    Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
//                                }
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

        table.setItems(data);

    }
}
