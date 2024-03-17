package com.example.project;

import java.util.ArrayList;

public class AbsencesTableRow {
    private final String studentUsername; // This is the username! rename if u want2
    private final StringBuilder studentAbsences;
    private final ArrayList<String> dates;
    private final static String separator = " ";

    public AbsencesTableRow(String studentName){
        this.studentUsername = studentName;
        this.dates = new ArrayList<>();
        this.studentAbsences = new StringBuilder();
    }

    public void addAbsence(String date){
        // ATTENTION! This function only add in the gui
        // use model.insertAbsence to add in the db
        dates.add(date);
        studentAbsences.append(date).append(separator);
    }

    public void removeAbsence(int index){
        // ATTENTION! This function only remove in the gui
        // use model.removeAbsence to remove in the db
        dates.remove(index);
        studentAbsences.setLength(0);
        for(String date : dates) studentAbsences.append(date).append(separator);
    }

    public String getStudentUsername(){return studentUsername;}
    public String getStudentAbsences(){return studentAbsences.toString();}
}
