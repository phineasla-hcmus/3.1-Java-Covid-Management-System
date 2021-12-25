module com.seasidechachacha.client {
    requires java.base;
    
    requires com.jfoenix;
    requires org.apache.logging.log4j;
    requires org.kordamp.bootstrapfx.core;
    
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphicsEmpty;
    
    requires com.seasidechachacha.common;

    opens com.seasidechachacha.client to javafx.fxml;
    opens com.seasidechachacha.client.controllers to javafx.fxml;
    opens com.seasidechachacha.client.models to javafx.base;

    exports com.seasidechachacha.client;
}
