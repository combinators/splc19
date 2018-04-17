package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.combinators.guidemo.concepts.Concepts.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JDBCProductOptionModule extends AbstractModule {
    @Override
    public void configure() {}


    @Provides
    @ProductOptions
    public List<String> provideProductOptions(
            @Location(of = Locatable.Database) String location,
            @OrderMenu ProductOptionErrorHandler errorHandler) {
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
            errorHandler.handle(e);
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

    @Provides
    @Location(of = Locatable.Database)
    public String provideJDBCDatabaseLocation() {
        return "jdbc:h2:tcp://localhost/mem:coffee";
    }
}
