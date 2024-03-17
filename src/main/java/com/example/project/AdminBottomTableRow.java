package com.example.project;

// This class represent a row in the TableView of the Admin Screen
// Each row contain the information of a Professor
public class AdminBottomTableRow {
    private String username;
    private String name;
    private String surname;

    //public AdminBottomTableRow() {}
    public AdminBottomTableRow(String username, String name, String surname){
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public AdminBottomTableRow() {
    }

    public String getUsername(){return username;}
    public String getName(){return name;}
    public String getSurname(){return surname;}

    public void setUsername(String username){ this.username = username; }
    public void setName(String name){
        this.name = name;
    }
    public void setSurname(String surname){this.surname = surname;}
}
