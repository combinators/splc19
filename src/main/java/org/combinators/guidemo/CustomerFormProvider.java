package org.combinators.guidemo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class CustomerFormProvider implements Provider<CustomerForm> {

    private final String title;
    private final URL logoLocation;
    private final Provider<List<Component>> productSelectionComponents;
    private final Provider<String> defaultOrder;

    @Inject
    public CustomerFormProvider(@Named("branch title") String title,
                                @Named("logo location") URL logoLocation,
                                Provider<List<Component>> productSelectionComponents,
                                @Named("Default Order") Provider<String> defaultOrder) {
        this.title = title;
        this.logoLocation = logoLocation;
        this.productSelectionComponents = productSelectionComponents;
        this.defaultOrder = defaultOrder;
    }

    @Override
    public CustomerForm get() {
        return new CustomerForm(title, logoLocation, productSelectionComponents.get(), defaultOrder.get());
    }
}
