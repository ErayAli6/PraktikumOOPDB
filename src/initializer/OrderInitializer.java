package initializer;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderInitializer {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;

    int orderId = -1;
    JPanel orderUpPanel = new JPanel();
    JPanel orderMidPanel = new JPanel();
    JPanel orderDownPanel = new JPanel();
    JLabel clientComboL = new JLabel("Клиент:");
    JComboBox<String> clientCombo = new JComboBox<>();
    JLabel productComboL = new JLabel("Продукт:");
    JComboBox<String> productCombo = new JComboBox<>();
    JLabel orderQuantityL = new JLabel("Количество:");
    JTextField orderQuantityTF = new JTextField();
    JButton addOrderBt = new JButton("Добавяне");
    JButton deleteOrderBt = new JButton("Изтриване");
    JButton editOrderBt = new JButton("Редактиране");
    JButton searchOrderBt = new JButton("Търсене по клиент");
    JButton refreshOrderBt = new JButton("Обнови");

    JTable orderTable = new JTable();
    JScrollPane myOrderScroll = new JScrollPane(orderTable);

    public void initialize(JPanel panelOrder) {
        panelOrder.setLayout(new GridLayout(3, 1));

        // upPanel------------------------------------------------------
        orderUpPanel.setLayout(new GridLayout(3, 2));
        orderUpPanel.add(clientComboL);
        orderUpPanel.add(clientCombo);
        orderUpPanel.add(productComboL);
        orderUpPanel.add(productCombo);
        orderUpPanel.add(orderQuantityL);
        orderUpPanel.add(orderQuantityTF);

        panelOrder.add(orderUpPanel);

        // midPanel-----------
        // ------------------------------------------
        orderMidPanel.add(addOrderBt);
        orderMidPanel.add(deleteOrderBt);
        orderMidPanel.add(editOrderBt);
        orderMidPanel.add(searchOrderBt);
        orderMidPanel.add(refreshOrderBt);

        panelOrder.add(orderMidPanel);

        addOrderBt.addActionListener(new AddOrderAction());
        deleteOrderBt.addActionListener(new DeleteOrderAction());
        searchOrderBt.addActionListener(new SearchOrderAction());
        refreshOrderBt.addActionListener(new RefreshOrderAction());
        editOrderBt.addActionListener(new EditOrderAction());

        // downPanel----------------------------------------------------
        myOrderScroll.setPreferredSize(new Dimension(450, 150));
        orderDownPanel.add(myOrderScroll);
        panelOrder.add(orderDownPanel);
        refreshOrderTable();
        orderTable.addMouseListener(new MouseOrderAction());
        refreshClientCombo();
        refreshProductCombo();
    }

    public void refreshClientCombo() {
        clientCombo.removeAllItems();
        String sql = "select id, fname, lname from client";
        conn = DBConnection.getConnection();
        String item = "";
        try {
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            while (result.next()) {
                item = result.getObject(1).toString() + "." + result.getObject(2).toString() + " " + result.getObject(3).toString();
                clientCombo.addItem(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshProductCombo() {
        productCombo.removeAllItems();
        String sql = "select product_id, product_name from product";
        conn = DBConnection.getConnection();
        String item = "";
        try {
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            while (result.next()) {
                item = result.getObject(1).toString() + "." + result.getObject(2).toString();
                productCombo.addItem(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshOrderTable() {
        conn = DBConnection.getConnection();
        try {
            String sql = "SELECT o.order_id, o.client_id, CONCAT(c.fname, ' ', c.lname) AS client_name, o.product_id, p.product_name, o.order_quantity " +
                    "FROM orders o " +
                    "JOIN client c ON o.client_id = c.id " +
                    "JOIN product p ON o.product_id = p.product_id";
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            orderTable.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearOrderForm() {
        clientCombo.removeAll();
        productCombo.removeAll();
        orderQuantityTF.setText("");
    }

    class AddOrderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO orders (CLIENT_ID, PRODUCT_ID, ORDER_QUANTITY) VALUES (?, ?, ?);";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, getSelectedClientId());
                state.setInt(2, getSelectedProductId());
                state.setInt(3, Integer.parseInt(orderQuantityTF.getText()));
                state.execute();
                refreshOrderTable();
                refreshClientCombo();
                refreshProductCombo();
                clearOrderForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseOrderAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = orderTable.getSelectedRow();
            orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString());
            int clientId = Integer.parseInt(orderTable.getValueAt(row, 1).toString());
            int productId = Integer.parseInt(orderTable.getValueAt(row, 3).toString());
            orderQuantityTF.setText(orderTable.getValueAt(row, 5).toString());
            clientCombo.setSelectedItem(getClientNameById(clientId));
            productCombo.setSelectedItem(getProductNameById(productId));
        }

        private String getClientNameById(int clientId) {
            String sql = "SELECT fname, lname FROM client WHERE id=?";
            conn = DBConnection.getConnection();
            String name = "";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, clientId);
                result = state.executeQuery();
                if (result.next()) {
                    name = result.getString("fname") + " " + result.getString("lname");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return name;
        }

        private String getProductNameById(int productId) {
            String sql = "SELECT product_name FROM product WHERE product_id=?";
            conn = DBConnection.getConnection();
            String name = "";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, productId);
                result = state.executeQuery();
                if (result.next()) {
                    name = result.getString("product_name");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return name;
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class DeleteOrderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "delete from orders where order_id=?";

            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, orderId);
                state.execute();
                refreshOrderTable();
                refreshProductCombo();
                refreshClientCombo();
                clearOrderForm();
                orderId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class EditOrderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET CLIENT_ID=?, PRODUCT_ID=?, ORDER_QUANTITY=? WHERE ORDER_ID=?";

            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, getSelectedClientId());
                state.setInt(2, getSelectedProductId());
                state.setInt(3, Integer.parseInt(orderQuantityTF.getText()));
                state.setInt(4, orderId);
                state.execute();
                refreshOrderTable();
                refreshProductCombo();
                refreshClientCombo();
                clearOrderForm();
                orderId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class SearchOrderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "select * from orders where client_id=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, getSelectedClientId());
                result = state.executeQuery();
                orderTable.setModel(new MyModel(result));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshOrderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshClientCombo();
            refreshProductCombo();
            refreshOrderTable();
        }
    }

    private int getSelectedClientId() {
        String selectedClient = (String) clientCombo.getSelectedItem();
        assert selectedClient != null;
        return Integer.parseInt(selectedClient.split("\\.")[0]);
    }

    private int getSelectedProductId() {
        String selectedClient = (String) productCombo.getSelectedItem();
        assert selectedClient != null;
        return Integer.parseInt(selectedClient.split("\\.")[0]);
    }
}
