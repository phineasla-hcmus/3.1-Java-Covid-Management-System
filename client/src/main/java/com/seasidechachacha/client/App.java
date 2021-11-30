package com.seasidechachacha.client;

import com.seasidechachacha.client.controllers.ViewPersonalInfoController;
import com.seasidechachacha.client.utils.PasswordAuthenticator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.seasidechachacha.database.DatabaseConfig;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import org.kordamp.bootstrapfx.BootstrapFX;

public class App extends Application {

    private static Scene scene;
    private static ScrollPane pn_all;
    private static Pane pn_core, pn_xeom, pn_atom, infoPane;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/Main"), 1050, 800);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        pn_all = (ScrollPane) scene.lookup("#pn_all");
        pn_core = (Pane) scene.lookup("#pn_core");
        pn_xeom = (Pane) scene.lookup("#pn_xeom");
        pn_atom = (Pane) scene.lookup("#pn_atom");

        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("view/ViewListUser.fxml"));
        // pn_all.getChildren().add(newLoadedPane);
        // pn_all.toFront();
        pn_all.setContent(newLoadedPane);
        pn_all.toFront();

        // config infoPane
        // infoPane =
        // FXMLLoader.load(getClass().getResource("view/ViewPersonalInfo.fxml"));
        // infoTable = (TableView<UserAccount>) infoPane.lookup("table");
        // setTable(infoTable, data);
        // table = (TableView<UserAccount>) newLoadedPane.lookup("#table");
        // setTable(table, data);
        stage.setScene(scene);
        stage.show();
    }

    public static void setCurrentPane(String pane, String fxml, UserAccount user) throws IOException {
        if (pane.equals("pn_all")) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

            // ScrollPane newLoadedPane = (ScrollPane) fxmlLoader.load();
            Pane newLoadedPane = (Pane) fxmlLoader.load();
            if (fxml.equals("view/ViewPersonalInfo")) {
                ViewPersonalInfoController controller = fxmlLoader.<ViewPersonalInfoController>getController();
                controller.setup(user);
            }
            pn_all.setContent(newLoadedPane);

            // table = (TableView<UserAccount>) newLoadedPane.lookup("#table");
            // setTable(table, data);
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
        PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
        String login = pwdAuth.authenticate("1ixrvSfjhPqd".toCharArray(),
                "$31$16$VKx6w7TTTyO8H504Ajxk6BOW034fSyZYhuayMVsf2P8")
                        ? "Welcome"
                        : "GET OUT";
        System.out.println(login);
        launch();
    }

}
