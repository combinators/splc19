package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.combinators.guidemo.concepts.Concepts.*;

import javax.inject.Named;
import java.util.List;

public class DefaultOrderModule extends AbstractModule {
    @Override
    public void configure() {
    }

    @Provides
    @DefaultOrder
    public String provideDefaultOrder(@ProductOptions List<String> productOptions) {
        return (productOptions.isEmpty() ? "" : productOptions.get(0));
    }
}
