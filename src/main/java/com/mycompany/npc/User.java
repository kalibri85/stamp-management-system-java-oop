/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.npc;

/**
 *
 * @author lana
 */
public class User {
    public String email;
    public String name;
    public String surname;
    public int role;

    public User(String email, String name, String surname, int role) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }
    public boolean isAdmin() {
        return role == 1; // 1 = admin, 0 = regular user
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
    
    public int getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
     public void setRole(int role) {
        this.role = role;
    }
}
