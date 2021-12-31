package com.seasidechachacha.client.controllers;

import com.seasidechachacha.client.App;
import com.seasidechachacha.client.models.OrderDetail;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewOrderDetailController {

    private static final Logger logger = LogManager.getLogger(ViewOrderDetailController.class);
    @FXML
    private Pagination paginationDetail;

    private List<OrderDetail> dataDetail;

    public int itemsPerPage() {
        return 1;
    }

    public int rowsPerPage() {
        return 10;
    }

    @FXML
    private void initialize() {
        
    }

    public void setup(List<OrderDetail> detail) {
        this.dataDetail = detail;
        if (dataDetail.size() % rowsPerPage() == 0) {
            paginationDetail.setPageCount(dataDetail.size() / rowsPerPage());

        } else {
            paginationDetail.setPageCount(dataDetail.size() / rowsPerPage() + 1);

        }
        paginationDetail.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > dataDetail.size() / rowsPerPage()) {
                    return null;
                } else {
                    return createDetailPage(pageIndex);
                }
            }
        });
    }

    public VBox createDetailPage(int pageIndex) {
        int lastIndex = 0;
        int displace = dataDetail.size() % rowsPerPage();
        if (displace > 0) {
            lastIndex = dataDetail.size() / rowsPerPage();
        } else {
            lastIndex = dataDetail.size() / rowsPerPage() - 1;

        }

        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();

        for (int i = page; i < page + itemsPerPage(); i++) {
            TableView<OrderDetail> table = new TableView<OrderDetail>();

            TableColumn nameCol = new TableColumn("Tên sản phẩm");
            nameCol.setCellValueFactory(
                    new PropertyValueFactory<OrderDetail, String>("packageName"));

            nameCol.setMinWidth(200);

            TableColumn quantityCol = new TableColumn("Số lượng");
            quantityCol.setCellValueFactory(
                    new PropertyValueFactory<OrderDetail, Integer>("quantity"));

            quantityCol.setMinWidth(200);

            TableColumn priceCol = new TableColumn("Đơn giá");
            priceCol.setCellValueFactory(
                    new PropertyValueFactory<OrderDetail, Integer>("price"));

            priceCol.setMinWidth(300);

            table.getColumns().addAll(nameCol, quantityCol, priceCol);
            table.setItems(FXCollections.observableArrayList(dataDetail));
            if (lastIndex == pageIndex) {
                table.setItems(FXCollections.observableArrayList(
                        dataDetail.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        dataDetail.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
            }

            box.getChildren().add(table);
        }
        return box;
    }

    @FXML
    private void goBack() {
        try {
            App.setCurrentPane("pn_all", "view/UserInfo", null);
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }
}
