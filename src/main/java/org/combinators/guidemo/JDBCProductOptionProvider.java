package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JDBCProductOptionProvider implements Provider<List<String>> {
    private final String location;
    private final Provider<ProductOptionErrorHandler> errorHandler;

    @Inject
    public JDBCProductOptionProvider(@Named("jdbc database location") String location,
                                     Provider<ProductOptionErrorHandler> errorHandler) {
        this.location = location;
        this.errorHandler = errorHandler;
    }

    public List<String> get() {
        List<String> options = new ArrayList<>();
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(location, "sa", "");
            ResultSet results = connection.prepareStatement("SELECT name FROM coffee").executeQuery();
            while (results.next()) {
                options.add(results.getString("name"));
            }
        } catch (Exception e) {
            errorHandler.get().handle(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return options;
    }
}
