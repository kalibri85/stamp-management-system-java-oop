/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
/**
 *
 * @author lana
 * Abstract class for displaying and managing stamps data in a JTable
 */

public abstract class StampsPanel extends JPanel {
    protected JTable stampsTable;
    protected DefaultTableModel tableModel;
    protected User user; // The current user logged-in, can be null for guests
    protected String[] columns;
    protected int hoverRow = -1;
    protected int hoverCol = -1;

    public StampsPanel(User user, String[] extraColumns) {
        this.user = user;
        this.columns = buildColumns(extraColumns);
        initComponents();
    }
    // Initialises the UI components
    protected void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Base table by default
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Ensure the "Image" columns renders as an Icon
                if ("Image".equals(columns[columnIndex])) {
                    return ImageIcon.class;
                }
                return String.class;
            }
        };

        stampsTable = new JTable(tableModel);
        stampsTable.setRowHeight(100); // Set height for stamp image
        JScrollPane scrollPane = new JScrollPane(stampsTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    // Common columns present in all stamp tables
    protected static final String[] BASE_COLUMNS = {
        "StampID",
        "Image",
        "Title",
        "Category",
        "Year",
        "Country"
    };
    protected void hideIdColumn() {
        TableColumn idCol = stampsTable.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setPreferredWidth(0);
    }
    // Combines base stamps' table columns with child-class specific columns
    protected static String[] buildColumns(String[] extraColumns) {
        String[] result = new String[BASE_COLUMNS.length + extraColumns.length];

        System.arraycopy(BASE_COLUMNS, 0, result, 0, BASE_COLUMNS.length);
        System.arraycopy(extraColumns, 0, result, BASE_COLUMNS.length, extraColumns.length);

        return result;
    }
    protected boolean isStampVisible(String ownerEmail) {
        return true;
    }
    // Load table by reading data from CSV
    protected void loadStamps() {
        tableModel.setRowCount(0); // Clear existing data

        try (BufferedReader br = new BufferedReader(new FileReader(getSourceFile()))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }

                String[] d = line.split(",");
                if (!isRowAllowed(d)) continue;

                Object[] row = buildRow(d);
                if (row != null) {
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Loads and scale an image file
    protected ImageIcon loadImage(String path) {
        if (path == null || path.isEmpty()) return null;

        File imgFile = new File(path);
        if (!imgFile.exists()) return null;

        Image img = new ImageIcon(path).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    // Centers the text in table cells
    protected void centerTableCells() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Loop through all columns to apply the renderer
        for (int i = 0; i < stampsTable.getColumnCount(); i++) {
            // Skip the image column
            if (i == 1) continue;
            stampsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    // Change the mouse cursor to hand cursor when hovering ober the button 
    protected void initHoverForColumns(int... actionCols) {
        stampsTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = stampsTable.rowAtPoint(e.getPoint());
                int col = stampsTable.columnAtPoint(e.getPoint());

                boolean hover = row >= 0;
                boolean match = false;

                for (int c : actionCols) {
                    if (col == c) {
                        match = true;
                        break;
                    }
                }

                if (hover && match) {
                    hoverRow = row;
                    hoverCol = col;
                    stampsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    hoverRow = -1;
                    hoverCol = -1;
                    stampsTable.setCursor(Cursor.getDefaultCursor());
                }

                stampsTable.repaint();
            }
        });
    }
    //methods-templates
    protected abstract String getSourceFile();
    protected abstract boolean isRowAllowed(String[] d);
    protected abstract Object[] buildRow(String[] d);
}
