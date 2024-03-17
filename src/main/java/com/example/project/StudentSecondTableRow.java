package com.example.project;

// This class represent a row in the TableView of the Student Screen
public class StudentSecondTableRow {
    private final String subject;
    private String average;
    private Double gradesSum;
    private int gradesCount;

    public StudentSecondTableRow(String subject, String firstGrade) {
        this.subject = subject;
        this.average = firstGrade;
        this.gradesSum = Double.valueOf(firstGrade);
        this.gradesCount = 1;
    }

    public String getSubject() {
        return subject;
    }
    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void addGrade(String grade){
        gradesSum += Double.parseDouble(grade);
        gradesCount++;
        average = String.format("%.2f", gradesSum / gradesCount);
    }
}
