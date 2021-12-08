module com.seasidechachacha.client {
    requires java.base;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.kordamp.bootstrapfx.core;
    requires com.jfoenix;
//    requires com.seasidechachacha.database;

    opens com.seasidechachacha.client to javafx.fxml;
    opens com.seasidechachacha.client.controllers to javafx.fxml;
    opens com.seasidechachacha.client.models to javafx.base;

    exports com.seasidechachacha.client;
    requires javafx.graphicsEmpty;
}
