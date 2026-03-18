/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UserStamps extends StampsPanel {

    private JButton btnAddStamp;
    String ownerEmail = user.getEmail();

    public UserStamps(User user) {
        super(user, new String[]{
            "Edit", "Delete"
        });
        if (user == null) {
            throw new IllegalArgumentException("AddStampDialog requires a logged-in user");
        }
        centerTableCells();
        //Hide the ID column
        hideIdColumn();
        
        initAddButton();  //Button "+ Add Stamp"
        initTableClickHandler(); // Set up action listeners for Edit/Delete
        initActionColumnsStyle(); //Last 2 column style
        initHoverForColumns(6, 7);
        loadStamps(); //Load data into the table
    }

    // Initialise and configure the "Add Stamp" button
    private void initAddButton() {
        btnAddStamp = new JButton("+ Add Stamp");
        btnAddStamp.setFont(new Font("Montserrat SemiBold", Font.PLAIN, 14));
        btnAddStamp.setForeground(Color.WHITE);
        btnAddStamp.setBackground(new Color(78, 105, 160));
        btnAddStamp.setPreferredSize(new Dimension(160, 36));
        btnAddStamp.setFocusPainted(false);

        btnAddStamp.addActionListener(e -> {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            AddStampDialog dialog = new AddStampDialog(parent, true, user);
            dialog.setVisible(true);
            loadStamps(); // Refresh table
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(btnAddStamp, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

// Source file for stamps
    @Override
    protected String getSourceFile() {
        return "stamps.csv";
    }

    // Rows filter: display only stamps belonging to the current user
    @Override
    protected boolean isRowAllowed(String[] d) {
        return d[6].equalsIgnoreCase(user.getEmail());
    }

    // Create Table Row
    @Override
    protected Object[] buildRow(String[] d) {
        ImageIcon icon = loadImage(d[5]);
        return new Object[]{
            d[0],      // StampID
            icon,      // Image
            d[1],      // Title
            d[2],      // Category
            d[3],      // Year
            d[4],      // Country
            "✏ Edit",
            "✖ Delete"
        };
    }

    // Hendle mouse clicks on columns (Edit/Delete)
    private void initTableClickHandler() {
        stampsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = stampsTable.rowAtPoint(e.getPoint());
                int col = stampsTable.columnAtPoint(e.getPoint());

                if (row < 0) return;

                String stampId = tableModel.getValueAt(row, 0).toString();
                String title   = tableModel.getValueAt(row, 2).toString();

                if (col == 6) { // Edit
                    EditStampDialog dialog =
                        new EditStampDialog(
                            (Frame) SwingUtilities.getWindowAncestor(UserStamps.this),
                            user,
                            stampId
                        );
                    dialog.setVisible(true);
                    loadStamps();
                } else if (col == 7) { // Delete
                    int confirm = JOptionPane.showConfirmDialog(
                        UserStamps.this,
                        "Delete stamp \"" + title + "\"?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteStamp(stampId);
                        loadStamps();
                    }
                }
            }
        });
    }
    // Edit/Delete columns style
    private void initActionColumnsStyle() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = new JLabel(value == null ? "" : value.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);

                boolean isHover = row == hoverRow && column == hoverCol;

                if (column == 6) { // ✏ Edit
                    label.setBackground(isHover
                            ? new Color(100, 130, 190)
                            : new Color(78, 105, 160));
                }

                if (column == 7) { // ✖ Delete
                    label.setBackground(isHover
                            ? new Color(190, 80, 80)
                            : new Color(160, 60, 60));
                }

                return label;
            }
        };

        stampsTable.getColumnModel().getColumn(6).setCellRenderer(renderer);
        stampsTable.getColumnModel().getColumn(7).setCellRenderer(renderer);
    }
    // Remove a stamp record from CSV file
    private void deleteStamp(String stampId) {
        List<String[]> rows = CsvHandler.read(getSourceFile());
        if (rows.isEmpty()) return;

        List<String[]> result = new ArrayList<>();
        result.add(rows.get(0)); // header

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length == 0) continue;

            if (!row[0].equals(stampId)) {
                result.add(row);
            }
        }

        CsvHandler.writeAll(getSourceFile(), result);
    }

    // Visibility check for ownership
    @Override
    protected boolean isStampVisible(String ownerEmail) {
        return ownerEmail.equalsIgnoreCase(user.getEmail());
    }
}