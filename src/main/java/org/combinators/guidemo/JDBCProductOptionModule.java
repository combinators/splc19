package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import javax.inject.Named;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JDBCProductOptionModule extends AbstractModule {
    @Override
    public void configure() {}


    @Provides
    public List<String> provideProductOptions(@Named("jdbc database location") String location,
                                              ProductOptionErrorHandler errorHandler) {
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
}
