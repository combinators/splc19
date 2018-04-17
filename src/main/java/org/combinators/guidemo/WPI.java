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

public class WPI extends AbstractModule {
    @Override
    public void configure() {
        bind(ProductOptionErrorHandler.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
        bind(ProductSelector.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
    }

    @Provides
    @BranchName
    public String provideBranchTitle() {
        return "Finest Coffee @ WPI";
    }

    @Provides
    @Location(of = Locatable.Logo)
    public URL provideLogoLocation() {
        try {
            return new URL("https://upload.wikimedia.org/wikipedia/en/thumb/1/1b/WPI_logo.png/150px-WPI_logo.png");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new WPI(),
                new DatabaseLocationModule(),
                new ComboboxOrderModule(),
                new DefaultOrderModule(),
                new JSONProductOptionModule()
        );
        CustomerForm form = injector.getInstance(CustomerForm.class);
        EventQueue.invokeLater(form::initComponents);
    }
}
