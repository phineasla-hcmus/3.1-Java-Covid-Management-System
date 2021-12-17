package com.seasidechachacha.client;

import com.seasidechachacha.client.controllers.ViewPackageInfoController;
import com.seasidechachacha.client.controllers.ViewPersonalInfoController;
import com.seasidechachacha.client.controllers.ViewTreatmentPlaceInfoController;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.utils.PasswordAuthenticator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import org.kordamp.bootstrapfx.BootstrapFX;
import javafx.application.Platform;
import javafx.scene.control.TableRow;

public class App extends Application {

    private static Scene scene;
    private static ScrollPane pn_all;
    private static String role = "admin";

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/AdminScreen"));
        stage.setScene(scene);
        stage.show();
    }
    
    public static void initializeMainScreen() throws IOException {
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        String initialPane = "";

        if (role.equals("moderator")) {
            setRoot("view/ModeratorScreen");
            initialPane = "view/ViewListUser.fxml";
        } else if (role.equals("admin")) {
            setRoot("view/AdminScreen");
        } else {
            setRoot("view/UserScreen");
        }
        pn_all = (ScrollPane) scene.lookup("#pn_all");

        Pane newLoadedPane = FXMLLoader.load(App.class.getResource(initialPane));
        pn_all.setContent(newLoadedPane);
        pn_all.toFront();
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
            }
            else if (fxml.equals("view/ViewTreatmentPlaceInfo")) {
                TreatmentPlace treat = (TreatmentPlace) tableRow.getItem();
                ViewTreatmentPlaceInfoController controller = fxmlLoader.<ViewTreatmentPlaceInfoController>getController();
                controller.setup(treat);
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

    public static void main(String[] args) {
//         PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
////         String login = pwdAuth.authenticate("1ixrvSfjhPqd".toCharArray(),
////                 "$31$16$VKx6w7TTTyO8H504Ajxk6BOW034fSyZYhuayMVsf2P8")
////                         ? "Welcome"
////                         : "GET OUT";
//        
////        String a=   pwdAuth.hash("123456".toCharArray());
//           
//         System.out.println(a);
        launch();
    }

}
