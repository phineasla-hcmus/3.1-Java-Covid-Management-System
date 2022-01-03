package com.seasidechachacha.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.seasidechachacha.client.controllers.ViewOrderDetailController;
import com.seasidechachacha.client.controllers.ViewPackageInfoController;
import com.seasidechachacha.client.controllers.ViewPersonalInfoController;
import com.seasidechachacha.client.controllers.ViewTreatmentPlaceInfoController;
import com.seasidechachacha.client.database.ManagerDao;
import com.seasidechachacha.client.database.UserDao;
import com.seasidechachacha.client.global.SSLConfig;
import com.seasidechachacha.client.global.TaskExecutor;
import com.seasidechachacha.client.models.Invoice;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.OrderDetail;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.common.DatabaseConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);
    private static Scene scene;
    private static ScrollPane pn_all;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        if (UserDao.isEmpty()) {
            scene = new Scene(loadFXML("view/FirstLogin"));
        } else {
            scene = new Scene(loadFXML("view/Login"));
        }

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        logger.info("Exiting");
        TaskExecutor.shutdown();
        if (!TaskExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
            List<Runnable> droppedTasks = TaskExecutor.shutdownNow();
            logger.warn("Executor did not terminate in the specified time. "
                    + droppedTasks.size()
                    + " tasks will not be executed");
        }
    }

    public static void setCurrentPane(String pane, String fxml, TableRow<Object> tableRow) throws IOException {
        if (pane.equals("pn_all")) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            Pane newLoadedPane = (Pane) fxmlLoader.load();
            if (fxml.equals("view/ViewPersonalInfo")) {
                ManagedUser user = (ManagedUser) tableRow.getItem();
                ViewPersonalInfoController controller = fxmlLoader.<ViewPersonalInfoController>getController();
                controller.setup(user);
            } else if (fxml.equals("view/ViewPackageInfo")) {
                Package pack = (Package) tableRow.getItem();
                ViewPackageInfoController controller = fxmlLoader.<ViewPackageInfoController>getController();
                controller.setup(pack);
            } else if (fxml.equals("view/ViewTreatmentPlaceInfo")) {
                TreatmentPlace treat = (TreatmentPlace) tableRow.getItem();
                ViewTreatmentPlaceInfoController controller = fxmlLoader
                        .<ViewTreatmentPlaceInfoController>getController();
                controller.setup(treat);
            } else if (fxml.equals("view/ViewOrderDetail")) {
                Invoice invoice = (Invoice) tableRow.getItem();
                List<OrderDetail> detail = ManagerDao.getOrderDetailById(invoice.getInvoiceId());
                ViewOrderDetailController controller = fxmlLoader
                        .<ViewOrderDetailController>getController();
                controller.setup(detail);
            }
            pn_all = (ScrollPane) scene.lookup("#pn_all");

            pn_all.setContent(newLoadedPane);

        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void close() {
        Platform.exit();
    }

    // Quick test
    public static void playground() throws Exception {
        // PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
        // String login = pwdAuth.authenticate("1ixrvSfjhPqd".toCharArray(),
        // "$31$16$VKx6w7TTTyO8H504Ajxk6BOW034fSyZYhuayMVsf2P8")
        // ? "Welcome"
        // : "GET OUT";
        // String a = pwdAuth.hash("123456".toCharArray());
        // System.out.println(a);

        // PaymentService ps = new PaymentService();
        // Serializable s = ps.getAccountBalance("userId");
        // UserResponse res = (UserResponse) s;
        // System.out.println(res.getDeposit());
        // InvoiceDao.logInvoice("079932368028");
    }

    public static void main(String[] args) throws Exception {
        try {
            SSLConfig.initialize();
            DatabaseConfig.initialize();
        } catch (NullPointerException e) {
            logger.fatal(e);
            return;
        }
        TaskExecutor.initialize();
        playground();
        launch();
    }
}
