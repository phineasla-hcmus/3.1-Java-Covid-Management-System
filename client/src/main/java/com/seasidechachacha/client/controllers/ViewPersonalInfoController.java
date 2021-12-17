package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import static com.seasidechachacha.client.database.ManagedUserDao.get;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentState;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentTreatmentPlace;
import static com.seasidechachacha.client.database.ManagerDao.getRelatedManagedUser;
import static com.seasidechachacha.client.database.ManagerDao.getStateHistoryList;
import static com.seasidechachacha.client.database.ManagerDao.getTreatmentPlaceHistoryList;
import static com.seasidechachacha.client.database.ManagerDao.getTreatmentPlaceList;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.StateHistory;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.TreatmentPlaceHistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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

    private static final Logger logger = LogManager.getLogger(ViewListPackageController.class);

    @FXML
    private TableView<ManagedUser> tableRelated;

    @FXML
    private TableView<StateHistory> tableStatus;

    @FXML
    private TableView<TreatmentPlaceHistory> tablePlace;

    @FXML
    private TableColumn<ManagedUser, String> numberCol, fullNameCol, birthYearCol, addressCol, statusCol;

    @FXML
    private TableColumn<StateHistory, String> dateStatusCol;

    @FXML
    private TableColumn<StateHistory, Integer> currentStatusCol;

    @FXML
    private TableColumn<TreatmentPlaceHistory, String> datePlaceCol;
    
    @FXML
    private TableColumn<TreatmentPlaceHistory, Integer> placeCol;
    
    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private Button btnChangeStatus, btnChangePlace;

    @FXML
    private void initialize() {
        // setTable(table, relatedData);

    }

    public void setup(ManagedUser user) {
        String userId = user.getUserId();
        ManagedUser currentUser = get(userId);
        labelFullName.setText(currentUser.getName());
        labelIdentityCard.setText(currentUser.getUserId());
        labelBirthYear.setText(String.valueOf(currentUser.getBirthYear()));
        labelAddress.setText(currentUser.getAddress());
        String currentStatus = "F" + getCurrentState(currentUser.getUserId());
        labelStatus.setText(currentStatus);
        TreatmentPlace treat = getCurrentTreatmentPlace(currentUser.getUserId());
        String currentPlace = treat.getName();
        if (treat != null) {
            labelTreatmentPlace.setText(treat.getName());
        }

        // String defaultStatus = user.getStatus();
        String status[] = {"F0", "F1", "F2"};

        ChoiceDialog<String> statusDialog = new ChoiceDialog<String>(currentStatus, status);
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

//        String defaultPlace = "abc";
//        String place[] = {"abc", "xyz", "ohi"};
        List<TreatmentPlace> treatmentPlaceList = getTreatmentPlaceList();
        List<String> places = new ArrayList<String>();
        
        for (int i = 0; i < treatmentPlaceList.size(); i++) {
            places.add(treatmentPlaceList.get(i).getName());
        }

        ChoiceDialog<String> placeDialog = new ChoiceDialog<String>(currentPlace, places);
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

        List<ManagedUser> relatedList = getRelatedManagedUser(currentUser.getUserId());
        setTableRelated(tableRelated, FXCollections.observableArrayList(relatedList));

        List<StateHistory> stateHistoryList = getStateHistoryList(currentUser.getUserId());
        setTableStatus(tableStatus, FXCollections.observableArrayList(stateHistoryList));
        
        List<TreatmentPlaceHistory> treatHistoryList = getTreatmentPlaceHistoryList(currentUser.getUserId());
        setTableTreat(tablePlace, FXCollections.observableArrayList(treatHistoryList));

    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/ViewListUser", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    private TableColumn<ManagedUser, String> getTableRelatedColumnByName(TableView<ManagedUser> tableView, String name) {
        for (TableColumn<ManagedUser, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<ManagedUser, String>) col;
            }
        }
        return null;
    }

    private TableColumn<StateHistory, String> getTableStatusColumnByName(TableView<StateHistory> tableView, String name) {
        for (TableColumn<StateHistory, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<StateHistory, String>) col;
            }
        }
        return null;
    }

    private TableColumn<StateHistory, Integer> getTableStatusByName(TableView<StateHistory> tableView, String name) {
        for (TableColumn<StateHistory, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<StateHistory, Integer>) col;
            }
        }
        return null;
    }
    
     private TableColumn<TreatmentPlaceHistory, String> getTableTreatColumnByName(TableView<TreatmentPlaceHistory> tableView, String name) {
        for (TableColumn<TreatmentPlaceHistory, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<TreatmentPlaceHistory, String>) col;
            }
        }
        return null;
    }
     
      private TableColumn<TreatmentPlaceHistory, Integer> getTableTreatByName(TableView<TreatmentPlaceHistory> tableView, String name) {
        for (TableColumn<TreatmentPlaceHistory, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return (TableColumn<TreatmentPlaceHistory, Integer>) col;
            }
        }
        return null;
    }

    public void setColumnsRelated(TableView<ManagedUser> table) {
        numberCol = getTableRelatedColumnByName(table, "ID");
        numberCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("userId"));

        fullNameCol = getTableRelatedColumnByName(table, "Họ tên");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("name"));

        birthYearCol = getTableRelatedColumnByName(table, "Năm sinh");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("birthYear"));

        addressCol = getTableRelatedColumnByName(table, "Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<ManagedUser, String>("address"));

        statusCol = getTableRelatedColumnByName(table, "Trạng thái");
        Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>> cellFactory1
                = //
                new Callback<TableColumn<ManagedUser, String>, TableCell<ManagedUser, String>>() {
            @Override
            public TableCell call(final TableColumn<ManagedUser, String> param) {
                final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTableRow().getItem() != null) {
                                ManagedUser user = (ManagedUser) getTableRow().getItem();
                                setText("F" + String.valueOf(ManagerDao.getCurrentState(user.getUserId())));
                            }

                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        };
        statusCol.setCellFactory(cellFactory1);
    }

    public void setTableRelated(TableView<ManagedUser> table, ObservableList<ManagedUser> data) {
        setColumnsRelated(table);
        table.setItems(data);

    }

    public void setColumnsStatus(TableView<StateHistory> table) {
        dateStatusCol = getTableStatusColumnByName(table, "Ngày");
        dateStatusCol.setCellValueFactory(new PropertyValueFactory<StateHistory, String>("time"));

        currentStatusCol = getTableStatusByName(table, "Trạng thái");
        currentStatusCol.setCellValueFactory(new PropertyValueFactory<StateHistory, Integer>("state"));
    }

    public void setTableStatus(TableView<StateHistory> table, ObservableList<StateHistory> data) {
        setColumnsStatus(table);
        table.setItems(data);

    }
    
     public void setColumnsTreat(TableView<TreatmentPlaceHistory> table) {
        datePlaceCol = getTableTreatColumnByName(table, "Ngày");
        datePlaceCol.setCellValueFactory(new PropertyValueFactory<TreatmentPlaceHistory, String>("time"));

        placeCol = getTableTreatByName(table, "Nơi điều trị");
        placeCol.setCellValueFactory(new PropertyValueFactory<TreatmentPlaceHistory, Integer>("treatID"));
    }
     
     public void setTableTreat(TableView<TreatmentPlaceHistory> table, ObservableList<TreatmentPlaceHistory> data) {
        setColumnsTreat(table);
        table.setItems(data);

    }
}
