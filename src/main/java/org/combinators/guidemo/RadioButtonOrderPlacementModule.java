package org.combinators.guidemo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.combinators.guidemo.concepts.Concepts.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RadioButtonOrderPlacementModule extends AbstractModule {
    @Override
    protected void configure() { }

    @Provides
    @ChoiceDialog
    public List<Component> provideRadioButtonOrder(
            @ProductOptions List<String> productOptions,
            @OrderMenu ProductSelector productSelector) {
        ButtonGroup group = new ButtonGroup();
        List<Component> components = new ArrayList<>();
        for (String option : productOptions) {
            JRadioButton optionButton = new JRadioButton(option);
            optionButton.addActionListener(e -> productSelector.select(option));
            group.add(optionButton);
            components.add(optionButton);
        }
        return components;
    }
}