package initializer;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementInitializer {
    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;

    JPanel statementUpPanel = new JPanel();
    JPanel statementMidPanel = new JPanel();
    JPanel statementDownPanel = new JPanel();
    JLabel sexL = new JLabel("Пол:");
    String[] sexItem = {"Мъж", "Жена"};
    JComboBox<String> sexCombo = new JComboBox<>(sexItem);
    JLabel productComboL = new JLabel("Продукт:");
    JComboBox<String> productCombo = new JComboBox<>();
    JButton checkBt = new JButton("Справка");
    JButton refreshBt = new JButton("Обнови");
    JTable statementTable = new JTable();
    JScrollPane myStatementScroll = new JScrollPane(statementTable);

    public void initialize(JPanel panelStatement) {
        panelStatement.setLayout(new GridLayout(3, 1));

        // upPanel------------------------------------------------------
        statementUpPanel.setLayout(new GridLayout(2, 2));
        statementUpPanel.add(sexL);
        statementUpPanel.add(sexCombo);
        statementUpPanel.add(productComboL);
        statementUpPanel.add(productCombo);

        panelStatement.add(statementUpPanel);

        // midPanel-----------
        // ------------------------------------------
        statementMidPanel.add(checkBt);
        statementMidPanel.add(refreshBt);

        panelStatement.add(statementMidPanel);

        checkBt.addActionListener(new CheckAction());
        refreshBt.addActionListener(new RefreshAction());


        // downPanel----------------------------------------------------
        myStatementScroll.setPreferredSize(new Dimension(450, 150));
        statementDownPanel.add(myStatementScroll);
        panelStatement.add(statementDownPanel);
        refreshProductCombo();
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

    public void refreshStatementTable(ResultSet result) {
        try {
            statementTable.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearProductForm() {
        productCombo.removeAll();
    }

    class CheckAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "SELECT c.* , o.ORDER_QUANTITY " +
                    "FROM CLIENT c " +
                    "JOIN ORDERS o ON c.ID = o.CLIENT_ID " +
                    "JOIN PRODUCT p ON o.PRODUCT_ID = p.PRODUCT_ID " +
                    "WHERE c.SEX = ? AND p.PRODUCT_NAME = ?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, sexCombo.getSelectedItem().toString());
                state.setString(2, getSelectedProductName());
                ResultSet result = state.executeQuery();
                refreshStatementTable(result);
                clearProductForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshProductCombo();
        }
    }

    private String getSelectedProductName() {
        String selectedClient = (String) productCombo.getSelectedItem();
        assert selectedClient != null;
        return selectedClient.split("\\.")[1];
    }
}
