package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.models.ManagedUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
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
    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);

    // private static final ObservableList<User> relatedData =
    // FXCollections.observableArrayList(
    // new User("Nguyễn Văn A", "1950", "abc", "F2"),
    // new User("Nguyễn Văn A", "1950", "abc", "F2"),
    // new User("Nguyễn Văn A", "1950", "abc", "F2"),
    // new User("Nguyễn Văn A", "1950", "abc", "F2"),
    // new User("Nguyễn Văn A", "1950", "abc", "F2"));
    @FXML
    private TableView<ManagedUser> table;

    @FXML
    private TableColumn<ManagedUser, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol;

    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private Button btnChangeStatus, btnChangePlace;

    @FXML
    private void initialize() {
        // setTable(table, relatedData);

    }

    public void setup(ManagedUser user) {
        labelFullName.setText(user.getName());
        labelIdentityCard.setText(user.getUserId());
        labelBirthYear.setText(String.valueOf(user.getBirthYear()));
        labelAddress.setText(user.getAddress());
        // labelStatus.setText(user.getStatus());
        // labelTreatmentPlace.setText("abc");

        // String defaultStatus = user.getStatus();
        String defaultStatus = "F2";
        String status[] = { "F0", "F1", "F2", "F3" };

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
        String place[] = { "abc", "xyz", "ohi" };

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
            logger.fatal(ex);
        }
    }

    private TableColumn<ManagedUser, String> getTableColumnByName(TableView<ManagedUser> tableView, String name) {
        for (TableColumn<ManagedUser, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<ManagedUser, String>) col;
            }
        }
        return null;
    }

    public void setColumns(TableView<ManagedUser> table) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(new Callback<CellDataFeatures<ManagedUser, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ManagedUser, String> p) {
                return new ReadOnlyObjectWrapper(table.getItems().indexOf(p.getValue()) + 1 + "");
            }
        });
        numberCol.setSortable(false);

        fullNameCol = getTableColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("fullName"));

        birthYearCol = getTableColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("birthYear"));

        addressCol = getTableColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("address"));

        statusCol = getTableColumnByName(table, "Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("status"));
    }

    public void setTable(TableView<ManagedUser> table, ObservableList<ManagedUser> data) {
        setColumns(table);
        table.setItems(data);

    }
}
