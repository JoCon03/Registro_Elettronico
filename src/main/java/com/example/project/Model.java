/************************************** ITA ****************************************
 * La classe Model fa da intermediario tra il Database SQLite e l'Applicazione Java.
 * E' una classe Singleton, percio' non puo' essere istanziata usando 'new'.
 * Per ottenere un oggetto Model usare Model.getInstance().
 * Il Model si aspetta di ricevere parametri corretti e non fa alcun controllo.
 * Il metodo loginCheck restituisce:
 *    0 se il login ha successo come Admin
 *    1 se il login ha successo come Professor
 *    2 se il login ha successo come Student
 *   -1 se il login fallisce.
 * I metodi get* restituiscono Stringhe, a volte vuote se falliscono.
 * I metodi get*TableRows restituiscono ArrayList<*TableRow>, vuote se falliscono.
 * Il metodo getTeachingsOf restituisce un ArrayList<String>, vuota se fallisce.
 * I metodi insert* restituiscono true se l'inserimento ha avuto successo.
 * I metodi update*, set* e remove* non restituiscono nulla (void).
 * Alcuni dei metodi update* sono stati rinominati per migliorare la leggibilita'.
 * I messaggi di errore del Model sono per gli sviluppatori delle altre classi,
 *   a loro si affida il compito di non esporli mai agli utenti finali.
 ************************************** ENG ****************************************
 * The Model bridges the SQLite Database and Java Application.
 * It is a Singleton class, therefor it cannot be instantiated using 'new'.
 * To obtain a Model object, use Model.getInstance().
 * The Model expects correct parameters and does not perform validations.
 * The loginCheck method returns:
 *     0 if the login is successful as an Admin
 *     1 if the login is successful as a Professor
 *     2 if the login is successful as a Student
 *    -1 if the login fails.
 * The get* methods return String objects, sometimes empty if they fail.
 * The get*TableRows methods return ArrayList<*>, empty if they fail.
 * The getTeachingsOf method returns an ArrayList<String>, empty if it fails.
 * The insert* methods return true if the insertion is successful, false otherwise.
 * The update*, and remove* methods do not return anything (void).
 * Some of the update* methods had their name changed for better readability.
 * Error messages in this class are intended for developers of the other classes,
 *   they are entrusted with ensuring that these are never exposed to end-users.
 ***********************************************************************************/
package com.example.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.control.CheckBox;
import org.mindrot.jbcrypt.BCrypt;

public class Model {

    // PRIVATE
    private final static Model instance = new Model();
    private final String url = "src/main/resources/database/school.db";
    private Connection con;
    private final String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private final String[] tables = {"Admin", "Professor", "Student"};

    private Model() {
        openConnection();
        try {
            File currentDatabase = new File(url);
            File backupDatabase = new File("src/main/resources/database/backup.db");
            if (checkDatabaseIntegrity()) // Create a backup copy
                Files.copy(currentDatabase.toPath(), backupDatabase.toPath(), StandardCopyOption.REPLACE_EXISTING);
            else { // Restore the backup
                Files.copy(backupDatabase.toPath(), currentDatabase.toPath(), StandardCopyOption.REPLACE_EXISTING);
                openConnection();
            }
        } catch (IOException ignored) {}
    }

    private void openConnection() {
        try {
            if(con != null && !con.isClosed()) con.close();
            con = DriverManager.getConnection("jdbc:sqlite:" + url);
        } catch (SQLException unreachable) {
            // This exception is unreachable, as the database is created if not found.
            // In this case any login attempt will just fail.
            throw new RuntimeException("Model Error: Impossible to connect to the database!");
        }
    }

    private boolean checkDatabaseIntegrity() {
        try (Statement statement = con.createStatement()) {
            String result = statement.executeQuery("PRAGMA integrity_check;").getString(1);
            return result.equalsIgnoreCase("ok");
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean doesExist(String id, String table) {
        String query = "SELECT COUNT(*) FROM %s WHERE id=?;";
        try (PreparedStatement stmt = con.prepareStatement(query.formatted(table))) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) >= 1;
        } catch (SQLException e){
            return false;
        }
    }
    private boolean doesUserExist(String id) {
        return doesExist(id, "Admin") || doesExist(id, "Professor") || doesExist(id, "Student");
    }

    private boolean isAbsent(String username) {
        String query = "SELECT COUNT(*) FROM Absence WHERE student=? AND date=?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, currentDate);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) >= 1;
        } catch (SQLException e){
            return false;
        }
    }

    // PUBLIC
    public static Model getInstance() {
        return instance;
    }

    //returns 0, 1, 2 if login succeed on the Admin, Professor or Student table respectively, -1 if login fails
    public int loginCheck(String username, String password) {
        String query = "SELECT password FROM %s WHERE id=?;";
        for (int i = 0; i < tables.length; i++)
            try (PreparedStatement stmt = con.prepareStatement(query.formatted(tables[i]))) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                String savedPassword = rs.getString(1);
                rs.close();
                if (BCrypt.checkpw(password, savedPassword))
                    return i;
            } catch (SQLException ignored) {}
        return -1;
    }

    // GET
    public String getFullName(String username) {
        String fullName = null;
        String query = "SELECT name, surname FROM %s WHERE id=?;";
        for (String table : new String[]{"Professor", "Student"})
            try (PreparedStatement stmt = con.prepareStatement(query.formatted(table))) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                fullName = "%s %s".formatted(rs.getString("name"), rs.getString("surname"));
                rs.close();
            } catch (SQLException ignored) {}
        if (fullName == null) {
            System.err.printf("Model Error: Unable to retrieve the full name of %s!!\n", username);
            fullName = "";
        }
        return fullName;
    }

    public String getAbsences(String studentUsername) {
        String query = "SELECT COUNT(*) FROM Absence WHERE student=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, studentUsername);
            return stmt.executeQuery().getString(1);
        } catch (SQLException e) {
            System.err.printf("Model Error: Unable to retrieve the correct number of absences of %s\n", studentUsername);
            return "0";
        }
    }
    public String getTeachingId(String professorUsername, String classroom, String subject) {
        String query = "SELECT id FROM Teaching WHERE professor=? AND classroom=? AND subject=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            String[] args = {professorUsername, classroom, subject};
            for (int i = 0; i < args.length; i++)
                stmt.setString(i + 1, args[i]);
            return stmt.executeQuery().getString(1);
        } catch (SQLException e){
            return "";
        }
    }
    public String getTheme(String username) {
        String query = "SELECT theme FROM %s WHERE id=?;";
        for (String table : tables)
            try (PreparedStatement stmt = con.prepareStatement(query.formatted(table))) {
                stmt.setString(1, username);
                String theme = stmt.executeQuery().getString(1);
                if (theme != null) return theme;
            } catch (SQLException ignored) {}
        return "Light";
    }
    public String getTheme() {
        String query = "SELECT login_theme FROM Admin;";
        try (Statement stmt = con.createStatement()) {
            return stmt.executeQuery(query).getString(1);
        } catch (SQLException ignored) {}
        return "Light";
    }
    public String getPasswordRequests(){
        String query = "SELECT password_requests FROM Admin;";
        try (Statement stmt =  con.createStatement()) {
            return stmt.executeQuery(query).getString(1);
        } catch (SQLException e) {
            System.err.print("Model Error: Change password requests may be incorrect!\n");
            return "";
        }
    }

    public ArrayList<String> getTeachingsOf(String professorUsername){
        ArrayList<String> teachings = new ArrayList<>();
        String query = "SELECT subject, classroom FROM Teaching WHERE professor=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, professorUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                teachings.add("%s %s".formatted(rs.getString(1), rs.getString(2)));
            rs.close();
        } catch (SQLException e){
            System.err.printf("Model Error: Unable to retrieve the full data about the teachings of %s!\n",
                    professorUsername);
        }
        return teachings;
    }

    // GET * TableRows
    public ArrayList<AdminTopTableRow> getAdminTopTableRows() {
        ArrayList<AdminTopTableRow> adminTopTableRows = new ArrayList<>();
        String query = "SELECT id, name, surname, classroom FROM Student;";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
                adminTopTableRows.add(new AdminTopTableRow(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4)));
            rs.close();
        } catch (SQLException e){
            System.err.print("Model Error: Unable to retrieve the full information about students!\n");
        }
        return adminTopTableRows;
    }
    public ArrayList<AdminBottomTableRow> getAdminBottomTableRows() {
        ArrayList<AdminBottomTableRow> adminBottomTableRows = new ArrayList<>();
        String query = "SELECT id, name, surname FROM Professor;";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
                adminBottomTableRows.add(new AdminBottomTableRow(rs.getString(1), rs.getString(2),
                        rs.getString(3)));
            rs.close();
        } catch (SQLException e) {
            System.err.print("Model Error: Unable to retrieve the full information about professors!\n");
        }
        return adminBottomTableRows;
    }
    public ArrayList<AbsencesTableRow> getAbsencesTableRows() {
        return getAbsencesTableRows(null);
    }
    public ArrayList<AbsencesTableRow> getAbsencesTableRows(String classroom) {
        ArrayList<AbsencesTableRow> absencesTableRows = new ArrayList<>();
        String[] querys = new String[2];
        if (classroom == null)
            querys[0] = "SELECT id FROM Student;";
        else
            querys[0] = "SELECT id FROM Student WHERE classroom=?;";

        querys[1] = "SELECT date FROM Absence WHERE student=?;";
        try {
            PreparedStatement[] stmts = {
                    con.prepareStatement(querys[0]),
                    con.prepareStatement(querys[1])
            };
            ResultSet[] rss = new ResultSet[2];
            if(classroom != null) stmts[0].setString(1, classroom);
            rss[0] = stmts[0].executeQuery();
            while(rss[0].next()){
                String studentUsername = rss[0].getString(1);
                AbsencesTableRow absencesTableRow = new AbsencesTableRow(studentUsername);
                stmts[1].setString(1, studentUsername);
                rss[1] = stmts[1].executeQuery();
                while(rss[1].next()) {
                    String[] dateParts = rss[1].getString("date").split("-");
                    absencesTableRow.addAbsence("%s/%s/%s".formatted(dateParts[2], dateParts[1], dateParts[0]));
                }
                rss[1].close();
                absencesTableRows.add(absencesTableRow);
            }
            stmts[0].close();
            stmts[1].close();
        } catch (SQLException ignored) {}
        return  absencesTableRows;
    }

    public ArrayList<ProfTableRow> getProfTableRows(String teaching) {
        ArrayList<ProfTableRow> profTableRows = new ArrayList<>();
        String[] querys = {
                "SELECT Student.id FROM Teaching JOIN Student ON Teaching.classroom = Student.classroom " +
                "WHERE Teaching.id = ?;",
                "SELECT value, date, comment FROM Grade WHERE student=? AND teaching=?;"
        };
        try {
            PreparedStatement[] stmts = {con.prepareStatement(querys[0]), con.prepareStatement(querys[1])};
            ResultSet[] rss = new ResultSet[2];
            stmts[0].setString(1, teaching);
            rss[0] = stmts[0].executeQuery();
            while (rss[0].next()){
                String studentUsername = rss[0].getString("id");
                ProfTableRow profTableRow = new ProfTableRow(studentUsername, teaching);
                stmts[1].setString(1, studentUsername);
                stmts[1].setString(2, teaching);
                rss[1] = stmts[1].executeQuery();
                while (rss[1].next())
                    profTableRow.addGrade(rss[1].getString("value"), rss[1].getString("date"),
                            rss[1].getString("comment"));
                rss[1].close();
                profTableRow.setAbsentCheckBox(new CheckBox(), isAbsent(studentUsername));
                profTableRows.add(profTableRow);
            }
            stmts[0].close();
            stmts[1].close();
        } catch(SQLException ignored) {}
        return  profTableRows;
    }

    public ArrayList<StudentFirstTableRow> getStudentFirstTableRows(String studentUsername) {
        ArrayList<StudentFirstTableRow> studentFirstTableRows = new ArrayList<>();
        String query =
                "SELECT Grade.value, Grade.date, Grade.comment, " +
                "Teaching.professor, Teaching.subject, Professor.name, Professor.surname " +
                "FROM Grade JOIN Teaching ON Grade.teaching = Teaching.id " +
                "JOIN Professor ON Teaching.professor = Professor.id " +
                "WHERE Grade.student = ?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, studentUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String[] dateParts = rs.getString("date").split("-");
                String  professorFullName = "%s %s".formatted(rs.getString("name"),
                            rs.getString("surname")),
                        subject = rs.getString("subject"),
                        value = rs.getString("value"),
                        //dateParts[] = rs.getString("date").split("-"),
                        date = "%s/%s/%s".formatted(dateParts[2], dateParts[1], dateParts[0]),
                        comment = rs.getString("comment");
                studentFirstTableRows.add(new StudentFirstTableRow(professorFullName, subject, value, date, comment));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.printf("Model Error: Unable to retrieve the full data of %s!\n", studentUsername);
        }
        return studentFirstTableRows;
    }
    public ArrayList<StudentSecondTableRow> getStudentSecondTableRows(String studentUsername) {
        ArrayList<StudentSecondTableRow> studentSecondTableRows = new ArrayList<>();
        String query = "SELECT Grade.value, Teaching.subject " +
                "FROM Grade JOIN Teaching ON Grade.teaching = Teaching.id WHERE student=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, studentUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String grade = rs.getString("value"), subject = rs.getString("subject");
                boolean appended = false;
                for(StudentSecondTableRow row : studentSecondTableRows)
                    if(row.getSubject().equalsIgnoreCase(subject)) {
                        row.addGrade(grade);
                        appended = true;
                    }
                if(!appended)
                    studentSecondTableRows.add(new StudentSecondTableRow(subject, grade));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.printf("Model Error: Unable to retrieve the full data of %s!\n", studentUsername);
        }
        return studentSecondTableRows;
    }


    // INSERT
    private boolean insert(String query, String[] args){ // PRIVATE implementation of public methods
        try ( PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++)
                stmt.setString(i + 1, args[i]);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.print("Model Error: Failed to save data into the database\n");
            return false;
        }
    }

    public boolean insertProfessor(AdminBottomTableRow p, String password) {
        return insertUser(p.getUsername(), password, p.getName(), p.getSurname(), null);
    }
    public boolean insertStudent(AdminTopTableRow s, String password) {
        return insertUser(s.getUsername(), password, s.getName(), s.getSurname(), s.getClassroom());
    }
    public boolean insertUser(String username, String password, String name, String surname, String classroom) {
        if(doesUserExist(username))
            return false;
        String query = "INSERT INTO %s(id, password, name, surname%s) VALUES(?, ?, ?, ?%s);";
        String[] args, additions;
        if (classroom == null) { // is a Professor
            args = new String[]{username, BCrypt.hashpw(password, BCrypt.gensalt(12)), name, surname};
            additions = new String[]{"Professor", "", ""};
        }else{ // is a Student
            args = new String[]{username, BCrypt.hashpw(password, BCrypt.gensalt(12)), name, surname, classroom};
            additions = new String[]{"Student", ", classroom", ", ?"};
        }
        return insert(query.formatted(additions[0], additions[1], additions[2]), args);
    }

    public boolean insertTeaching(String professorUsername, String classroom, String subject) {
        if(
                getTeachingId(professorUsername, classroom, subject).isEmpty()
                        &&
                doesExist(professorUsername, "Professor")
                        &&
                (doesExist(classroom, "Classroom") || insert("INSERT INTO Classroom(id) VALUES(?);",
                        new String[]{classroom}))
                        &&
                (doesExist(subject, "Subject") || insert("INSERT INTO Subject(id) VALUES(?);",
                        new String[]{subject}))
        ) {
            String query = "INSERT INTO Teaching(professor, classroom, subject) VALUES(?, ?, ?);";
            String[] args = {professorUsername, classroom, subject};
            return insert(query, args);
        }
        return false;
    }

    public boolean insertGrade(String studentUsername, String teaching, String date, String comment, String value) {
        if (!doesExist(studentUsername, "Student") || !doesExist(teaching, "Teaching"))
            return false;
        String query = "INSERT INTO Grade(student, teaching, date, comment, value) VALUES(?, ?, ?, ?, ?);";
        String[] args = {studentUsername, teaching, date, comment, value};
        return insert(query, args);
    }

    public boolean insertAbsence(String studentUsername, String date) {
        if(!doesExist(studentUsername, "Student"))
            return false;
        String query = "INSERT INTO Absence(student, date) VALUES(?, ?);";
        String[] args = {studentUsername, date};
        return insert(query, args);
    }


    // UPDATE ( SET, REQUEST, DISCARD )
    private void update(String table, String column, String id, String newValue) { // PRIVATE implementation of public methods
        String query = "UPDATE %s SET %s=? WHERE id=?;".formatted(table, column);
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newValue);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException ignored) {}
    }
    private void updateAdmin(String column, String newValue){ // PRIVATE implementation of public methods
        String query = "UPDATE Admin SET %s=?;".formatted(column);
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newValue);
            stmt.executeUpdate();
        } catch (SQLException ignored) {}
    }
    public void updatePassword(String username, String newPassword) {
        for (String table: tables)
            update(table, "password", username, BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
        discardPasswordRequest(username);
    }
    public void updateUsername(String oldUsername, String newUsername) {
        if (doesUserExist(newUsername)) return;
        update("Professor", "id", oldUsername, newUsername);
        update("Student", "id", oldUsername, newUsername);
    }
    public void updateName(String username, String newName) {
        update("Professor", "name", username, newName);
        update("Student", "name", username, newName);
    }
    public void updateSurname(String username, String newSurname) {
        update("Professor", "surname", username, newSurname);
        update("Student", "surname", username, newSurname);
    }
    public void updateClassroom(String username, String newClassroom) {
        update("Student", "classroom", username, newClassroom);
    }
    public void updateTheme(String username, String newTheme) {
        // inserting the wrong theme can lead to serious consequences => input validation
        String[] validThemes = {"Light", "Dark", "Purple", "Blue", "Green", "Red", "Yellow"};
        boolean found = false;
        for (String theme : validThemes)
            if (theme.equals(newTheme)) {
                found = true;
                break;
            }
        if (!found) {
            System.err.println(newTheme + "is not a valid theme!");
            return;
        }
        if (username != null) for ( String table: tables ) update(table, "theme", username, newTheme);
        updateAdmin("login_theme", newTheme);
    }

    public void setAbsence(String studentUsername, boolean isAbsent) {
        if(isAbsent(studentUsername) == isAbsent) return;
        if (isAbsent)
            insertAbsence(studentUsername, currentDate);
        else
            removeAbsence(studentUsername, currentDate);
    }
    public void requestPassword(String username, String requestedPassword){
        discardPasswordRequest(username);
        String oldRequests = getPasswordRequests();
        String appendage = username;
        if (requestedPassword != null) appendage += ":" + requestedPassword;
        appendage += ",";
        updateAdmin("password_requests", oldRequests + appendage);
    }
    public void discardPasswordRequest(String username) {
        String oldRequests = getPasswordRequests();
        if (oldRequests.isEmpty()) return;
        StringBuilder newRequests = new StringBuilder();
        for (String request : oldRequests.split(",")) {
            if (!request.matches("^" + username + "(:.*?)?$"))
                newRequests.append(request).append(",");
        }
        updateAdmin("password_requests", newRequests.toString());
    }


    // REMOVE
    private void remove(String table, String column, String value) { // PRIVATE implementation of public methods
        String query = "DELETE FROM %s WHERE %s=?;".formatted(table, column);
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.execute();
        } catch (SQLException ignored){}
    }
    public void removeProfessor(String username) {
        remove("Teaching", "professor", username);
        remove("Professor", "id", username);
    }
    public void removeStudent(String username) {
        remove("Grade", "student", username);
        remove("Student", "id", username);
    }
    public void removeTeaching(String professorUsername, String classroom, String subject) {
        remove("Grade", "teaching", getTeachingId(professorUsername, classroom, subject));
        remove("Teaching", "id", getTeachingId(professorUsername, classroom, subject));
    }
    public void removeGrade(String studentUsername, String teaching, String date, String value) {
        String query = "SELECT id FROM Grade WHERE student=? AND teaching=? AND date=? AND value=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) { // Retrive the id of the Grade
            String[] args = {studentUsername, teaching, date, value};
            for (int i = 0; i < args.length; i++)
                stmt.setString(i + 1, args[i]);
            remove("Grade", "id", stmt.executeQuery().getString("id"));
        } catch (SQLException ignored){}
    }

    public void removeAbsence(String studentUsername, String date) {
        String query = "SELECT id FROM Absence WHERE student=? AND date=?;";
        try (PreparedStatement stmt = con.prepareStatement(query)) { // Retrive the id of the Absence
            String[] args = {studentUsername, date};
            for (int i = 0; i < args.length; i++)
                stmt.setString(i + 1, args[i]);
            remove("Absence", "id", stmt.executeQuery().getString("id"));
        } catch (SQLException ignored){}
    }

} //end of class