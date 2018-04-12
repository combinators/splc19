package org.combinators.guidemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Provider;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSONProductOptionModule extends AbstractModule {
    @Override
    protected void configure() { }


    @Provides
    public List<String> provideProductOptions(@Named("json database location") String location,
                                              ProductOptionErrorHandler errorHandler) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> options;
        try {
            options = mapper.readValue(new URL(location), mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            options = new ArrayList<>();
            errorHandler.handle(e);
        }
        return options;
    }
}
