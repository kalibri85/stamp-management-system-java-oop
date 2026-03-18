/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lana
 */
public class UserController {
    // Source file of users
    private static final String FILE = "users.csv";
    private static final String HEADER = "Email,Name,Surname,Password,Role";
    public static User login(String email, String password) {
       CsvHandler.ensureFileExists(FILE, HEADER);

        String hash = hashPassword(password);

        for (String[] row : CsvHandler.readData(FILE)) {
            if (row.length < 5) continue;

            if (row[0].equalsIgnoreCase(email) && row[3].equals(hash)) {
                int role = Integer.parseInt(row[4]);
                return new User(row[0], row[1], row[2], role);
            }
        }
        return null;
    }
    // Converts string into SHA-256 hex string. For secure password storage and comparison
    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            // Convert byte array to hex format
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Check if specific email address is already exist in CSV file.
    public static boolean userExists(String email) {
        CsvHandler.ensureFileExists(FILE, HEADER);

        for (String[] row : CsvHandler.readData(FILE)) {
            if (row.length > 0 && row[0].equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // Save a new user in CSV file. Default role is set to 0. Only one user is admin.
    public static void registerUser(String email, String name, String surname, String password) {
        CsvHandler.ensureFileExists(FILE, HEADER);

        if (userExists(email)) return;

        List<String[]> rows = CsvHandler.read(FILE); // WITH header
        rows.add(new String[]{
                email,
                name,
                surname,
                hashPassword(password),
                "0"
        });

        CsvHandler.writeAll(FILE, rows);
    }
    // Get all users list
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        CsvHandler.ensureFileExists(FILE, HEADER);

        for (String[] row : CsvHandler.readData(FILE)) {
            if (row.length < 5) continue;
            users.add(new User(
                    row[0],
                    row[1],
                    row[2],
                    Integer.parseInt(row[4])
            ));
        }
        return users;
    }
    // Get user data by email
    public static User getUserByEmail(String email) {
        CsvHandler.ensureFileExists(FILE, HEADER);

        for (String[] row : CsvHandler.readData(FILE)) {
            if (row.length < 5) continue;
            if (row[0].equalsIgnoreCase(email)) {
                return new User(row[0], row[1], row[2], Integer.parseInt(row[4]));
            }
        }
        return null;
    }
    // Edit existing user
    public static void updateUser(User user, String newPassword) {
        CsvHandler.ensureFileExists(FILE, HEADER);

        List<String[]> rows = CsvHandler.read(FILE); // Includes header

        for (int i = 1; i < rows.size(); i++) { // Starts after header
            String[] row = rows.get(i);

            if (row[0].equalsIgnoreCase(user.getEmail())) {
                row[1] = user.name;
                row[2] = user.surname;

                if (newPassword != null && !newPassword.isEmpty()) {
                    row[3] = hashPassword(newPassword);
                }
                row[4] = String.valueOf(user.role);
            }
        }
        CsvHandler.writeAll(FILE, rows);
    }
}
