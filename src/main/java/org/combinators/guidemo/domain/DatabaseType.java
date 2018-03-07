package org.combinators.guidemo.domain;

import java.net.URL;

public enum DatabaseType {
    RestJSON("http://localhost:9000/coffeebar/json/productoptions"),
    JDBC("jdbc:h2:tcp://localhost/mem:coffee");

    public final String defaultLocation;

    DatabaseType(String defaultLocation) {
        this.defaultLocation = defaultLocation;
    }
}
