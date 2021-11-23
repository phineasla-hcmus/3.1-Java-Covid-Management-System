package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.UserAccount;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewListUserController {

    private static final ObservableList<UserAccount> data
            = FXCollections.observableArrayList(
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn B", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn C", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn D", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn E", "1950", "abc", "F2")
            );
    private ViewPersonalInfoController infoController;
    
    @FXML
    private TableView<UserAccount> table;
    private static TableColumn numberCol, fullNameCol, birthYearCol, addressCol, statusCol, actionCol;

    @FXML
    private void initialize() {
        setTable(table, data);
    }

    private <T> TableColumn<T, ?> getTableColumnByName(TableView<T> tableView, String name) {
        for (TableColumn<T, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public void setColumns(TableView<UserAccount> table, TableColumn numberCol, TableColumn fullNameCol, TableColumn birthYearCol, TableColumn addressCol, TableColumn statusCol, TableColumn actionCol) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserAccount, UserAccount>, ObservableValue<UserAccount>>() {
            @Override
            public ObservableValue<UserAccount> call(TableColumn.CellDataFeatures<UserAccount, UserAccount> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numberCol.setCellFactory(new Callback<TableColumn<UserAccount, UserAccount>, TableCell<UserAccount, UserAccount>>() {
            public TableCell<UserAccount, UserAccount> call(TableColumn<UserAccount, UserAccount> param) {
                return new TableCell<UserAccount, UserAccount>() {
                    @Override
                    protected void updateItem(UserAccount item, boolean empty) {
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

        fullNameCol = getTableColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        birthYearCol = getTableColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        addressCol = getTableColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        statusCol = getTableColumnByName(table, "Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void setTable(TableView<UserAccount> table, ObservableList<UserAccount> data) {
        setColumns(table, numberCol, fullNameCol, birthYearCol, addressCol, statusCol, actionCol);
        actionCol = getTableColumnByName(table, "#");
        actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
        Callback<TableColumn<UserAccount, String>, TableCell<UserAccount, String>> cellFactory
                = //
                new Callback<TableColumn<UserAccount, String>, TableCell<UserAccount, String>>() {
            @Override
            public TableCell call(final TableColumn<UserAccount, String> param) {
                final TableCell<UserAccount, String> cell = new TableCell<UserAccount, String>() {
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
                                    UserAccount row = getTableRow().getItem();
                                    App.setCurrentPane("pn_all", "view/ViewPersonalInfo", row);
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

        actionCol.setCellFactory(cellFactory);

        table.setItems(data);

    }

}
