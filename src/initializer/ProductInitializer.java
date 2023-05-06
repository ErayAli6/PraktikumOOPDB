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

public class ProductInitializer {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;

    int productId = -1;
    JPanel productUpPanel = new JPanel();
    JPanel productMidPanel = new JPanel();
    JPanel productDownPanel = new JPanel();
    JLabel productNameL = new JLabel("Име на продукт:");
    JLabel productDescL = new JLabel("Описание:");
    JLabel productCategoryL = new JLabel("Категория:");
    JLabel productPriceL = new JLabel("Цена:");
    JTextField productNameTF = new JTextField();
    JTextField productDescTF = new JTextField();
    JTextField productCategoryTF = new JTextField();
    JTextField productPriceTF = new JTextField();
    JTable productTable = new JTable();
    JScrollPane myProductScroll = new JScrollPane(productTable);
    JButton addProductBt = new JButton("Добавяне");
    JButton deleteProductBt = new JButton("Изтриване");
    JButton editProductBt = new JButton("Редактиране");
    JButton searchProductBt = new JButton("Търсене по име");
    JButton refreshProductBt = new JButton("Обнови");

    public void initialize(JPanel panelProduct) {
        panelProduct.setLayout(new GridLayout(3, 1));

        // upPanel------------------------------------------------------
        productUpPanel.setLayout(new GridLayout(4, 2));
        productUpPanel.add(productNameL);
        productUpPanel.add(productNameTF);
        productUpPanel.add(productDescL);
        productUpPanel.add(productDescTF);
        productUpPanel.add(productCategoryL);
        productUpPanel.add(productCategoryTF);
        productUpPanel.add(productPriceL);
        productUpPanel.add(productPriceTF);

        panelProduct.add(productUpPanel);

        // midPanel-----------
        // ------------------------------------------
        productMidPanel.add(addProductBt);
        productMidPanel.add(deleteProductBt);
        productMidPanel.add(editProductBt);
        productMidPanel.add(searchProductBt);
        productMidPanel.add(refreshProductBt);

        panelProduct.add(productMidPanel);

        addProductBt.addActionListener(new AddProductAction());
        deleteProductBt.addActionListener(new DeleteProductAction());
        searchProductBt.addActionListener(new SearchProductAction());
        refreshProductBt.addActionListener(new RefreshProductAction());
        editProductBt.addActionListener(new EditProductAction());

        // downPanel----------------------------------------------------
        myProductScroll.setPreferredSize(new Dimension(450, 150));
        productDownPanel.add(myProductScroll);
        panelProduct.add(productDownPanel);
        refreshProductTable();
        productTable.addMouseListener(new MouseProductAction());
    }

    public void refreshProductTable() {
        conn = DBConnection.getConnection();
        try {
            state = conn.prepareStatement("select * from product");
            result = state.executeQuery();
            productTable.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearProductForm() {
        productNameTF.setText("");
        productDescTF.setText("");
        productCategoryTF.setText("");
        productPriceTF.setText("");
    }

    class AddProductAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO product (product_name, product_desc, product_category, product_price) VALUES(?, ?, ?, ?)";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, productNameTF.getText());
                state.setString(2, productDescTF.getText());
                state.setString(3, productCategoryTF.getText());
                state.setFloat(4, Float.parseFloat(productPriceTF.getText()));
                state.execute();
                refreshProductTable();
                clearProductForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseProductAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = productTable.getSelectedRow();
            productId = Integer.parseInt(productTable.getValueAt(row, 0).toString());
            productNameTF.setText(productTable.getValueAt(row, 1).toString());
            productDescTF.setText(productTable.getValueAt(row, 2).toString());
            productCategoryTF.setText(productTable.getValueAt(row, 3).toString());
            productPriceTF.setText(productTable.getValueAt(row, 4).toString());
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

    class DeleteProductAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "delete from product where product_id=?";

            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, productId);
                state.execute();
                refreshProductTable();
                clearProductForm();
                productId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class EditProductAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "UPDATE product SET PRODUCT_NAME=?, PRODUCT_DESC=?, PRODUCT_CATEGORY=?, PRODUCT_PRICE=? WHERE PRODUCT_ID=?";

            try {
                state = conn.prepareStatement(sql);
                state.setString(1, productNameTF.getText());
                state.setString(2, productDescTF.getText());
                state.setString(3, productCategoryTF.getText());
                state.setFloat(4, Float.parseFloat(productPriceTF.getText()));
                state.setInt(5, productId);
                state.execute();
                refreshProductTable();
                clearProductForm();
                productId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class SearchProductAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "select * from product where product_name=?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, productNameTF.getText());
                result = state.executeQuery();
                productTable.setModel(new MyModel(result));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshProductAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshProductTable();
        }
    }
}
