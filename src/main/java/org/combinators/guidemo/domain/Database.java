package org.combinators.guidemo.domain;

import java.net.URL;

public class Database {
    private DatabaseType databaseType;
    private URL databaseLocation;

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public URL getDatabaseLocation() {
        return databaseLocation;
    }

    public void setDatabaseLocation(URL databaseLocation) {
        this.databaseLocation = databaseLocation;
    }
}
