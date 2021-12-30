package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.ManagedUserDao;
import com.seasidechachacha.client.database.ManagerDao;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentState;
import static com.seasidechachacha.client.database.ManagerDao.getCurrentTreatmentPlace;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.StateHistory;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.TreatmentPlaceHistory;
import com.seasidechachacha.client.utils.Alert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
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
    private TableColumn<StateHistory, String> dateStatusCol, currentStatusCol;

    @FXML
    private TableColumn<TreatmentPlaceHistory, String> datePlaceCol;

    @FXML
    private TableColumn<TreatmentPlaceHistory, Integer> placeCol;

    @FXML
    private Label labelFullName, labelIdentityCard, labelBirthYear, labelAddress, labelStatus, labelTreatmentPlace;

    @FXML
    private Button btnChangeStatus, btnChangePlace;

    ChoiceDialog<String> statusDialog, placeDialog;

    private String currentStatus, currentPlace, userId;
    private int currentState;

    private ManagerDao manager = new ManagerDao(Session.getUser().getUserId());

    @FXML
    private void initialize() {
        btnChangeStatus.setOnAction(event -> {
            statusDialog.setTitle("Thay đổi trạng thái");
            statusDialog.setHeaderText("Trạng thái hiện tại");
            Optional<String> result = statusDialog.showAndWait();
            if (result.isPresent()) {
                int state = -1;
                switch (result.get()) {
                    case "F0":
                        state = 0;
                        break;
                    case "F1":
                        state = 1;
                        break;
                    case "F2":
                        state = 2;
                        break;
                    default:
                        break;
                }
                if (state > currentState) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin người liên quan Covid19", "Chỉ có thể thay đổi trạng thái từ F2->F1, F2->F0, F1->F0!");
                } else if (state != currentState && manager.setState(userId, state)) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin người liên quan Covid19", "Thay đổi trạng thái thành công!");
                    labelStatus.setText(result.get());
                    getManagedUserThread();
                }
            }
        });
        btnChangePlace.setOnAction(event -> {
            placeDialog.setTitle("Thay đổi nơi điều trị/cách ly");
            placeDialog.setHeaderText("Nơi điều trị/cách ly hiện tại");
            Optional<String> result = placeDialog.showAndWait();
            if (result.isPresent() && !currentPlace.equals(result.get())) {
                int treatID = ManagerDao.getTreatmentPlaceIDByName(result.get());
                TreatmentPlace t = ManagerDao.getTreatmentPlaceByID(treatID);
                if (t.isFull()) {
                    Alert.showAlert(AlertType.WARNING, "Cập nhật thông tin người liên quan Covid19", "Địa điểm điều trị này đã hết chỗ!");
                } else if (manager.addTreatmentPlaceHistory(userId, treatID)) {
                    Alert.showAlert(AlertType.INFORMATION, "Cập nhật thông tin người liên quan Covid19", "Thay đổi địa điểm điều trị thành công!");
                    labelTreatmentPlace.setText(result.get());
                    getManagedUserThread();
                }
            }
        });
    }

    private void getManagedUserThread() {
        Task<ManagedUser> dataTask = new Task<ManagedUser>() {
            @Override
            public ManagedUser call() {
                return ManagedUserDao.get(userId);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveManagedUser(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveManagedUser(WorkerStateEvent e, ManagedUser user) throws IOException {
        labelFullName.setText(user.getName());
        labelIdentityCard.setText(user.getUserId());
        labelBirthYear.setText(String.valueOf(user.getBirthYear()));
        labelAddress.setText(user.getAddress());
        currentState = getCurrentState(user.getUserId());
        currentStatus = "F" + currentState;
        labelStatus.setText(currentStatus);
        TreatmentPlace treat = getCurrentTreatmentPlace(user.getUserId());
        currentPlace = treat.getName();
        if (treat != null) {
            labelTreatmentPlace.setText(currentPlace);
        }

        String status[] = {"F0", "F1", "F2"};

        statusDialog = new ChoiceDialog<String>(currentStatus, status);
        statusDialog.setResultConverter((ButtonType type) -> {

            ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                return statusDialog.getSelectedItem();
            } else {
                return null;
            }
        });
        getListTreatmentPlaceThread();

    }

    private void getListTreatmentPlaceThread() {
        Task<List<TreatmentPlace>> dataTask = new Task<List<TreatmentPlace>>() {
            @Override
            public List<TreatmentPlace> call() {
                return ManagerDao.getTreatmentPlaceList();
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListTreatmentPlace(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveListTreatmentPlace(WorkerStateEvent e, List<TreatmentPlace> list) throws IOException {
        List<String> places = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            places.add(list.get(i).getName());
        }

        placeDialog = new ChoiceDialog<String>(currentPlace, places);
        placeDialog.setResultConverter((ButtonType type) -> {

            ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                return placeDialog.getSelectedItem();
            } else {
                return null;
            }
        });
        getListRelatedThread();
    }

    private void getListRelatedThread() {
        Task<List<ManagedUser>> dataTask = new Task<List<ManagedUser>>() {
            @Override
            public List<ManagedUser> call() {
                return ManagerDao.getRelatedManagedUser(userId);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListRelated(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveListRelated(WorkerStateEvent e, List<ManagedUser> list) throws IOException {
        setTableRelated(tableRelated, FXCollections.observableArrayList(list));
        getListStateHistoryThread();
    }

    private void getListStateHistoryThread() {
        Task<List<StateHistory>> dataTask = new Task<List<StateHistory>>() {
            @Override
            public List<StateHistory> call() {
                return ManagerDao.getStateHistoryList(userId);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListStateHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveListStateHistory(WorkerStateEvent e, List<StateHistory> list) throws IOException {
        if (list != null) {
            setTableStatus(tableStatus, FXCollections.observableArrayList(list));
        }
        getListTreatmentPlaceHistoryThread();
    }

    private void getListTreatmentPlaceHistoryThread() {
        Task<List<TreatmentPlaceHistory>> dataTask = new Task<List<TreatmentPlaceHistory>>() {
            @Override
            public List<TreatmentPlaceHistory> call() {
                return ManagerDao.getTreatmentPlaceHistoryList(userId);
            }
        };
        dataTask.setOnSucceeded(e -> {
            try {
                resolveListTreatmentPlaceHistory(e, dataTask.getValue());
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });
        TaskExecutor.execute(dataTask);
    }

    public void resolveListTreatmentPlaceHistory(WorkerStateEvent e, List<TreatmentPlaceHistory> list) throws IOException {
        if (list != null) {
            setTableTreat(tablePlace, FXCollections.observableArrayList(list));

        }
    }

    public void setup(ManagedUser user) {
        userId = user.getUserId();
        getManagedUserThread();
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

        currentStatusCol = getTableStatusColumnByName(table, "Trạng thái");
        Callback<TableColumn<StateHistory, String>, TableCell<StateHistory, String>> cellFactory1
                = //
                new Callback<TableColumn<StateHistory, String>, TableCell<StateHistory, String>>() {
            @Override
            public TableCell call(final TableColumn<StateHistory, String> param) {
                final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTableRow() != null) {
                                StateHistory state = (StateHistory) getTableRow().getItem();
                                setText("F" + String.valueOf(ManagerDao.getCurrentStateByTime(state.getUserID(), state.getTime())));
                            }

                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        };
        currentStatusCol.setCellFactory(cellFactory1);

    }

    public void setTableStatus(TableView<StateHistory> table, ObservableList<StateHistory> data) {
        setColumnsStatus(table);
        table.setItems(data);

    }

    public void setColumnsTreat(TableView<TreatmentPlaceHistory> table) {
        datePlaceCol = getTableTreatColumnByName(table, "Ngày");
        datePlaceCol.setCellValueFactory(new PropertyValueFactory<TreatmentPlaceHistory, String>("time"));

        placeCol = getTableTreatByName(table, "Nơi điều trị");
        placeCol.setCellValueFactory(new PropertyValueFactory<TreatmentPlaceHistory, Integer>("name"));
    }

    public void setTableTreat(TableView<TreatmentPlaceHistory> table, ObservableList<TreatmentPlaceHistory> data) {
        setColumnsTreat(table);
        table.setItems(data);

    }
}
