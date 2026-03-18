/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.List;

/**
 *
 * @author lana
 * Displaying a list of all stamps
 * Features include category filtering, ownership checks and an "Add to Favourites" button
 */
public class AllStamps extends StampsPanel {
    private Set<String> favourites = new HashSet<>();

    public AllStamps(User user) {
        // Dynamic column setup baset on guest/ logged-in user
        super(user, user != null ? new String[]{"Owner", "Add To Favourite"} : new String[]{"Owner"});
        centerTableCells();
        // Hides the StampID column
        hideIdColumn();

        if (user != null) {
            loadFavourites();
            initFavouriteColumn();
        }
        initFilterPanel();
        loadStamps();
        initHoverForColumns(columns.length - 1);
        initFavouriteColumnRenderer();
    }

    // Data source overrides
    @Override
    protected String getSourceFile() {
        return "stamps.csv";
    }

    // All stamps are visible
    @Override
    protected boolean isRowAllowed(String[] d) {
        return true;
    }

    /** Constructs a table row. Row includes a "Favorite" button if the stamps is not already in the user's wishlist 
    * and not owned by the current user
    */
    @Override
    protected Object[] buildRow(String[] d) {
        ImageIcon icon = loadImage(d[5]);
        //JButton favBtn = null;
        Object favContent = null; // Can be a JButton or a String message

        if (user != null) {
            String key = d[0] + "_" + user.getEmail();
            // Show favourit button only if user isn't the owner and hasn't favourited this stamp yet
            if (!key.equals(d[0] + "_" + d[6]) && !favourites.contains(key)) {
                JButton favBtn = new JButton("❤");
                favBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
                favBtn.setForeground(new Color(255, 255, 255));
                favBtn.setContentAreaFilled(false);
                favBtn.setBorderPainted(false);
                favContent = favBtn;
            } else if(!key.equals(d[0] + "_" + d[6]) && favourites.contains(key)){
                favContent = "In your wishlist";
            } else {
                favContent = "It's your stamp";
            }
        }

        return new Object[]{d[0], icon, d[1], d[2], d[3], d[4], d[6], favContent};
    }

    // Loading the fovourite relationships from CSV into memory. Creates file with headers if it does not exist
    private void loadFavourites() {
        CsvHandler.ensureFileExists(
        "favourites.csv",
        "StampId,User"
    );
        for (String[] d : CsvHandler.readData("favourites.csv")) {
            if (d.length == 2) {
                favourites.add(d[0] + "_" + d[1]);
            }
        }
    }
    // Set up the category filter at the top of the table
    private void initFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        String[] categories = {"All", "Definitive", "Commemorative", "Used", "Mint"};
        JComboBox<String> categorySelect = new JComboBox<>(categories);
        categorySelect.setPreferredSize(new Dimension(150, 30));
        filterPanel.add(categorySelect);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setFocusPainted(false);
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor pointer
        filterBtn.setBorder(BorderFactory.createLineBorder(new Color(78, 105, 160), 1, true));
        filterBtn.setBackground(new Color(78, 105, 160));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setPreferredSize(new Dimension(100, 30));

        filterBtn.addActionListener(e -> {
            String selectedCategory = (String) categorySelect.getSelectedItem();
            applyCategoryFilter(selectedCategory);
        });

        filterPanel.add(filterBtn);

        add(filterPanel, BorderLayout.NORTH);
    }
    // Reloads the table filtered by the selected category
    private void applyCategoryFilter(String category) {
        tableModel.setRowCount(0); // Clean table

        for (String[] d : CsvHandler.readData(getSourceFile())) {
            if (d.length < 7) continue;

            if (!category.equals("All") &&
                !d[2].equalsIgnoreCase(category)) continue;

            tableModel.addRow(buildRow(d));
        }
    }
    // Adds click functionality to the Favourite column
    private void initFavouriteColumn() {
        stampsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = stampsTable.rowAtPoint(e.getPoint());
                int col = stampsTable.columnAtPoint(e.getPoint());

                if (row < 0 || col != columns.length - 1) return;

                String stampId = tableModel.getValueAt(row, 0).toString();
                String owner = tableModel.getValueAt(row, 6).toString();
                String key = stampId + "_" + user.getEmail();

                if (owner.equalsIgnoreCase(user.getEmail())) {
                    JOptionPane.showMessageDialog(AllStamps.this, "This is your stamp");
                    return;
                }

                if (favourites.contains(key)) return;

                CsvHandler.ensureFileExists("favourites.csv", "StampId,User");

                List<String[]> rows = CsvHandler.read("favourites.csv");
                rows.add(new String[]{stampId, user.getEmail()});
                CsvHandler.writeAll("favourites.csv", rows);

                favourites.add(key);
                tableModel.setValueAt(null, row, col);
            }
        });
    }
   // Custom render to handle button background colour; hover effect
   private void initFavouriteColumnRenderer() {
    int favColumnIndex = columns.length - 1;

    stampsTable.getColumnModel()
        .getColumn(favColumnIndex)
        .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {

    if (value instanceof JButton) {
        JButton btn = (JButton) value;
        btn.setOpaque(true);

        // 🔹 Hover effect
        if (row == hoverRow && column == hoverCol) {
            btn.setBackground(new Color(100, 130, 190)); // Hover
        } else {
            btn.setBackground(new Color(78, 105, 160)); // Normal
        }

        btn.setBorder(BorderFactory.createLineBorder(
            new Color(78, 105, 160), 1, true));
        return btn;
    } else if (value instanceof String) {
        JLabel label = new JLabel(value.toString());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Montserrat", Font.ITALIC, 12));
        label.setForeground(new Color(120, 120, 120)); // Subtle gray
        return label;
    }
    // Return empty panel if no button. Already favourited or self-owned
    JPanel empty = new JPanel();
    empty.setBackground(Color.WHITE);
    return empty;
});
}
    // Load image
    @Override
    protected ImageIcon loadImage(String path) {
        ImageIcon icon = null;
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Image img = new ImageIcon(path).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        return icon;
    }
}