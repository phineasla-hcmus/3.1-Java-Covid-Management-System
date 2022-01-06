package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.database.AdminDao;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.global.Session;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.utils.Alert;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewListTreatmentPlaceController {

    private static final Logger logger = LogManager.getLogger(ViewListUserController.class);
    private static List<TreatmentPlace> data;

    @FXML
    private Button btnAdd;

    @FXML
    private Pagination pagination;

    private AdminDao admin = new AdminDao(Session.getUser().getUserId());

    @FXML
    private void initialize() {
        getListTreatmentPlaceThread();

        btnAdd.setOnAction(event -> {
            try {
                App.setCurrentPane("pn_all", "view/AddNewTreatmentPlace", null);
            } catch (IOException ex) {
                logger.fatal(ex);
            }
        });

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
        data = list;
        if (data.size() % rowsPerPage() == 0) {
            pagination.setPageCount(data.size() / rowsPerPage());

        } else {
            pagination.setPageCount(data.size() / rowsPerPage() + 1);

        }
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > data.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });
    }

    public VBox createPage(int pageIndex) {
        int lastIndex = 0;
        int displace = data.size() % rowsPerPage();
        lastIndex = data.size() / rowsPerPage();
        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<TreatmentPlace> table = new TableView<TreatmentPlace>();
            TableColumn numCol = new TableColumn("ID");
            numCol.setCellValueFactory(
                    new PropertyValueFactory<TreatmentPlace, String>("treatID"));

            numCol.setMinWidth(20);

            TableColumn nameCol = new TableColumn("Tên địa điểm");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<TreatmentPlace, String>("name"));

            nameCol.setMinWidth(160);

            TableColumn addrCol = new TableColumn("Địa chỉ");
            addrCol.setCellValueFactory(
                    new PropertyValueFactory<TreatmentPlace, String>("address"));

            addrCol.setMinWidth(190);

            TableColumn capacityCol = new TableColumn("Sức chứa");
            capacityCol.setCellValueFactory(
                    new PropertyValueFactory<TreatmentPlace, String>("capacity"));

//            capacityCol.setMinWidth(160);
            TableColumn currentReceptionCol = new TableColumn("Số lượng tiếp nhận hiện tại");
            currentReceptionCol.setCellValueFactory(
                    new PropertyValueFactory<TreatmentPlace, String>("currentReception"));

            currentReceptionCol.setMinWidth(180);

            TableColumn actionCol = new TableColumn("");
            actionCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<TreatmentPlace, String>, TableCell<TreatmentPlace, String>> cellFactory
                    = //
                    new Callback<TableColumn<TreatmentPlace, String>, TableCell<TreatmentPlace, String>>() {
                @Override
                public TableCell call(final TableColumn<TreatmentPlace, String> param) {
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
                                        App.setCurrentPane("pn_all", "view/ViewTreatmentPlaceInfo", getTableRow());
                                    } catch (IOException ex) {
                                        logger.fatal(ex);
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

            TableColumn deleteCol = new TableColumn();
            deleteCol.setCellValueFactory(new PropertyValueFactory<>(""));
            Callback<TableColumn<TreatmentPlace, String>, TableCell<TreatmentPlace, String>> cellFactory2
                    = //
                    new Callback<TableColumn<TreatmentPlace, String>, TableCell<TreatmentPlace, String>>() {
                @Override
                public TableCell call(final TableColumn<TreatmentPlace, String> param) {
                    final TableCell<TreatmentPlace, String> cell = new TableCell<TreatmentPlace, String>() {
                        final Button btn = new Button("Xoá");

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btn.setOnAction(event -> {
                                    TreatmentPlace treat = getTableRow().getItem();
                                    int treatID = treat.getTreatID();
                                    if (admin.deleteTreatmentPlace(treatID)) {
                                        Alert.showAlert(AlertType.INFORMATION, "Quản lý địa điểm điều trị/cách ly", "Xoá địa điểm điều trị/cách ly thành công!");
                                        getListTreatmentPlaceThread();
                                    } else {
                                        Alert.showAlert(AlertType.WARNING, "Quản lý địa điểm điều trị/cách ly", "Không thể xoá địa điểm điều trị/cách ly này!");
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

            deleteCol.setCellFactory(cellFactory2);

            table.getColumns().addAll(numCol, nameCol, addrCol, capacityCol, currentReceptionCol, actionCol, deleteCol);
            table.setItems(FXCollections.observableArrayList(data));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(data.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 5;
    }

}
