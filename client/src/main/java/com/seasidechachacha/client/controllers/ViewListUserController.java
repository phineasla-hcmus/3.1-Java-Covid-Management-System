package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.UserDao.getUserList;
import com.seasidechachacha.client.models.User;
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

public class ViewListUserController {
    
    private static List<User> data;

    @FXML
    private TableView<User> table;
    private static TableColumn<User, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol,
            actionCol;

    @FXML
    private Button btnAdd;

    @FXML
    private void initialize() {
        data = getUserList(5, 0);
        setTable(table, FXCollections.observableList(data));
        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewUser", null);
            } catch (IOException ex) {
                Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private TableColumn<User, String> getTableColumnByName(TableView<User> tableView, String name) {
        for (TableColumn<User, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                // TODO: Type safety: Unchecked cast from TableColumn<User,capture#2-of
                // ?> to TableColumn<User,String>
                return (TableColumn<User, String>) col;
            }
        }
        return null;
    }

    public void setColumns(TableView<User> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> p) {
                        return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
                    }
                });

        numberCol.setSortable(false);

        fullNameCol = getTableColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        birthYearCol = getTableColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<User, String>("birthYear"));

        addressCol = getTableColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<User, String>("address"));

//        statusCol = getTableColumnByName(table, "Trạng thái");
//        statusCol.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
    }

    public void setTable(TableView<User> table, ObservableList<User> data) {
        setColumns(table);
        actionCol = getTableColumnByName(table, "#");
        actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory = //
                new Callback<TableColumn<User, String>, TableCell<User, String>>() {
                    // TODO: TableCell is a raw type. References to generic type TableCell<S,T>
                    // should be parameterized
                    @Override
                    public TableCell call(final TableColumn<User, String> param) {
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
