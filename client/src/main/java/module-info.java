module com.seasidechachacha.client {
    requires com.seasidechachacha.database;

    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.seasidechachacha.client to javafx.fxml;

    exports com.seasidechachacha.client;
}
