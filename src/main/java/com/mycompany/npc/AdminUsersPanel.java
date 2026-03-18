/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author lana
 */
public class AdminUsersPanel extends JPanel {
    private MainFrame mainFrame;
    private User user;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private int hoverRow = -1;
    private int hoverCol = -1;
    
    public AdminUsersPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Users");
        title.setFont(new Font("Montserrat SemiBold", Font.PLAIN, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Create table model
        tableModel = new DefaultTableModel(new Object[]{"Email", "Name", "Surname", "Role", "Edit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        usersTable = new JTable(tableModel);
        // Centers the text in table cells
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        usersTable.setRowHeight(38);

        // Add button "Edit" to the last cell
        usersTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox()));
        usersTable.getColumn("Edit").setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JLabel label = new JLabel("Edit");
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);

            if (row == hoverRow && col == hoverCol) {
                label.setBackground(new Color(100, 130, 190)); // hover
            } else {
                label.setBackground(new Color(78, 105, 160)); // normal
            }

            return label;
        });
        
        

        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);
        
        initHoverEffect();
        loadUsers();
    }
    // Loading all users to the table
    private void loadUsers() {
        tableModel.setRowCount(0); // clear table

        List<User> users = UserController.getAllUsers(); 
        for (User u : users) {
            tableModel.addRow(new Object[]{
                    u.getEmail(),
                    u.getName(),
                    u.getSurname(),
                    u.isAdmin() ? "Admin" : "User",
                    "Edit"
            });
        }
    }
    // Button rendering in JTable
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener((ActionEvent e) -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
                this.row = row;
                label = (value == null) ? "" : value.toString();
                button.setText(label);
                clicked = true;
                return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                String email = (String) tableModel.getValueAt(row, 0);
                User selectedUser = UserController.getUserByEmail(email);
                if (selectedUser != null) {
                    EditUserDialog dialog = new EditUserDialog(
                            (Frame) SwingUtilities.getWindowAncestor(AdminUsersPanel.this),
                            true,
                            mainFrame.getCurrentUser(),
                            selectedUser
                    );
                    dialog.setVisible(true);
                    loadUsers(); // Update table after editing
                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
    private void initHoverEffect() {
        usersTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = usersTable.rowAtPoint(e.getPoint());
                int col = usersTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == usersTable.getColumnCount() - 1) {
                    hoverRow = row;
                    hoverCol = col;
                    usersTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    hoverRow = -1;
                    hoverCol = -1;
                    usersTable.setCursor(Cursor.getDefaultCursor());
                }

                usersTable.repaint();
            }
        });
    }

}
