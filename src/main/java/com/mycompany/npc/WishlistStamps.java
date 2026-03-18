/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author lana
 */
public class WishlistStamps extends StampsPanel{
    //Set to the store unique stamps' ID's for fast lookup
    private Set<String> wishlistIds = new HashSet<>();

    public WishlistStamps(User user) {
        //Initialise with a "Remove" action column
        super(user, new String[]{"Remove"});

        // Hide the StampID column
        hideIdColumn();
        centerTableCells();
        // Load wishlist data from favourites.csv
        loadWishlistIds();       
        // Initialise click listener for the "Remove" button
        initTableClickHandler();
        initRemoveColumnStyle();   // Button style   
        initHoverForColumns(columns.length - 1);
        // Data Loading
        loadStamps();
    }

    // Source File For Wishlist
    @Override
    protected String getSourceFile() {
        return "stamps.csv";
    }

    // Row filtering: only show stamps that exist in the user's wishlist set
    @Override
    protected boolean isRowAllowed(String[] d) {
        return wishlistIds.contains(d[0]);
    }

    // Build the table row object array from CSV data
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
            "✖ Remove"
        };
    }

    // Load stamp IDs belonging to the current user from the favorites file
    private void loadWishlistIds() {
        wishlistIds.clear();

        List<String[]> rows = CsvHandler.readData("favourites.csv");

        for (String[] row : rows) {
            if (row.length < 2) continue;

            if (row[1].equalsIgnoreCase(user.getEmail())) {
                wishlistIds.add(row[0]);
            }
        }
    }

    // Handle interaction with the "Remove" button in the table
    private void initTableClickHandler() {
        stampsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = stampsTable.rowAtPoint(e.getPoint());
                int col = stampsTable.columnAtPoint(e.getPoint());

                if (row < 0 || col != 6) return; // "Remove" button in column number 6

                String stampId = tableModel.getValueAt(row, 0).toString();
                String title   = tableModel.getValueAt(row, 2).toString();

                int confirm = JOptionPane.showConfirmDialog(
                        WishlistStamps.this,
                        "Remove \"" + title + "\" from your wishlist?",
                        "Confirm remove",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    removeFromWishlist(stampId); // Delete from CSV file
                    wishlistIds.remove(stampId); // Update local cashe
                    loadStamps();                // Refresh table display
                }
            }
        });
    }
    private void initRemoveColumnStyle() {
        int removeCol = 6;

        stampsTable.getColumnModel()
            .getColumn(removeCol)
            .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {

                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);

                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                label.setForeground(Color.WHITE);

                // Hover-effect
                if (row == hoverRow && column == hoverCol) {
                    label.setBackground(new Color(190, 80, 80));
                } else {
                    label.setBackground(new Color(160, 60, 60));
                }

                label.setBorder(BorderFactory.createLineBorder(
                    new Color(78, 105, 160), 1, true
                ));

                return label;
            });
    }

    // Remove a specific record from CSV file
    private void removeFromWishlist(String stampId) {
        List<String[]> rows = CsvHandler.read("favourites.csv");
        if (rows.isEmpty()) return;

        List<String[]> result = new ArrayList<>();
        result.add(rows.get(0)); // header

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length < 2) continue;

            // Skip the row that matches both stampId and current user
            if (row[0].equals(stampId) && row[1].equalsIgnoreCase(user.getEmail())) {
                continue;
            }

            result.add(row);
        }

        CsvHandler.writeAll("favourites.csv", result);
    }

    @Override
    protected boolean isStampVisible(String ownerEmail) {
        return true; // All Rows For Particular User Are Visible
    }
}