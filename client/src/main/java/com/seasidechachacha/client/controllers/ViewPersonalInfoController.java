package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.models.User;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ViewPersonalInfoController {

//    private static final ObservableList<User> relatedData = FXCollections.observableArrayList(
//            new User("Nguyễn Văn A", "1950", "abc", "F2"),
//            new User("Nguyễn Văn A", "1950", "abc", "F2"),
//            new User("Nguyễn Văn A", "1950", "abc", "F2"),
//            new User("Nguyễn Văn A", "1950", "abc", "F2"),
//            new User("Nguyễn Văn A", "1950", "abc", "F2"));
    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol;

    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private Button btnChangeStatus, btnChangePlace;

    @FXML
    private void initialize() {
//        setTable(table, relatedData);

    }

    public void setup(User user) {
        labelFullName.setText(user.getName());
        labelIdentityCard.setText(user.getUserId());
        labelBirthYear.setText(String.valueOf(user.getBirthYear()));
        labelAddress.setText(user.getAddress());
//        labelStatus.setText(user.getStatus());
//        labelTreatmentPlace.setText("abc");

//        String defaultStatus = user.getStatus();
        String defaultStatus = "F2";
        String status[] = {"F0", "F1", "F2", "F3"};

        ChoiceDialog<String> statusDialog = new ChoiceDialog<String>(defaultStatus, status);
        statusDialog.setResultConverter((ButtonType type) -> {

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

        ChoiceDialog<String> placeDialog = new ChoiceDialog<String>(defaultPlace, place);
        placeDialog.setResultConverter((ButtonType type) -> {

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

    private TableColumn<User, String> getTableColumnByName(TableView<User> tableView, String name) {
        for (TableColumn<User, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<User, String>) col;
            }
        }
        return null;
    }

    public void setColumns(TableView<User> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<User, String> p) {
                return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberCol.setSortable(false);

        fullNameCol = getTableColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));

        birthYearCol = getTableColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<User, String>("birthYear"));

        addressCol = getTableColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<User, String>("address"));

        statusCol = getTableColumnByName(table, "Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
    }

    public void setTable(TableView<User> table, ObservableList<User> data) {
        setColumns(table);
        table.setItems(data);

    }
}
