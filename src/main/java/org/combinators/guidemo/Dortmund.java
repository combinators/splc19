package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.combinators.guidemo.concepts.Concepts.BranchName;
import org.combinators.guidemo.concepts.Concepts.Locatable;
import org.combinators.guidemo.concepts.Concepts.Location;
import org.combinators.guidemo.concepts.Concepts.OrderMenu;

import java.awt.*;
import java.net.URL;

public class Dortmund extends AbstractModule {
    @Override
    public void configure() {
        bind(ProductOptionErrorHandler.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
        bind(ProductSelector.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
    }

    @Provides
    @BranchName
    public String provideBranchTitle() {
        return "Finest Coffee @ TU-Dortmund";
    }

    @Provides
    @Location(of = Locatable.Logo)
    public URL provideLogoLocation() {
        try {
            return new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Technische_Universit%C3%A4t_Dortmund_Logo.svg/320px-Technische_Universit%C3%A4t_Dortmund_Logo.svg.png");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new Dortmund(),
                new DatabaseLocationModule(),
                new ComboboxOrderModule(),
                new DefaultOrderModule(),
                new JDBCProductOptionModule()
                );
        CustomerForm form = injector.getInstance(CustomerForm.class);
        EventQueue.invokeLater(form::initComponents);
    }

}
