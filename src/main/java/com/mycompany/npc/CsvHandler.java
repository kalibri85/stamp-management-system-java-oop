/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lana
 */
public class CsvHandler {

    public static void ensureFileExists(String fileName, String headerLine) {
        File file = new File(fileName);

        if (file.exists()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(headerLine);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create CSV file: " + fileName, e);
        }
    }
    // Returns all rows
    public static List<String[]> read(String fileName) {
        List<String[]> rows = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) return rows;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read CSV file: " + fileName, e);
        }

        return rows;
    }
    // Returns only rows with data
    public static List<String[]> readData(String fileName) {
        List<String[]> allRows = read(fileName);
        if (allRows.isEmpty()) return allRows;
        return allRows.subList(1, allRows.size()); // Returns only data, not header
    }

    public static void writeAll(String fileName, List<String[]> rows) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String[] row : rows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write CSV file: " + fileName, e);
        }
    }
}
