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

public class ClientInitializer {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;

    int clientId = -1;

    JPanel clientUpPanel = new JPanel();
    JPanel clientMidPanel = new JPanel();
    JPanel clientDownPanel = new JPanel();

    JLabel fnameL = new JLabel("Име:");
    JLabel lnameL = new JLabel("Фамилия:");
    JLabel sexL = new JLabel("Пол:");
    JLabel ageL = new JLabel("Години:");
    JLabel townL = new JLabel("Град:");
    JLabel addressL = new JLabel("Адрес:");
    JLabel postalCodeL = new JLabel("Пощенски код:");

    JTextField fnameTF = new JTextField();
    JTextField lnameTF = new JTextField();
    JTextField ageTF = new JTextField();
    JTextField townTF = new JTextField();
    JTextField postalCodeTF = new JTextField();
    JTextField addressTF = new JTextField();

    String[] sexItem = {"Мъж", "Жена"};
    JComboBox<String> sexCombo = new JComboBox<>(sexItem);

    JTable clientTable = new JTable();
    JScrollPane myClientScroll = new JScrollPane(clientTable);
    JButton addClientBt = new JButton("Добавяне");
    JButton deleteClientBt = new JButton("Изтриване");
    JButton editClientBt = new JButton("Редактиране");
    JButton searchClientBt = new JButton("Търсене по фамилия");
    JButton refreshClientBt = new JButton("Обнови");

    public void initialize(JPanel panelClient) {
        panelClient.setLayout(new GridLayout(3, 1));

        // upPanel------------------------------------------------------
        clientUpPanel.setLayout(new GridLayout(7, 2));
        clientUpPanel.add(fnameL);
        clientUpPanel.add(fnameTF);
        clientUpPanel.add(lnameL);
        clientUpPanel.add(lnameTF);
        clientUpPanel.add(sexL);
        clientUpPanel.add(sexCombo);
        clientUpPanel.add(ageL);
        clientUpPanel.add(ageTF);
        clientUpPanel.add(townL);
        clientUpPanel.add(townTF);
        clientUpPanel.add(addressL);
        clientUpPanel.add(addressTF);
        clientUpPanel.add(postalCodeL);
        clientUpPanel.add(postalCodeTF);

        panelClient.add(clientUpPanel);

        // midPanel-----------
        // ------------------------------------------
        clientMidPanel.add(addClientBt);
        clientMidPanel.add(deleteClientBt);
        clientMidPanel.add(editClientBt);
        clientMidPanel.add(searchClientBt);
        clientMidPanel.add(refreshClientBt);

        panelClient.add(clientMidPanel);

        addClientBt.addActionListener(new AddClientAction());
        deleteClientBt.addActionListener(new DeleteClientAction());
        searchClientBt.addActionListener(new SearchClientAction());
        refreshClientBt.addActionListener(new RefreshClientAction());
        editClientBt.addActionListener(new EditClientAction());

        // downPanel----------------------------------------------------
        myClientScroll.setPreferredSize(new Dimension(450, 150));
        clientDownPanel.add(myClientScroll);
        panelClient.add(clientDownPanel);
        refreshClientTable();
        clientTable.addMouseListener(new MouseClientAction());
    }


    public void refreshClientTable() {
        conn = DBConnection.getConnection();
        try {
            state = conn.prepareStatement("select * from client");
            result = state.executeQuery();
            clientTable.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearClientForm() {
        fnameTF.setText("");
        lnameTF.setText("");
        ageTF.setText("");
        townTF.setText("");
        addressTF.setText("");
        postalCodeTF.setText("");
    }

    class AddClientAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "INSERT into client(fname, lname, sex, age, town, address, postal_code) VALUES(?,?,?,?,?,?,?)";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, fnameTF.getText());
                state.setString(2, lnameTF.getText());
                state.setString(3, sexCombo.getSelectedItem().toString());
                state.setInt(4, Integer.parseInt(ageTF.getText()));
                state.setString(5, townTF.getText());
                state.setString(6, addressTF.getText());
                state.setString(7, postalCodeTF.getText());
                state.execute();
                refreshClientTable();
                clearClientForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseClientAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = clientTable.getSelectedRow();
            clientId = Integer.parseInt(clientTable.getValueAt(row, 0).toString());
            fnameTF.setText(clientTable.getValueAt(row, 1).toString());
            lnameTF.setText(clientTable.getValueAt(row, 2).toString());
            ageTF.setText(clientTable.getValueAt(row, 4).toString());
            townTF.setText(clientTable.getValueAt(row, 5).toString());
            addressTF.setText(clientTable.getValueAt(row, 6).toString());
            postalCodeTF.setText(clientTable.getValueAt(row, 7).toString());
            if (clientTable.getValueAt(row, 3).toString().equalsIgnoreCase("Мъж")) {
                sexCombo.setSelectedIndex(0);
            } else {
                sexCombo.setSelectedIndex(1);
            }
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

    class DeleteClientAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "delete from client where id=?";

            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, clientId);
                state.execute();
                refreshClientTable();
                clearClientForm();
                clientId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class EditClientAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "update client set fname = ?, lname = ?, sex = ?, age = ?, town = ?, address = ?, postal_code = ? where id = ? ";

            try {
                state = conn.prepareStatement(sql);
                state.setString(1, fnameTF.getText());
                state.setString(2, lnameTF.getText());
                state.setString(3, sexCombo.getSelectedItem().toString());
                state.setInt(4, Integer.parseInt(ageTF.getText()));
                state.setString(5, townTF.getText());
                state.setString(6, addressTF.getText());
                state.setString(7, postalCodeTF.getText());
                state.setInt(8, clientId);
                state.execute();
                refreshClientTable();
                clearClientForm();
                clientId = -1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class SearchClientAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DBConnection.getConnection();
            String sql = "select * from client where lname=?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1, lnameTF.getText());
                result = state.executeQuery();
                clientTable.setModel(new MyModel(result));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshClientAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshClientTable();
        }
    }
}
