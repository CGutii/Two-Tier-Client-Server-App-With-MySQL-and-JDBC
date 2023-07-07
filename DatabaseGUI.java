/*
Name: Cristian Gutierrez
Course: CNT 4714 Summer 2023
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 2, 2023
Class: Enterprise Computing CNT 4714-23Summer C001
*/

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class DatabaseGUI extends JFrame {
    private JComboBox<String> dbUrlField, userPropField;
    private JTextField usernameField, passwordField;
    private JTextArea sqlCmdField;
    private JTable resultTable;
    private JButton connectButton, clearCmdButton, execCmdButton, clearResultButton;
    private JLabel connectionStatus;
    private DatabaseManager dbManager;

    public DatabaseGUI() {
        dbManager = new DatabaseManager();
        initializeGUI();

        connectButton.addActionListener(e -> connectToDatabase());
        clearCmdButton.addActionListener(e -> clearSQLCommand());
        execCmdButton.addActionListener(e -> {
			try {
				executeSQLCommand();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        clearResultButton.addActionListener(e -> clearResultWindow());
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding

        // Top left elements
        JPanel topLeft = new JPanel();
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components in order (with helper function to reduce redundancy)
        topLeft.add(createInputPair("DB URL Properties", dbUrlField = new JComboBox<>(new String[]{"bikedb.properties", "project2.properties", "newdb.properties", "modeldb.properties"})));
        topLeft.add(createInputPair("User Properties", userPropField = new JComboBox<>(new String[]{"root.properties", "client.properties", "newuser.properties", "mysteryuser.properties", "userX.properties"})));
        topLeft.add(createInputPair("Username", usernameField = new JTextField()));
        topLeft.add(createInputPair("Password", passwordField = new JPasswordField()));

        // Connection button and status label
        connectButton = new JButton("Connect to Database");
        connectButton.setBackground(Color.RED);
        connectButton.setForeground(Color.WHITE);
        connectionStatus = new JLabel("");
        connectionStatus.setForeground(Color.RED);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(connectButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // create a vertical space
        buttonPanel.add(connectionStatus);
        topLeft.add(buttonPanel);

        // Top right elements
        JPanel topRight = new JPanel(new BorderLayout());
        topRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // SQL Command input
        sqlCmdField = new JTextArea();
        sqlCmdField.setLineWrap(true);
        sqlCmdField.setWrapStyleWord(true);
        JScrollPane sqlCmdScrollPane = new JScrollPane(sqlCmdField);

        topRight.add(new JLabel("Enter an SQL Command"), BorderLayout.NORTH);
        topRight.add(sqlCmdScrollPane, BorderLayout.CENTER);

        // SQL Command buttons
        JPanel buttonPanelTopRight = new JPanel();
        clearCmdButton = new JButton("Clear SQL Command");
        execCmdButton = new JButton("Execute SQL Command");
        buttonPanelTopRight.add(clearCmdButton);
        buttonPanelTopRight.add(execCmdButton);
        topRight.add(buttonPanelTopRight, BorderLayout.SOUTH);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topLeft, BorderLayout.WEST);
        topPanel.add(topRight, BorderLayout.CENTER);

        // Bottom elements
        JLabel resultLabel = new JLabel("SQL Execution Result Window");
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        clearResultButton = new JButton("Clear Results");

        // Organize in bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultLabel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(clearResultButton, BorderLayout.SOUTH);

        // Add to main panel
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(1000, 600));

        setTitle("Database GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    // Helper function for creating label + input component pairs
    private JPanel createInputPair(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        return panel;
    }

    private void connectToDatabase() {
        String dbPropFileName = (String) dbUrlField.getSelectedItem();
        String userPropFileName = (String) userPropField.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(((JPasswordField) passwordField).getPassword());

        try {
            dbManager.connect(dbPropFileName, userPropFileName, username, password);
            connectionStatus.setText("Connected to " + dbPropFileName);
            connectionStatus.setForeground(Color.GREEN);
        } catch (Exception ex) {
            connectionStatus.setText("Failed to connect to " + dbPropFileName + ". Reason: " + ex.getMessage());
            connectionStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + ex.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void clearSQLCommand() {
        sqlCmdField.setText("");
    }

    private void executeSQLCommand() throws IOException {
        String sqlCmd = sqlCmdField.getText();
        if (!sqlCmd.isEmpty()) {
            try {
                // Create a statement
                Statement stmt = dbManager.getConnection().createStatement();
                boolean isResultSet = stmt.execute(sqlCmd);

                // If command was a query
                if (isResultSet) {
                    ResultSet resultSet = stmt.getResultSet();
                    resultTable.setModel(buildTableModel(resultSet));
                    JOptionPane.showMessageDialog(this, "Query executed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update operationslog for query
                    dbManager.updateOperationsLog(true);
                } 
                // If command was an update
                else {
                    int updateCount = stmt.getUpdateCount();
                    if (updateCount >= 0) {
                        JOptionPane.showMessageDialog(this, "Command executed successfully. Rows affected: " + updateCount, "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Update operationslog for update
                        dbManager.updateOperationsLog(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Command executed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error executing SQL command: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void clearResultWindow() {
        resultTable.setModel(new DefaultTableModel()); // clear table
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseGUI::new);
    }
}
