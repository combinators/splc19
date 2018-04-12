package org.combinators.guidemo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class CustomerForm extends JFrame implements ProductSelector, ProductOptionErrorHandler {

    private JButton orderButton;

    private String selectedOrder;

    private List<Component> productSelectionComponents;

    private String title;

    private URL logoLocation;

    private String defaultOrder;

    public CustomerForm(String title,
                        URL logoLocation,
                        List<Component> productSelectionComponents,
                        String defaultOrder) {
        super();
        this.title = title;
        this.logoLocation = logoLocation;
        this.productSelectionComponents = productSelectionComponents;
        this.defaultOrder = defaultOrder;

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.TRAILING);
        this.setLayout(layout);
        initComponents();
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void initComponents() {
        for (Component component : productSelectionComponents) {
            this.add(component);
        }
        try {
            this.add(new JLabel(new ImageIcon(logoLocation)));
        } catch (Exception e) {
        }
        this.setTitle(title);
        selectedOrder = defaultOrder;

        orderButton = new JButton();
        orderButton.setText("Order");
        orderButton.addActionListener(e -> JOptionPane.showMessageDialog(orderButton, String.format("You ordered: %s", selectedOrder)));
        this.add(orderButton);
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
