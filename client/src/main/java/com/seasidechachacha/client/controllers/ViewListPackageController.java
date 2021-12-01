package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.Package;
import java.io.IOException;
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

    private static final ObservableList<Package> data
            = FXCollections.observableArrayList(
                    new Package("Panadol extra", "1", "30", "135000"),
                    new Package("Khẩu trang than hoạt tính 4 lớp Pharmacity", "1", "30", "89000"),
                    new Package("Bộ xét nghiệm nhanh COVID-19 tại nhà Humasis COVID-19 Ag Home Tes", "1", "30", "610000"),
                    new Package("Siro điều trị viêm đường hô hấp kèm theo ho Prospa", "2", "7", "68000"),
                    new Package("Dầu gió Khuynh Diệp OPC", "2", "15", "62900")
            );
//    private ViewPersonalInfoController infoController;

    @FXML
    private TableView<Package> table;
    private static TableColumn numberCol, nameCol, limitCol, dayCol, priceCol, editCol, deleteCol;

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        setTable(table, data);
        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewPackage", null);
            } catch (IOException ex) {
                Logger.getLogger(ViewListPackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private <T> TableColumn<T, ?> getTableColumnByName(TableView<T> tableView, String name) {
        for (TableColumn<T, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public void setColumns(TableView<Package> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Package, Package>, ObservableValue<Package>>() {
            @Override
            public ObservableValue<Package> call(TableColumn.CellDataFeatures<Package, Package> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numberCol.setCellFactory(new Callback<TableColumn<Package, Package>, TableCell<Package, Package>>() {
            public TableCell<Package, Package> call(TableColumn<Package, Package> param) {
                return new TableCell<Package, Package>() {
                    @Override
                    protected void updateItem(Package item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex() + 1 + "");
                        } else {
                            setText("");
                        }
                    }
                };
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
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
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
                final TableCell<Package, String> cell = new TableCell<Package, String>() {
                    final Button btn = new Button("Sửa");

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
