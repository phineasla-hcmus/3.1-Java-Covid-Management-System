module com.seasidechachacha.common {
    requires org.apache.logging.log4j;
    
    requires transitive java.sql;

    exports com.seasidechachacha.common;
    exports com.seasidechachacha.common.payment;
}