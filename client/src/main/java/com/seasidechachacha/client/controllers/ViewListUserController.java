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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewListUserController {

    private static final ObservableList<UserAccount> data = FXCollections.observableArrayList(
            new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
            new UserAccount("Nguyễn Văn B", "1950", "abc", "F2"),
            new UserAccount("Nguyễn Văn C", "1950", "abc", "F2"),
            new UserAccount("Nguyễn Văn D", "1950", "abc", "F2"),
            new UserAccount("Nguyễn Văn E", "1950", "abc", "F2"));

    @FXML
    private TableView<UserAccount> table;
    private static TableColumn<UserAccount, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol,
            actionCol;

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        setTable(table, data);
        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewUser", null);
            } catch (IOException ex) {
                Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private TableColumn<UserAccount, String> getTableColumnByName(TableView<UserAccount> tableView, String name) {
        for (TableColumn<UserAccount, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                // TODO: Type safety: Unchecked cast from TableColumn<UserAccount,capture#2-of
                // ?> to TableColumn<UserAccount,String>
                return (TableColumn<UserAccount, String>) col;
            }
        }
        return null;
    }

    public void setColumns(TableView<UserAccount> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<UserAccount, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserAccount, String> p) {
                        return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
                    }
                });

        numberCol.setSortable(false);

        fullNameCol = getTableColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<UserAccount, String>("fullName"));

        birthYearCol = getTableColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<UserAccount, String>("birthYear"));

        addressCol = getTableColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<UserAccount, String>("address"));

        statusCol = getTableColumnByName(table, "Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<UserAccount, String>("status"));
    }

    public void setTable(TableView<UserAccount> table, ObservableList<UserAccount> data) {
        setColumns(table);
        actionCol = getTableColumnByName(table, "#");
        actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
        Callback<TableColumn<UserAccount, String>, TableCell<UserAccount, String>> cellFactory = //
                new Callback<TableColumn<UserAccount, String>, TableCell<UserAccount, String>>() {
                    // TODO: TableCell is a raw type. References to generic type TableCell<S,T>
                    // should be parameterized
                    @Override
                    public TableCell call(final TableColumn<UserAccount, String> param) {
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
                                            Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE,
                                                    null, ex);
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
