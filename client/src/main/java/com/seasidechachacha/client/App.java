package com.seasidechachacha.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.seasidechachacha.database.DatabaseConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import org.kordamp.bootstrapfx.BootstrapFX;

public class App extends Application {

    private static Scene scene;
    private static Pane pn_all, pn_core, pn_xeom, pn_atom, infoPane;
    private static TableView<UserAccount> table, infoTable;
    private static TableColumn numberCol, fullNameCol, birthYearCol, addressCol, statusCol, actionCol;

    private static final ObservableList<UserAccount> data
            = FXCollections.observableArrayList(
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2"),
                    new UserAccount("Nguyễn Văn A", "1950", "abc", "F2")
            );

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/Main"), 1050, 800);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        pn_all = (Pane) scene.lookup("#pn_all");
        pn_core = (Pane) scene.lookup("#pn_core");
        pn_xeom = (Pane) scene.lookup("#pn_xeom");
        pn_atom = (Pane) scene.lookup("#pn_atom");

        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("view/ViewListUser.fxml"));
        pn_all.getChildren().add(newLoadedPane);
        pn_all.toFront();
        
        // config infoPane
        infoPane = FXMLLoader.load(getClass().getResource("view/ViewPersonalInfo.fxml"));
//        infoTable = (TableView<UserAccount>) infoPane.lookup("table");
//        setTable(infoTable, data);

        table = (TableView<UserAccount>) newLoadedPane.lookup("#table");
        setTable(table, data);

        stage.setScene(scene);
        stage.show();
    }

    private static <T> TableColumn<T, ?> getTableColumnByName(TableView<T> tableView, String name) {
        for (TableColumn<T, ?> col : tableView.getColumns()) {
            if (col.getText().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public static void setColumns(TableView<UserAccount> table, TableColumn numberCol, TableColumn fullNameCol, TableColumn birthYearCol, TableColumn addressCol, TableColumn statusCol, TableColumn actionCol) {
        numberCol = getTableColumnByName(table, "STT");
        numberCol.setCellValueFactory(new Callback<CellDataFeatures<UserAccount, UserAccount>, ObservableValue<UserAccount>>() {
            @Override
            public ObservableValue<UserAccount> call(CellDataFeatures<UserAccount, UserAccount> p) {
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

    public static void setTable(TableView<UserAccount> table, ObservableList<UserAccount> data) {
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
                        btn.getStyleClass().setAll("btn", "btn-info");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                pn_all.getChildren().clear();
                                pn_all.getChildren().add(infoPane);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        actionCol.setCellFactory(cellFactory);
//        if (actionCol != null)

        table.setItems(data);

    }

    public static void setCurrentPane(String pane) throws IOException {
        if (pane.equals("pn_all")) {
            Pane newLoadedPane = (Pane) loadFXML("view/ViewListUser");
            pn_all.getChildren().clear();
            pn_all.getChildren().add(newLoadedPane);

            table = (TableView<UserAccount>) newLoadedPane.lookup("#table");
            setTable(table, data);
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.out.println(DatabaseConfig.getUrl());
        launch();
    }

}
