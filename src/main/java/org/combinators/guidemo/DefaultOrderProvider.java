package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.List;

public class DefaultOrderProvider implements Provider<String> {
    private final Provider<List<String>> productOptions;
    @Inject
    public DefaultOrderProvider(Provider<List<String>> productOptions) {
        this.productOptions = productOptions;
    }

    @Named("Default Order")
    public String get() {
        List<String> options = productOptions.get();
        return (options.isEmpty() ? "" : options.get(0));
    }
}
