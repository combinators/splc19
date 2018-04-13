package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import java.util.List;

public class DefaultOrderModule extends AbstractModule {
    @Override
    public void configure() {
    }

    @Provides
    @Named("Default Order")
    public String provideDefaultOrder(List<String> productOptions) {
        return (productOptions.isEmpty() ? "" : productOptions.get(0));
    }
}
