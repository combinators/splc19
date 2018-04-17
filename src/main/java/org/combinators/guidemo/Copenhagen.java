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

public class Copenhagen extends AbstractModule {
    @Override
    public void configure() {
        bind(ProductOptionErrorHandler.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
        bind(ProductSelector.class).annotatedWith(OrderMenu.class).to(CustomerForm.class);
    }

    @Provides
    @BranchName
    public String provideBranchTitle() {
        return "Finest Coffee @ DIKU";
    }

    @Provides
    @Location(of = Locatable.Logo)
    public URL provideLogoLocation() {
        try {
            return new URL("https://upload.wikimedia.org/wikipedia/en/thumb/0/0f/University_of_Copenhagen_Seal.svg/174px-University_of_Copenhagen_Seal.svg.png");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new Copenhagen(),
                new DatabaseLocationModule(),
                new RadioButtonOrderPlacementModule(),
                new DefaultOrderModule(),
                new JSONProductOptionModule()
        );
        CustomerForm form = injector.getInstance(CustomerForm.class);
        EventQueue.invokeLater(form::initComponents);
    }
}
