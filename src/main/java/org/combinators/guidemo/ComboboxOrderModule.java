package org.combinators.guidemo;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ComboboxOrderModule implements Provider<List<Component>> {
    private final Provider<List<String>> productOptions;
    private final Provider<ProductSelector> productSelector;
    @Inject
    public ComboboxOrderModule(Provider<List<String>> productOptions, Provider<ProductSelector> productSelector) {
        this.productOptions = productOptions;
        this.productSelector = productSelector;
    }

    public List<Component> get() {
        JComboBox<String> optionSelection = new JComboBox<>(productOptions.get().toArray(new String[0]));
        optionSelection.addActionListener(e -> productSelector.get().select((String)optionSelection.getSelectedItem()));
        return Lists.newArrayList(optionSelection);
    }
}
