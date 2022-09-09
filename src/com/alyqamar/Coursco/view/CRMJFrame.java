/*0
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.alyqamar.Coursco.view;

import com.alyqamar.Coursco.dal.dao.DBConnection;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.util.Date;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @alyQamar
 */
public class CRMJFrame extends javax.swing.JFrame {

    /**
     * Creates new form ProductJFrame
     */
    //db variables
    Connection con = null;
    Statement st = null;
    PreparedStatement pst = null;

    public CRMJFrame() {
        initComponents();

        setJFrameIcon();  //Set Frame Icon
        setJComponentsIcon();
        //Table
        //Table header
        contentJTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        contentJTable.getTableHeader().setOpaque(false);
        contentJTable.getTableHeader().setBackground(new Color(63, 103, 150));
        contentJTable.getTableHeader().setForeground(Color.WHITE);

        //Table Algorithm
        contentJTable.setAutoscrolls(true); // Auto scrol

        connect();
        resetAllTextFields();
        UpdateTableItems();

    }

    private void setJFrameIcon() {
        String path = "img/crmIcon.png";
        Image img = new ImageIcon(path).getImage();
        this.setIconImage(img);
    }

    private void setJComponentsIcon() {
        moneyJLabel.setIcon(new ImageIcon("img\\egyptionPound.png"));
        insertJButton.setIcon(new ImageIcon("img\\add.png"));
        resetJButton.setIcon(new ImageIcon("img\\reset.png"));
        updateJButton.setIcon(new ImageIcon("img\\update.png"));
        deleteJButton.setIcon(new ImageIcon("img\\delete.png"));
        clearAllJButton.setIcon(new ImageIcon("img\\clearAll.png"));
        printJButton.setIcon(new ImageIcon("img\\print.png"));
        FindJButton.setIcon(new ImageIcon("img\\search.png"));
        
    }

    private void connect() {
        DBConnection dBCon = new DBConnection();
        dBCon.Connect();
        con = dBCon.getCon();
    }

    private void UpdateTableItems() {
        try {
            pst = con.prepareStatement("SELECT code, name, price, start_date, end_date, DATEDIFF(day, start_date, end_date) AS duration FROM CRM;");
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData RSM = rs.getMetaData();
            int c;
            c = RSM.getColumnCount();
            DefaultTableModel DF = (DefaultTableModel) contentJTable.getModel();
            DF.setRowCount(0);

            while (rs.next()) {
                Vector v2 = new Vector();
                for (int i = 1; i <= c; i++) {
                    v2.add(rs.getInt("code"));
                    v2.add(rs.getString("name"));
                    v2.add(rs.getDouble("price"));
                    v2.add(rs.getString("start_date"));
                    v2.add(rs.getString("end_date"));
                    v2.add(rs.getInt("duration"));
                }
                DF.addRow(v2);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changeAction() {

        DefaultTableModel d1 = (DefaultTableModel) contentJTable.getModel();
        int SelectIndex = contentJTable.getSelectedRow();

        codeJTextField.setText(d1.getValueAt(SelectIndex, 0).toString());
        nameJTextField.setText(d1.getValueAt(SelectIndex, 1).toString());
        priceJTextField.setText(d1.getValueAt(SelectIndex, 2).toString());
        Date date;
        try {
            date = new SimpleDateFormat("yyy-MM-dd").parse((String) d1.getValueAt(SelectIndex, 3));
            startDateJDateChooser.setDate(date);

            date = new SimpleDateFormat("yyy-MM-dd").parse((String) d1.getValueAt(SelectIndex, 4));
            endDateJDateChooser.setDate(date);

        } catch (ParseException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        durationJTextField.setText(d1.getValueAt(SelectIndex, 5).toString());

    }

    private void resetAllTextFields() {
        codeJTextField.setText("");
        nameJTextField.setText("");
        priceJTextField.setText("");
        ((JTextField) startDateJDateChooser.getDateEditor().getUiComponent()).setText("");
        ((JTextField) endDateJDateChooser.getDateEditor().getUiComponent()).setText("");
        durationJTextField.setText("");
        searchJTextField.setText("Search here!");
    }

    private boolean emptyTextFields() {
        String code = codeJTextField.getText();
        String name = nameJTextField.getText();
        String startDate = getStartDate();
        String endDate = getEndDate();
        String price = priceJTextField.getText();
        boolean empty = (code.equals("") || name.equals("")
                || startDate.equals("") || endDate.equals("") || price.equals(""));
        return empty;
    }

// you can use this method with bit changes to check if duplicate name or duration
    private boolean duplicatedItemsCode(String code) {
        boolean bol = false;

        try {
            pst = con.prepareStatement("select code from CRM");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                if (Integer.parseInt(code) == rs.getInt("code")) {
                    bol = true;
                }
            }
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bol;
    }

    private boolean duplicatedItemsName() {
        boolean bol = false;
        String name = nameJTextField.getText();

        try {
            pst = con.prepareStatement("select * from CRM");
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData RSM = rs.getMetaData();
            int c;
            c = RSM.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= c; i++) {
                    if (name.equals(rs.getString("name"))) {
                        bol = true;
                    }
                }
            }
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bol;
    }

    private void findByCode() {
        //Search key value
        String codeKey = searchJTextField.getText();

        boolean emptyKey = codeKey.equals("");
        if (emptyKey) {
            JOptionPane.showMessageDialog(this, "please Enter code in search field to search for it!");
        } else if (duplicatedItemsCode(codeKey)) {
            try {
                pst = con.prepareStatement("SELECT code, name, price, start_date, end_date,"
                        + " DATEDIFF(day, start_date, end_date) AS duration FROM CRM WHERE code = ?;");
                pst.setInt(1, Integer.parseInt(codeKey));
                ResultSet rs = pst.executeQuery();
                ResultSetMetaData RSM = rs.getMetaData();
                int c;
                c = RSM.getColumnCount();
                DefaultTableModel DF = (DefaultTableModel) contentJTable.getModel();
                DF.setRowCount(0);

                while (rs.next()) {
                    Vector v2 = new Vector();
                    for (int i = 1; i <= c; i++) {
                        v2.add(rs.getInt("code"));
                        v2.add(rs.getString("name"));
                        double p = rs.getDouble("price");
                        v2.add(p);
                        v2.add(rs.getString("start_date"));
                        v2.add(rs.getString("end_date"));
                        v2.add(rs.getInt("duration"));

                    }
                    DF.addRow(v2);
                }
                pst.close();
            } catch (SQLException ex) {
                Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            searchJTextField.setText("Search here!");
        } else {
            JOptionPane.showMessageDialog(this, "your item code isn't found. Try again with right code!", "Alert", JOptionPane.WARNING_MESSAGE);
            resetAllTextFields();
        }
    }

    private void findByName() {
        //Search key value
        String nameKey = searchJTextField.getText();

        boolean emptyKey = nameKey.equals("");
        if (emptyKey) {
            JOptionPane.showMessageDialog(this, "please Enter name in search field to search for it!!");
        } else if (duplicatedItemsName()) {
            try {
                pst = con.prepareStatement("SELECT code, name, price, start_date, end_date,"
                        + " DATEDIFF(day, start_date, end_date) AS duration FROM CRM WHERE name = ?;");
                pst.setString(1, nameKey);
                ResultSet rs = pst.executeQuery();
                ResultSetMetaData RSM = rs.getMetaData();
                int c;
                c = RSM.getColumnCount();
                DefaultTableModel DF = (DefaultTableModel) contentJTable.getModel();
                DF.setRowCount(0);

                while (rs.next()) {
                    Vector v2 = new Vector();
                    for (int i = 1; i <= c; i++) {
                        v2.add(rs.getInt("code"));
                        v2.add(rs.getString("name"));
                        v2.add(rs.getDouble("price"));
                        v2.add(rs.getString("start_date"));
                        v2.add(rs.getString("end_date"));
                        v2.add(rs.getInt("duration"));

                    }
                    DF.addRow(v2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            searchJTextField.setText("Search here!");
        } else {
            JOptionPane.showMessageDialog(this, "your item name isn't found. Try again with right name!", "Alert", JOptionPane.WARNING_MESSAGE);
            resetAllTextFields();
        }
    }

    private String getStartDate() {
        if (((JTextField) startDateJDateChooser.getDateEditor().getUiComponent()).getText().equals("")) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(startDateJDateChooser.getDate());
            return date;
        }
    }

    private String getEndDate() {
        if (((JTextField) endDateJDateChooser.getDateEditor().getUiComponent()).getText().equals("")) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(endDateJDateChooser.getDate());
            return date;
        }
    }

    private void calcDurationInDays() {

        try {
            pst = con.prepareStatement("SELECT start_date,end_date, DATEDIFF(day, start_date, end_date) AS duration FROM CRM;");
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void calcDurationInMonths() {

        try {
            pst = con.prepareStatement("SELECT start_date,end_date, DATEDIFF(month, start_date, end_date) AS duration FROM CRM;");
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootJPanel = new javax.swing.JPanel();
        titleJPanel = new javax.swing.JPanel();
        titleJLabel = new javax.swing.JLabel();
        darkMJRadioButton = new javax.swing.JRadioButton();
        inputsJPanel = new javax.swing.JPanel();
        codeJLabel = new javax.swing.JLabel();
        codeJTextField = new javax.swing.JTextField();
        startDateJLabel = new javax.swing.JLabel();
        priceJTextField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        nameJTextField = new javax.swing.JTextField();
        durationJLabel = new javax.swing.JLabel();
        endDateJLabel = new javax.swing.JLabel();
        startDateJDateChooser = new com.toedter.calendar.JDateChooser();
        endDateJDateChooser = new com.toedter.calendar.JDateChooser();
        priceJLabel1 = new javax.swing.JLabel();
        durationJTextField = new javax.swing.JTextField();
        moneyJLabel = new javax.swing.JLabel();
        tableJPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contentJTable = new javax.swing.JTable();
        FindJComboBox = new javax.swing.JComboBox<>();
        FindJButton = new javax.swing.JButton();
        searchJTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        printJButton = new javax.swing.JButton();
        insertJButton = new javax.swing.JButton();
        deleteJButton = new javax.swing.JButton();
        clearAllJButton = new javax.swing.JButton();
        updateJButton = new javax.swing.JButton();
        resetJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());
        setResizable(false);

        titleJLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        titleJLabel.setForeground(new java.awt.Color(33, 150, 243));
        titleJLabel.setText("CRM");

        darkMJRadioButton.setText("Dark Mode");
        darkMJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkMJRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout titleJPanelLayout = new javax.swing.GroupLayout(titleJPanel);
        titleJPanel.setLayout(titleJPanelLayout);
        titleJPanelLayout.setHorizontalGroup(
            titleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleJPanelLayout.createSequentialGroup()
                .addContainerGap(355, Short.MAX_VALUE)
                .addComponent(titleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(260, 260, 260)
                .addComponent(darkMJRadioButton)
                .addGap(24, 24, 24))
        );
        titleJPanelLayout.setVerticalGroup(
            titleJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titleJPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(titleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(titleJPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(darkMJRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        codeJLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        codeJLabel.setText("Code");

        codeJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codeJTextFieldActionPerformed(evt);
            }
        });

        startDateJLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        startDateJLabel.setText("Start date");

        priceJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priceJTextFieldActionPerformed(evt);
            }
        });

        nameJLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nameJLabel.setText("Name");

        nameJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameJTextFieldActionPerformed(evt);
            }
        });

        durationJLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        durationJLabel.setText("Duration");

        endDateJLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        endDateJLabel.setText("End Date");

        startDateJDateChooser.setDateFormatString("yyyy-MM-dd");

        endDateJDateChooser.setDateFormatString("yyyy-MM-dd");

        priceJLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        priceJLabel1.setText("Price");

        durationJTextField.setEditable(false);
        durationJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationJTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout inputsJPanelLayout = new javax.swing.GroupLayout(inputsJPanel);
        inputsJPanel.setLayout(inputsJPanelLayout);
        inputsJPanelLayout.setHorizontalGroup(
            inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputsJPanelLayout.createSequentialGroup()
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addComponent(startDateJLabel)
                        .addGap(18, 18, 18)
                        .addComponent(startDateJDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addComponent(codeJLabel)
                        .addGap(59, 59, 59)
                        .addComponent(codeJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36)
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameJLabel)
                    .addComponent(endDateJLabel))
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(nameJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(endDateJDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(36, 36, 36)
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(durationJLabel)
                    .addComponent(priceJLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addComponent(priceJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(moneyJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(durationJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        inputsJPanelLayout.setVerticalGroup(
            inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moneyJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(codeJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(codeJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nameJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(priceJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(priceJLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startDateJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(endDateJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputsJPanelLayout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addComponent(startDateJDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(inputsJPanelLayout.createSequentialGroup()
                        .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(inputsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(durationJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(durationJLabel))
                            .addComponent(endDateJDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        contentJTable.setAutoCreateRowSorter(true);
        contentJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Name", "Price (£)", "Start Date", "End Date", "Duration (Days)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        contentJTable.setSelectionBackground(new java.awt.Color(97, 123, 154));
        contentJTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        contentJTable.setShowHorizontalLines(true);
        contentJTable.getTableHeader().setReorderingAllowed(false);
        contentJTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                contentJTableFocusGained(evt);
            }
        });
        contentJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contentJTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                contentJTableMousePressed(evt);
            }
        });
        contentJTable.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                contentJTableCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        contentJTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                contentJTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                contentJTableKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                contentJTableKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(contentJTable);

        FindJComboBox.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        FindJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Search by", "Search by code", "Search by name" }));
        FindJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                FindJComboBoxItemStateChanged(evt);
            }
        });
        FindJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindJComboBoxActionPerformed(evt);
            }
        });
        FindJComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                FindJComboBoxPropertyChange(evt);
            }
        });
        FindJComboBox.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                FindJComboBoxVetoableChange(evt);
            }
        });

        FindJButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        FindJButton.setMaximumSize(new java.awt.Dimension(44, 42));
        FindJButton.setMinimumSize(new java.awt.Dimension(44, 42));
        FindJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                FindJButtonMouseEntered(evt);
            }
        });
        FindJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindJButtonActionPerformed(evt);
            }
        });

        searchJTextField.setText("Search Here");
        searchJTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchJTextFieldMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchJTextFieldMousePressed(evt);
            }
        });
        searchJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchJTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tableJPanelLayout = new javax.swing.GroupLayout(tableJPanel);
        tableJPanel.setLayout(tableJPanelLayout);
        tableJPanelLayout.setHorizontalGroup(
            tableJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(tableJPanelLayout.createSequentialGroup()
                        .addComponent(searchJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FindJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FindJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        tableJPanelLayout.setVerticalGroup(
            tableJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableJPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(tableJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tableJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(searchJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                        .addComponent(FindJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(FindJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        printJButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        printJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printJButtonActionPerformed(evt);
            }
        });

        insertJButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        insertJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertJButtonActionPerformed(evt);
            }
        });

        deleteJButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        deleteJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteJButtonActionPerformed(evt);
            }
        });

        clearAllJButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        clearAllJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllJButtonActionPerformed(evt);
            }
        });

        updateJButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        updateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateJButtonActionPerformed(evt);
            }
        });

        resetJButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        resetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(insertJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(updateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(deleteJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(clearAllJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(printJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(138, 138, 138))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(printJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(updateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(insertJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resetJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(deleteJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(clearAllJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout rootJPanelLayout = new javax.swing.GroupLayout(rootJPanel);
        rootJPanel.setLayout(rootJPanelLayout);
        rootJPanelLayout.setHorizontalGroup(
            rootJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootJPanelLayout.createSequentialGroup()
                .addGroup(rootJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(rootJPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(rootJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(titleJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputsJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(rootJPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(70, 70, 70))
        );
        rootJPanelLayout.setVerticalGroup(
            rootJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootJPanelLayout.createSequentialGroup()
                .addComponent(titleJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inputsJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tableJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rootJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 851, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void resetJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetJButtonActionPerformed
        // TODO add your handling code here:
        resetAllTextFields();
        UpdateTableItems();
    }//GEN-LAST:event_resetJButtonActionPerformed

    private void FindJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindJButtonActionPerformed
        // TODO add your handling code here:
        String selectedValue = FindJComboBox.getSelectedItem().toString();
        if (selectedValue.equals("Search by")) {
            JOptionPane.showMessageDialog(this, "please select search key (Search by Code / Search by name)");
        } else if (selectedValue.equals("Search by code")) {
            findByCode();
        } else if (selectedValue.equals("Search by name")) {
            findByName();
        }
    }//GEN-LAST:event_FindJButtonActionPerformed

    private void clearAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllJButtonActionPerformed
        int a = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all items in the table?");
        if (a == JOptionPane.YES_OPTION) {

            try {

                st = con.createStatement();
                st.executeUpdate("delete from CRM;");
                st.close();
                resetAllTextFields();
                UpdateTableItems();
                JOptionPane.showMessageDialog(this, "successfully clear all items!");
            } catch (SQLException ex) {
                Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_clearAllJButtonActionPerformed

    private void updateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateJButtonActionPerformed

        //Get inputs data from user
        String code = codeJTextField.getText();
        String name = nameJTextField.getText();
        //Handle Money inputs
        String price = priceJTextField.getText();
        //Handle Date inputs
        String startDate = getStartDate();
        String endDate = getEndDate();

        if (emptyTextFields()) {
            JOptionPane.showMessageDialog(this, "please Enter All Data!");
        } else if (!duplicatedItemsCode(code)) {
            JOptionPane.showMessageDialog(this, "Item is not in the table!", "Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            try {

                pst = con.prepareStatement("update CRM set  name =? , price = ? ,  start_date = ?,  end_date = ? where code = ?");
                pst.setString(1, name);
                pst.setDouble(2, Double.parseDouble(price));
                pst.setString(3, startDate);
                pst.setString(4, endDate);
                pst.setInt(5, Integer.parseInt(code));

                int k = pst.executeUpdate();
                if (k == 1) {
                    resetAllTextFields();
                    UpdateTableItems();
                    JOptionPane.showMessageDialog(this, "successfully updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Unfortunately, your item didn't updated. Try again!", "Alert", JOptionPane.WARNING_MESSAGE);
                }
                pst.close();
            } catch (SQLException ex) {
                Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_updateJButtonActionPerformed

    private void deleteJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteJButtonActionPerformed
        int a = JOptionPane.showConfirmDialog(this, "Are you sure you want delete this item from table?");
        if (a == JOptionPane.YES_OPTION) {
            String code = codeJTextField.getText();

            if (code.equals("")) {
                JOptionPane.showMessageDialog(this, "please Enter right code number to delete item!");
            } else if (!duplicatedItemsCode(code)) {
                JOptionPane.showMessageDialog(this, "Item is not in the table!", "Alert", JOptionPane.WARNING_MESSAGE);
            } else {
                try {

                    pst = con.prepareStatement("delete from CRM where code = ? ");
                    pst.setString(1, code);

                    int k = pst.executeUpdate();

                    if (k == 1) {
                        resetAllTextFields();
                        UpdateTableItems();
                        JOptionPane.showMessageDialog(this, "successfully deleted!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Unfortunately, your item didn't deleted. Try again!", "Alert", JOptionPane.WARNING_MESSAGE);
                    }
                    pst.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_deleteJButtonActionPerformed

    private void insertJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertJButtonActionPerformed
        //Get inputs data from user
        String code = codeJTextField.getText();
        String name = nameJTextField.getText();
        //Handle Money inputs
        String price = (priceJTextField.getText());
        //Handle Date inputs
        String startDate = getStartDate();
        String endDate = getEndDate();

        if (emptyTextFields()) {
            JOptionPane.showMessageDialog(this, "please Enter All Data!");
        } else if (Integer.parseInt(code) < 1) { // negative integer code condition
            JOptionPane.showMessageDialog(this, "Try again with right code which greater than zero!", "Alert", JOptionPane.WARNING_MESSAGE);
        } else if (duplicatedItemsCode(code)) { // check if this code is stored with another item
            // Code should be unique because it's search key
            JOptionPane.showMessageDialog(this, "Item Code is already in the table!", "Alert", JOptionPane.WARNING_MESSAGE);
        } else if (duplicatedItemsName()) {// check if this name is stored with another item
            // name should be unique because it's search key
            JOptionPane.showMessageDialog(this, "Item name is already in the table!", "Alert", JOptionPane.WARNING_MESSAGE);

        } else {
            try {

                pst = con.prepareStatement("insert into CRM(code,name,price,start_date,end_date)values(?,?,?,?,?);");
                pst.setInt(1, Integer.parseInt(code));
                pst.setString(2, name);
                pst.setDouble(3, Double.parseDouble(price));
                pst.setString(4, startDate);
                pst.setString(5, endDate);
                int k = pst.executeUpdate();

                if (k == 1) {
                    resetAllTextFields();
                    UpdateTableItems();
                    JOptionPane.showMessageDialog(this, "successfully added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Unfortunately, your item didn't add. Try again!", "Alert", JOptionPane.WARNING_MESSAGE);
                }

                pst.close();
            } catch (SQLException ex) {
                Logger.getLogger(CRMJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_insertJButtonActionPerformed

    private void contentJTableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_contentJTableKeyTyped
        // TODO add your handling code here: 
    }//GEN-LAST:event_contentJTableKeyTyped

    private void contentJTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_contentJTableKeyPressed
//        changeAction(1);

    }//GEN-LAST:event_contentJTableKeyPressed

    private void contentJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contentJTableMouseClicked

    }//GEN-LAST:event_contentJTableMouseClicked

    private void priceJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priceJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_priceJTextFieldActionPerformed

    private void codeJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codeJTextFieldActionPerformed

    private void nameJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameJTextFieldActionPerformed

    private void printJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printJButtonActionPerformed
        // TODO add your handling code here:
        MessageFormat header = new MessageFormat("CRM");
        MessageFormat footer = new MessageFormat("Created by Aly Qamar");
        try {
            boolean print = contentJTable.print(JTable.PrintMode.NORMAL, header, footer);
            if (!print) {
                JOptionPane.showMessageDialog(this, "Unable To Print !!..");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_printJButtonActionPerformed

    private void FindJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindJComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FindJComboBoxActionPerformed

    private void durationJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_durationJTextFieldActionPerformed

    private void darkMJRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkMJRadioButtonActionPerformed
        // TODO add your handling code here:
        if (darkMJRadioButton.isSelected()) {
            EventQueue.invokeLater(() -> {
                FlatDarkLaf.setup();
                FlatLaf.updateUI();
            });
        } else {
            EventQueue.invokeLater(() -> {
                FlatIntelliJLaf.setup();
                FlatLaf.updateUI();
            });
        }
    }//GEN-LAST:event_darkMJRadioButtonActionPerformed

    private void FindJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FindJButtonMouseEntered

    }//GEN-LAST:event_FindJButtonMouseEntered

    private void searchJTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchJTextFieldMouseClicked
        //searchJTextField.setText("");
    }//GEN-LAST:event_searchJTextFieldMouseClicked

    private void searchJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchJTextFieldActionPerformed

    private void FindJComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_FindJComboBoxPropertyChange
        // TODO add your handling code here:


    }//GEN-LAST:event_FindJComboBoxPropertyChange

    private void FindJComboBoxVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_FindJComboBoxVetoableChange
        // TODO add your handling code here:

    }//GEN-LAST:event_FindJComboBoxVetoableChange

    private void FindJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_FindJComboBoxItemStateChanged
        // TODO add your handling code here:
        resetAllTextFields();
        UpdateTableItems();
    }//GEN-LAST:event_FindJComboBoxItemStateChanged

    private void searchJTextFieldMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchJTextFieldMousePressed
        // TODO add your handling code here:
        searchJTextField.setText("");
    }//GEN-LAST:event_searchJTextFieldMousePressed

    private void contentJTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contentJTableMousePressed
        // TODO add your handling code here:
        changeAction();
    }//GEN-LAST:event_contentJTableMousePressed

    private void contentJTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contentJTableFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_contentJTableFocusGained

    private void contentJTableCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_contentJTableCaretPositionChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_contentJTableCaretPositionChanged

    private void contentJTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_contentJTableKeyReleased
        // TODO add your handling code here:
        changeAction();
    }//GEN-LAST:event_contentJTableKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CRMJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CRMJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CRMJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CRMJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //Dark mode
        FlatIntelliJLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CRMJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FindJButton;
    private javax.swing.JComboBox<String> FindJComboBox;
    private javax.swing.JButton clearAllJButton;
    private javax.swing.JLabel codeJLabel;
    private javax.swing.JTextField codeJTextField;
    private javax.swing.JTable contentJTable;
    private javax.swing.JRadioButton darkMJRadioButton;
    private javax.swing.JButton deleteJButton;
    private javax.swing.JLabel durationJLabel;
    private javax.swing.JTextField durationJTextField;
    private com.toedter.calendar.JDateChooser endDateJDateChooser;
    private javax.swing.JLabel endDateJLabel;
    private javax.swing.JPanel inputsJPanel;
    private javax.swing.JButton insertJButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel moneyJLabel;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JTextField nameJTextField;
    private javax.swing.JLabel priceJLabel1;
    private javax.swing.JTextField priceJTextField;
    private javax.swing.JButton printJButton;
    private javax.swing.JButton resetJButton;
    private javax.swing.JPanel rootJPanel;
    private javax.swing.JTextField searchJTextField;
    private com.toedter.calendar.JDateChooser startDateJDateChooser;
    private javax.swing.JLabel startDateJLabel;
    private javax.swing.JPanel tableJPanel;
    private javax.swing.JLabel titleJLabel;
    private javax.swing.JPanel titleJPanel;
    private javax.swing.JButton updateJButton;
    // End of variables declaration//GEN-END:variables
}
