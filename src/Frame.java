import initializer.ClientInitializer;
import initializer.OrderInitializer;
import initializer.ProductInitializer;
import initializer.StatementInitializer;

import javax.swing.*;

public class Frame extends JFrame {

    JPanel panelClient = new JPanel();
    JPanel panelProduct = new JPanel();
    JPanel panelOrder = new JPanel();
    JPanel panelStatement = new JPanel();

    JTabbedPane tab = new JTabbedPane();

    ClientInitializer clientInitializer = new ClientInitializer();
    ProductInitializer productInitializer = new ProductInitializer();
    OrderInitializer orderInitializer = new OrderInitializer();
    StatementInitializer statementInitializer = new StatementInitializer();

    public Frame() {
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        clientInitializer.initialize(panelClient);
        productInitializer.initialize(panelProduct);
        orderInitializer.initialize(panelOrder);
        statementInitializer.initialize(panelStatement);

        tab.add(panelClient, "Клиенти");
        tab.add(panelProduct, "Продукти");
        tab.add(panelOrder, "Поръчка");
        tab.add(panelStatement, "Справка по пол и продукт");

        this.add(tab);

        this.setVisible(true);
    }
}
