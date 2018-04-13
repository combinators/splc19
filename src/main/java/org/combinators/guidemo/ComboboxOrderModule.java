package org.combinators.guidemo;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ComboboxOrderModule extends AbstractModule {
    @Override
    public void configure() {}

    @Provides
    public List<Component> provideProductSelectionComponents(List<String> productOptions, ProductSelector productSelector) {
        JComboBox<String> optionSelection = new JComboBox<>(productOptions.toArray(new String[0]));
        optionSelection.addActionListener(e -> productSelector.select((String)optionSelection.getSelectedItem()));
        return Lists.newArrayList(optionSelection);
    }
}
