package org.combinators.guidemo;

import com.google.inject.*;
import com.google.inject.name.Names;

import javax.inject.Named;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class Dortmund extends AbstractModule {
    @Override
    public void configure() {
        bind(CustomerForm.class).toProvider(CustomerFormProvider.class);
        bind(ProductOptionErrorHandler.class).toProvider(CustomerFormProvider.class);
        bind(ProductSelector.class).toProvider(CustomerFormProvider.class);
        bind(new TypeLiteral<List<String>>() {}).toProvider(JDBCProductOptionProvider.class);
        bind(new TypeLiteral<List<Component>>() {}).toProvider(ComboboxOrderModule.class);
        bind(String.class).annotatedWith(Names.named("Default Order")).toProvider(DefaultOrderProvider.class);
    }

    @Provides
    @Named("branch title")
    public String provideBranchTitle() {
        return "Finest Coffee @ TU-Dortmund";
    }

    @Provides
    @Named("logo location")
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
                new DatabaseLocationModule());
        CustomerForm form = injector.getInstance(CustomerForm.class);
    }

}
