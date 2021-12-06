package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.UserAccount;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewPersonalInfoController {

    private static final ObservableList<UserAccount> relatedData
            = FXCollections.observableArrayList(
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2")
            );
    @FXML
    private TableView<UserAccount> table;

    @FXML
    private TableColumn numberCol, fullNameCol, birthYearCol, addressCol, statusCol;

    @FXML
    private TableColumn dateCol, currentStatusCol, currentPlaceCol;

    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private Button btnChangeStatus, btnChangePlace;

    @FXML
    private void initialize() {
        setTable(table, relatedData);

    }

    public void setup(UserAccount user) {
        labelFullName.setText(user.getFullName());
        labelIdentityCard.setText("123456789");
        labelBirthYear.setText(user.getBirthYear());
        labelAddress.setText(user.getAddress());
        labelStatus.setText(user.getStatus());
        labelTreatmentPlace.setText("abc");

        String defaultStatus = user.getStatus();
        String status[] = {"F0", "F1", "F2", "F3"};

        ChoiceDialog<String> statusDialog = new ChoiceDialog<>(defaultStatus, status);
        statusDialog.setResultConverter((ButtonType type)
                -> {
            ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                return statusDialog.getSelectedItem();
            } else {
                return null;
            }
        });

        btnChangeStatus.setOnAction(event -> {
            statusDialog.setTitle("Thay đổi trạng thái");
            statusDialog.setHeaderText("Trạng thái hiện tại");
            Optional<String> result = statusDialog.showAndWait();
            if (result.isPresent()) {
                labelStatus.setText(result.get());
            }
        });

        String defaultPlace = "abc";
        String place[] = {"abc", "xyz", "ohi"};

        ChoiceDialog<String> placeDialog = new ChoiceDialog<>(defaultPlace, place);
        placeDialog.setResultConverter((ButtonType type)
                -> {
            ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                return placeDialog.getSelectedItem();
            } else {
                return null;
            }
        });

        btnChangePlace.setOnAction(event -> {
            placeDialog.setTitle("Thay đổi nơi điều trị/cách ly");
            placeDialog.setHeaderText("Nơi điều trị/cách ly hiện tại");
            Optional<String> result = placeDialog.showAndWait();
            if (result.isPresent()) {
                labelTreatmentPlace.setText(result.get());
            }
        });
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
        } catch (IOException ex) {
            Logger.getLogger(ViewListUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private <T> TableColumn<T, ?> getTableColumnByName(TableView<T> tableView, String name) {
        for (TableColumn<T, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public void setColumns(TableView<UserAccount> table) {
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
        setColumns(table);
        table.setItems(data);

    }
}
