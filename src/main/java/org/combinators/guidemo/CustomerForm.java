package org.combinators.guidemo;

import org.combinators.guidemo.concepts.Concepts.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class CustomerForm extends JFrame implements ProductSelector, ProductOptionErrorHandler {

    private JButton orderButton;

    private String selectedOrder;

    private final Provider<List<Component>> productSelectionComponents;

    private final String title;

    private final URL logoLocation;

    private final Provider<String> defaultOrder;

    @Inject
    public CustomerForm(@BranchName String title,
                        @Location(of = Locatable.Logo) URL logoLocation,
                        @ChoiceDialog Provider<List<Component>> productSelectionComponents,
                        @DefaultOrder Provider<String> defaultOrder) {
        super();
        this.title = title;
        this.logoLocation = logoLocation;
        this.productSelectionComponents = productSelectionComponents;
        this.defaultOrder = defaultOrder;

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.TRAILING);
        this.setLayout(layout);
    }

    public void initComponents() {
        for (Component component : productSelectionComponents.get()) {
            this.add(component);
        }
        try {
            this.add(new JLabel(new ImageIcon(logoLocation)));
        } catch (Exception e) {
        }
        this.setTitle(title);
        selectedOrder = defaultOrder.get();

        orderButton = new JButton();
        orderButton.setText("Order");
        orderButton.addActionListener(e -> JOptionPane.showMessageDialog(orderButton, String.format("You ordered: %s", selectedOrder)));
        this.add(orderButton);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void select(String product) {
        selectedOrder = product;
    }

    @Override
    public void handle(Exception e) {
        JOptionPane.showMessageDialog(this, String.format("Could not load options: %s", e.getMessage()));
    }
}
