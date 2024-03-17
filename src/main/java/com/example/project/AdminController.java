package com.example.project;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.example.project.View.requestsStage;

public class AdminController implements Initializable {
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField surnameTextField;
    @FXML
    public TextField classroomTextField;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public Label nameLabel;
    @FXML
    public Label surnameLabel;
    @FXML
    public Label classroomLabel;
    public int studentClicksCount = 0;
    public int profClicksCount = 0;
    @FXML
    public Button cancelButton;
    @FXML
    public TableView<AdminTopTableRow> studentsTable;
    @FXML
    public TableColumn<AdminTopTableRow, String> studentClassroomColumn = new TableColumn<>();
    @FXML
    public TableColumn<AdminTopTableRow, String> studentSurnameColumn = new TableColumn<>();
    @FXML
    public TableColumn<AdminTopTableRow, String> studentNameColumn = new TableColumn<>();

    @FXML
    public TableColumn<AdminTopTableRow, String> studentUsernameColumn = new TableColumn<>();
    @FXML
    public TableView<AdminBottomTableRow> profTable;
    @FXML
    public TableColumn<AdminBottomTableRow, String> profUsernameColumn;
    @FXML
    public TableColumn<AdminBottomTableRow, String> profNameColumn;
    @FXML
    public TableColumn<AdminBottomTableRow, String> profSurnameColumn;
    public boolean white = true;
    @FXML
    public AnchorPane leftPane;
    @FXML
    public AnchorPane rightPane;
    @FXML
    public Button removeStudentButton;
    @FXML
    public Button addStudentButton;
    @FXML
    public Button addProfButton;
    @FXML
    public Button removeProfButton;
    @FXML
    public Button removeTeachingButton;
    @FXML
    public Button addTeachingButton;
    @FXML
    public TextField subjectTextField;
    @FXML
    public TextField classroomTeachingTextField;
    @FXML
    public Label subjectLabel;
    @FXML
    public Label classroomTeachingLabel;
    @FXML
    public Label profName;
    @FXML
    public Label profSurname;
    @FXML
    public TableView<String> teachingTable;
    @FXML
    public TableColumn<String, String> teachingColumn;
    public ArrayList<TextField> textes = new ArrayList<>();

    public ArrayList<Label> lables = new ArrayList<>();
    public ArrayList<TableColumn> columns = new ArrayList<>();
    public ArrayList<Button> addButtons = new ArrayList<>();
    public ArrayList<Button> removeButtons = new ArrayList<>();

    public boolean incomplete = false;
    public boolean addingAStudent;
    public boolean written = false;
    public boolean teaching;
    public boolean removed;
    public boolean removing;
    private final Model model = Model.getInstance();
    public ArrayList<AdminTopTableRow> adminTopTableRows = model.getAdminTopTableRows();
    public ArrayList<AdminBottomTableRow> adminBottomTableRows = model.getAdminBottomTableRows();
    public Alert syntaxAlert = new Alert(Alert.AlertType.ERROR);
    public Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    public Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
    public ArrayList<AdminTopTableRow> sortWithNames = new ArrayList<>();
    private boolean screenOpened;

    public void initialize(URL location, ResourceBundle resources) {

        studentUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        studentClassroomColumn.setCellValueFactory(new PropertyValueFactory<>("classroom"));

        profUsernameColumn.setCellValueFactory((new PropertyValueFactory<>("username")));
        profNameColumn.setCellValueFactory((new PropertyValueFactory<>("name")));
        profSurnameColumn.setCellValueFactory((new PropertyValueFactory<>("surname")));

        teachingColumn.setCellValueFactory((data -> new SimpleStringProperty(data.getValue())));  //dice che la colonna riceverà il valore di una stringa passata
        add(textes, lables, columns, addButtons, removeButtons);
        sortByUsername();
        sortByClassroom();

        for (AdminTopTableRow adminTopTableRow : adminTopTableRows) {
            studentsTable.getItems().add(adminTopTableRow);
        }
        for (AdminBottomTableRow adminBottomTableRow : adminBottomTableRows) {
            profTable.getItems().add(adminBottomTableRow);
        }
        studentsTable.setEditable(true);
        for (TableColumn column : columns) {
            column.setCellFactory(TextFieldTableCell.forTableColumn());             //Consente di modificare le celle delle colonne delle tabelle
        }
        profTable.setEditable(true);
        syntaxAlert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);   //Fa sì che l'alert si adatti alla
                                                                                                //lunghezza del testo che deve contenere
        switch (model.getTheme()) {
            case "Red" -> setAdminRed(null);
            case "Blue" -> setAdminBlue(null);
            case "Green" -> setAdminGreen(null);
            case "Yellow" -> setAdminYellow(null);
            case "Light" -> setAdminLight(null);
            case "Dark" -> setAdminDark(null);
            case "Purple" -> setAdminPurple(null);
        }
        if (!model.getPasswordRequests().isEmpty()) {
            confirmationAlert.setTitle("RICHIESTE CAMBIO PASSWORD TROVATE!");
            confirmationAlert.setHeaderText("HAI DELLE RICHIESTE DI CAMBIO PASSWORD DA STUDENTI/PROFESSORI!");
            confirmationAlert.setContentText("VUOI VEDERLE?");
            //aspetto che si apra la schermata, poi mostro l'alert
            Platform.runLater(() -> {
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    screenOpened=true;
                    View.requestsWindow();
                    requestsStage.setOnCloseRequest(windowEvent -> {
                        screenOpened=false;
                    });
                }
            });
        }
    }

    public void sortByUsername() {
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<AdminTopTableRow> sortedAdminTopTableRows = new ArrayList<>();
        for (AdminTopTableRow adminTopTableRow : adminTopTableRows) {
            usernames.add(adminTopTableRow.getUsername());
        }
        Collections.sort(usernames);
        for (String string : usernames) {
            for (AdminTopTableRow adminTopTableRow : adminTopTableRows) {
                if (adminTopTableRow.getUsername().equals(string)) {
                    sortedAdminTopTableRows.add(adminTopTableRow);
                }
            }
        }
        sortWithNames = sortedAdminTopTableRows;
    }

    public void sortByClassroom() {
        ArrayList<String> classrooms = new ArrayList<>();
        ArrayList<AdminTopTableRow> sortedAdminTopTableRows = new ArrayList<>();
        for (AdminTopTableRow adminTopTableRow : adminTopTableRows) {
            if (!classrooms.contains(adminTopTableRow.getClassroom())) {
                classrooms.add(adminTopTableRow.getClassroom());
            }
        }
        Collections.sort(classrooms);
        for (String string : classrooms) {
            for (AdminTopTableRow adminTopTableRow : sortWithNames) {
                if (adminTopTableRow.getClassroom().equals(string)) {
                    sortedAdminTopTableRows.add(adminTopTableRow);
                }
            }
        }
        adminTopTableRows = sortedAdminTopTableRows;
    }

    public void add(ArrayList t, ArrayList l, ArrayList c, ArrayList ab, ArrayList rb) {
        t.add(usernameTextField);
        t.add(passwordTextField);
        t.add(nameTextField);
        t.add(surnameTextField);
        t.add(classroomTextField);
        l.add(usernameLabel);
        l.add(passwordLabel);
        l.add(nameLabel);
        l.add(surnameLabel);
        l.add(classroomLabel);
        c.add(studentUsernameColumn);
        c.add(studentNameColumn);
        c.add(studentSurnameColumn);
        c.add(studentClassroomColumn);
        c.add(profUsernameColumn);
        c.add(profNameColumn);
        c.add(profSurnameColumn);
        c.add(teachingColumn);
        ab.add(addStudentButton);
        ab.add(addProfButton);
        ab.add(addTeachingButton);
        rb.add(removeStudentButton);
        rb.add(removeProfButton);
        rb.add(removeTeachingButton);
    }

    public void addStudent(ActionEvent actionEvent) {

        if ((studentClicksCount == 0 && profClicksCount == 0) || teaching || removing) {
            teaching = false;
            subjectTextField.setText("");
            subjectTextField.setVisible(false);
            subjectLabel.setVisible(false);
            classroomTeachingTextField.setText("");
            classroomTeachingTextField.setVisible(false);
            classroomTeachingLabel.setVisible(false);
            profSurname.setText("alunno:");
            profName.setText("Aggiungi un");
            profSurname.setVisible(true);
            profName.setVisible(true);
            teachingTable.setVisible(false);
            for (int i = 0; i < 5; i++) {
                textes.get(i).setVisible(true);
                lables.get(i).setVisible(true);
            }
            cancelButton.setVisible(true);
            addingAStudent = true;

        } else if (!(addingAStudent)) {
            textes.get(4).setVisible(true);
            lables.get(4).setVisible(true);
            addingAStudent = true;
            for (int i = 0; i < 4; i++) {
                textes.get(i).setText("");
            }
            profSurname.setText("alunno:");
            profName.setText("Aggiungi un");
            profSurname.setVisible(true);
            profName.setVisible(true);
        } else if (addingAStudent) {
            for (int j = 0; j < 5; j++) {
                if (textes.get(j).getText().isEmpty()) {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("CI SONO DELLE SEZIONI VUOTE! RIEMPILE PER COMPLETARE L'AGGIUNTA!");
                    syntaxAlert.show();
                    incomplete = true;
                    break;
                }
                if (j == 4) {
                    incomplete = false;
                }
            }
            //il !incomplete è usato per evitare che gli alert si sovrappongano in caso ci siano più errori di sintassi contemporaneamente
            if ((!(usernameTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$")) || !(passwordTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$"))) && !incomplete) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("CI SONO DEI SIMBOLI NON CONSENTITI NELLA SEZIONE USERNAME/PASSWORD! SONO CONSENTITI SOLO ALFANUMERICI, '_' , '-' , '.' E '@'");
                syntaxAlert.show();
                incomplete = true;
            }
            if(!incomplete && passwordTextField.getText().length()<5){
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("LA PASSWORD DEVE CONTENERE ALMENO 4 CARATTERI!");
                syntaxAlert.show();
                incomplete=true;
            }

            if (!(nameTextField.getText().matches("[a-zA-Z\\s']+$") && surnameTextField.getText().matches("[a-zA-Z\\s']+$")) && !incomplete) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI NELLA SEZIONE NOME O NELLA SEZIONE COGNOME! SISTEMA PER COMPLETARE L'AGGIUNTA");
                syntaxAlert.show();
                incomplete = true;
            }

            if (!(classroomTextField.getText().matches("[1-5][A-Z]")) && !incomplete) {      //inizia dal primo carattere e passa al secondo
                studentsTable.refresh();                                                            //la regex dice se classe è formato da un numero da 1 a 5 seguito da una lettera e basta
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("NELLA SEZIONE CLASSE DEVI INSERIRE UN NUMERO DA 1 A 5 SEGUITO DA UNA LETTERA E BASTA!!!");
                syntaxAlert.show();
                incomplete = true;
            }
            //se non ci sono errori proseguo
            if (!(incomplete)) {
                boolean duplicate = false;
                for (AdminTopTableRow s : studentsTable.getItems()) {
                    if (Objects.equals(s.getUsername(), usernameTextField.getText())) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI!");
                        syntaxAlert.show();
                        duplicate = true;
                        break;
                    }
                }
                for (AdminBottomTableRow adminBottomTableRow : profTable.getItems()) {
                    if (Objects.equals(adminBottomTableRow.getUsername(), usernameTextField.getText())) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI A QUELLI DEI PROFESSORI!");
                        syntaxAlert.show();
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    AdminTopTableRow adminTopTableRow;
                    adminTopTableRow = new AdminTopTableRow(usernameTextField.getText(), nameTextField.getText(), surnameTextField.getText(), classroomTextField.getText());
                    studentsTable.getItems().add(adminTopTableRow);
                    model.insertStudent(adminTopTableRow, passwordTextField.getText());
                    for (int i = 0; i < 5; i++) {
                        textes.get(i).setText("");
                    }
                }
            }
        }
        studentClicksCount++;
        removing = false;

    }

    public void removeStudent(ActionEvent actionEvent) {
        removed = true;
        if (teaching) {
            subjectTextField.setText("");
            subjectTextField.setVisible(false);
            subjectLabel.setVisible(false);
            classroomTeachingTextField.setText("");
            classroomTeachingTextField.setVisible(false);
            classroomTeachingLabel.setVisible(false);
            profName.setVisible(false);
            profSurname.setVisible(false);
        }
        AdminTopTableRow s = studentsTable.getSelectionModel().getSelectedItem();
        if (s != null) {
            confirmationAlert.setTitle("SEI SICURO?");
            confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO STUDENTE?");
            confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                studentsTable.getItems().remove(s);
                model.removeStudent(s.getUsername());
            }
        } else {
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("DEVI SELEZIONARE UN ALUNNO PER POTERLO RIMUOVERE! ");
            syntaxAlert.show();
        }
    }


    public void Cancel(ActionEvent actionEvent) {
        if (addingAStudent) {
            for (int i = 0; i < 5; i++) {
                if (!textes.get(i).getText().isEmpty()) {
                    written = true;
                    break;
                }
                if (i == 4) {
                    written = false;
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (textes.get(i).getText() != "") {
                    written = true;
                    break;
                }
                if (i == 3) {
                    written = false;
                }
            }
        }
        if (written) {
            confirmationAlert.setTitle("SEI SICURO?");
            confirmationAlert.setHeaderText("SEI SICURO DI VOLER ANNULLARE? TUTTE LE SCRITTE ANDRANNO CANCELLATE!");
            confirmationAlert.setContentText("PREMI OK PER CANCELLARE, ANNULLA PER NON CANCELLARE");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (addingAStudent) {
                    for (int i = 0; i < 5; i++) {
                        textes.get(i).setText("");
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        textes.get(i).setText("");
                    }
                }
            }
        }
    }

    public void addProf(ActionEvent actionEvent) {
        if ((studentClicksCount == 0 && profClicksCount == 0) || teaching || removing) {
            teaching = false;
            removing = false;
            subjectTextField.setText("");
            subjectTextField.setVisible(false);
            subjectLabel.setVisible(false);
            classroomTeachingTextField.setText("");
            classroomTeachingTextField.setVisible(false);
            classroomTeachingLabel.setVisible(false);
            profSurname.setText("professore:");
            profName.setText("Aggiungi un");
            profSurname.setVisible(true);
            profName.setVisible(true);
            teachingTable.setVisible(false);
            for (int i = 0; i < 4; i++) {
                textes.get(i).setVisible(true);
                lables.get(i).setVisible(true);
            }

            cancelButton.setVisible(true);
        } else if (addingAStudent) {
            textes.get(4).setVisible(false);
            lables.get(4).setVisible(false);
            addingAStudent = false;
            for (int i = 0; i < 5; i++) {
                textes.get(i).setText("");
            }
            profSurname.setText("professore:");
            profName.setText("Aggiungi un");
            profSurname.setVisible(true);
            profName.setVisible(true);

        } else if (!addingAStudent) {
            //stessi controlli di addStudent
            for (int j = 0; j < 4; j++) {
                if ((textes.get(j).getText().isEmpty())) {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("CI SONO DELLE SEZIONI VUOTE! RIEMPILE PER COMPLETARE L'AGGIUNTA!");
                    syntaxAlert.show();
                    incomplete = true;
                    break;
                }
                if (j == 3) {
                    incomplete = false;
                }
            }
            if ((!(usernameTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$")) || !(passwordTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$"))) && !incomplete) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("CI SONO DEI CARATTERI NON CONSENTITI NELLA SEZIONE USERNAME/PASSWORD! SONO CONSENTITI SOLO ALFANUMERICI, '_' , '-' , '.' , E '@'");
                syntaxAlert.show();
                incomplete = true;
            }
            if(!incomplete && passwordTextField.getText().length()<5){
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("LA PASSWORD DEVE CONTENERE ALMENO 4 CARATTERI!");
                syntaxAlert.show();
                incomplete = true;
            }
            if (!(nameTextField.getText().matches("[a-zA-Z\\s']+$") && surnameTextField.getText().matches("[a-zA-Z\\s']+$")) && !incomplete) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI NELLA SEZIONE NOME O NELLA SEZIONE COGNOME! SISTEMA PER COMPLETARE L'AGGIUNTA");
                syntaxAlert.show();
                incomplete = true;
            }

            if (!(incomplete)) {
                boolean duplicate = false;
                for (AdminTopTableRow s : studentsTable.getItems()) {
                    if (Objects.equals(s.getUsername(), usernameTextField.getText())) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI A QUELLI DEI PROFESSORI!");
                        syntaxAlert.show();
                        duplicate = true;
                        break;
                    }
                }
                for (AdminBottomTableRow prof : profTable.getItems()) {
                    if (Objects.equals(prof.getUsername(), usernameTextField.getText())) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("NON POSSONO ESSERCI PROFESSORI CON USERNAME UGUALI!");
                        syntaxAlert.show();
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    AdminBottomTableRow prof;
                    prof = new AdminBottomTableRow(usernameTextField.getText(), nameTextField.getText(), surnameTextField.getText());
                    profTable.getItems().add(prof);
                    model.insertProfessor(prof, passwordTextField.getText());

                    for (int i = 0; i < 4; i++) {
                        textes.get(i).setText("");
                    }
                }
            }
        }
        profClicksCount++;
    }


    public void removeProf(ActionEvent actionEvent) {
        addingAStudent = false;
        removed = true;
        if (teaching) {
            subjectTextField.setText("");
            subjectTextField.setVisible(false);
            subjectLabel.setVisible(false);
            classroomTeachingTextField.setText("");
            classroomTeachingTextField.setVisible(false);
            classroomTeachingLabel.setVisible(false);
            profSurname.setVisible(false);
            profName.setVisible(false);
        }
        AdminBottomTableRow p = profTable.getSelectionModel().getSelectedItem();
        if (p != null) {
            confirmationAlert.setTitle("SEI SICURO?");
            confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO PROFESSORE?");
            confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                profTable.getItems().remove(p);
                model.removeProfessor(p.getUsername());
            }
        } else {
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("DEVI SELEZIONARE UN ALUNNO PER POTERLO RIMUOVERE! ");
            syntaxAlert.show();
        }
    }

    public void LogoutButton(ActionEvent actionEvent) throws IOException {
        if(screenOpened){
            requestsStage.close();
        }
        try {
            // Chiudi la finestra corrente (interfaccia studente)
            View.currentStage.close();

            // Richiama il metodo start per aprire la schermata di login
            new View().start(new Stage());
        } catch (IOException e) {
            // Gestione dell'eccezione
        }
    }

    public void Instructions(ActionEvent actionEvent) {
        informationAlert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        informationAlert.setTitle("ISTRUZIONI PER L'USO");
        informationAlert.setHeaderText("BENVENUTO NELLA SCHERMATA ADMIN!");
        informationAlert.setContentText("""
Qui gestisci facilmente alunni e professori. I pulsanti sulla destra forniscono le varie opzioni.
Per aggiungere alunni o professori, clicca sul pulsante appropriato e completa le informazioni richieste (per muoverti tra le aree di testo puoi usare le frecce su e giù); poi clicca di nuovo il pulsante o premi Invio. 
Una volta aggiunta la persona puoi correggere eventuali errori facendo doppio clic su una cella o rimuovendo completamente la persona dalla tabella.
Per cancellare dati non ancora inseriti, premi "Annulla". Per rimuovere un individuo, selezionalo e premi il "Rimuovi" relativo alla sua categoria o usa i tasti Backspace e Canc.
Per aggiungere un insegnamento seleziona un professore, premi il pulsante appropriato, completa i campi e premi di nuovo il pulsante/premi Invio.
Per rimuovere un insegnamento seleziona un professore, clicca il pulsante appropriato, seleziona l'insegnamento dalla tabella che compare a sinistra, poi clicca di nuovo il pulsante o usa i tasti Backspace e Canc.
Puoi cambiare il tema, visualizzare le richieste di cambio password,caricare direttamente alunni e professori da file ed effettuare il logout,usando i pulsanti sulla barra in alto."""
        );
        informationAlert.show();

    }

    public void EditStudentUsername(TableColumn.CellEditEvent<AdminTopTableRow, String> studentStringCellEditEvent) {
        AdminTopTableRow adminTopTableRow = studentsTable.getSelectionModel().getSelectedItem();
        String sUsername = adminTopTableRow.getUsername();
        String newUsername = studentStringCellEditEvent.getNewValue();          //Prende il nuovo valore della colonna
        if (!(newUsername.matches("[a-zA-Z0-9_\\-.@]+$"))) {
            studentsTable.refresh();

            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI SIMBOLI/SPAZI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminTopTableRow.setUsername(newUsername);
            model.updateUsername(sUsername, newUsername);
        }
    }

    public void EditStudentName(TableColumn.CellEditEvent<AdminTopTableRow, String> studentStringCellEditEvent) {
        AdminTopTableRow adminTopTableRow = studentsTable.getSelectionModel().getSelectedItem();
        String sUsername = adminTopTableRow.getUsername();
        String newName = studentStringCellEditEvent.getNewValue();          //Prende il nuovo valore della colonna
        if (!(newName.matches("[A-Za-z\\s']+$"))) {
            studentsTable.refresh();
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminTopTableRow.setName(newName);
            model.updateName(sUsername, newName);
        }

    }

    public void EditStudentSurname(TableColumn.CellEditEvent<AdminTopTableRow, String> studentStringCellEditEvent) {
        AdminTopTableRow adminTopTableRow = studentsTable.getSelectionModel().getSelectedItem();
        String sUsername = adminTopTableRow.getUsername();
        String newSurname = studentStringCellEditEvent.getNewValue();       //Prende il nuovo valore della colonna
        if (!(newSurname.matches("[A-Za-z\\s']+$"))) {
            studentsTable.refresh();
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminTopTableRow.setSurname(newSurname);
            model.updateSurname(sUsername, newSurname);
        }
    }

    public void EditStudentClassroom(TableColumn.CellEditEvent<AdminTopTableRow, String> studentStringCellEditEvent) {
        String sUsername = studentsTable.getSelectionModel().getSelectedItem().getUsername();
        AdminTopTableRow adminTopTableRow = studentsTable.getSelectionModel().getSelectedItem();
        String newClassroom = studentStringCellEditEvent.getNewValue();         //Prende il nuovo valore della colonna
        if (!(newClassroom.matches("[1-5][A-Z]"))) { //inizia dal primo carattere e passa al secondo
            studentsTable.refresh();                                 //la regex dice se classroom è formato da un numero da 1 a 5 seguito da una lettera e basta
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("LA CLASSE E' FORMATA DA UNA CIFRA DA 1 A 5 SEGUITA DA UNA LETTERA!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminTopTableRow.setClassroom(newClassroom);
            model.updateClassroom(sUsername, newClassroom);
        }
    }

    public void EditProfUsername(TableColumn.CellEditEvent<AdminBottomTableRow, String> professorStringCellEditEvent) {
        AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
        String pUsername = adminBottomTableRow.getUsername();
        String newUsername = professorStringCellEditEvent.getNewValue();            //Prende il nuovo valore della colonna
        if (!(newUsername.matches("[A-Za-z0-9_\\-.@]+$"))) {
            profTable.refresh();
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI SIMBOLI/SPAZI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminBottomTableRow.setUsername(newUsername);
            model.updateUsername(pUsername, newUsername);
        }
    }

    public void EditProfName(TableColumn.CellEditEvent<AdminBottomTableRow, String> professorStringCellEditEvent) {
        AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
        String pUsername = adminBottomTableRow.getUsername();
        String newName = professorStringCellEditEvent.getNewValue();            //Prende il nuovo valore della colonna
        if (!(newName.matches("[A-Za-z\\s']+$"))) {
            profTable.refresh();
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminBottomTableRow.setName(newName);
            model.updateName(pUsername, newName);
        }
    }


    public void EditProfSurname(TableColumn.CellEditEvent<AdminBottomTableRow, String> professorStringCellEditEvent) {
        AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
        String pUsername = adminBottomTableRow.getUsername();
        String newSurname = professorStringCellEditEvent.getNewValue();             //Prende il nuovo valore della colonna
        if (!(newSurname.matches("[A-Za-z\\s']+$"))) {
            profTable.refresh();
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI DOVE NON DOVREBBERO ESSERCI!!! SISTEMA PER COMPLETARE L'AGGIUNTA");
            syntaxAlert.show();
        } else {
            adminBottomTableRow.setSurname(newSurname);
            model.updateSurname(pUsername, newSurname);
        }
    }

    public void addTeaching(ActionEvent actionEvent) {

        if (removed) {
            teaching = false;
            removed = false;
        }
        //MaxSize nomeProf e cognomeProf 24 caratteri
        try {
            AdminBottomTableRow prof = profTable.getSelectionModel().getSelectedItem();
            profName.setText(prof.getName());
            profName.setVisible(true);
            profSurname.setText(prof.getSurname());
            profSurname.setVisible(true);
            ArrayList<String> teachings = model.getTeachingsOf(prof.getUsername());
            if (teachingTable.getItems().isEmpty()) {
                for (String s : teachings) {
                    teachingTable.getItems().add(s);
                }
            }
            if (!(teaching)) {
                removing = false;
                if (addingAStudent) {
                    for (int i = 0; i < textes.size(); i++) {
                        textes.get(i).setText("");
                        textes.get(i).setVisible(false);
                        lables.get(i).setVisible(false);
                    }
                    addingAStudent = false;
                } else {
                    for (int i = 0; i < 4; i++) {
                        textes.get(i).setVisible(false);
                        lables.get(i).setVisible(false);
                        textes.get(i).setText("");
                    }
                }
                subjectTextField.setVisible(true);
                subjectLabel.setVisible(true);
                classroomTeachingTextField.setVisible(true);
                classroomTeachingLabel.setVisible(true);
                teachingTable.setVisible(true);

                teaching = true;
                cancelButton.setVisible(false);
            } else {
                if (subjectTextField.getText().isEmpty() || classroomTeachingTextField.getText().isEmpty()) {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("UNO O PIU' CAMPI DI TESTO SONO VUOTI. RIEMPILI PER COMPLETARE L'AGGIUNTA");
                    syntaxAlert.show();
                } else if (!(subjectTextField.getText().matches("[a-zA-Z\\s]+$"))) {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("LA MATERIA DEVE ESSERE FORMATA SOLO DA LETTERE");
                    syntaxAlert.show();
                } else if (!(classroomTeachingTextField.getText().matches("[1-5][A-Z]"))) {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("LA CLASSE DEVE ESSERE FORMATA DA UNA CIFRA SEGUITA DA UNA LETTERA!");
                    syntaxAlert.show();
                } else {
                    boolean g = model.insertTeaching(prof.getUsername(), classroomTeachingTextField.getText(), subjectTextField.getText());
                    if (g) {
                        teachingTable.getItems().add(subjectTextField.getText() + " " + classroomTeachingTextField.getText());
                        subjectTextField.setText("");
                        classroomTeachingTextField.setText("");
                    } else {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("IL PROFESSORE INSEGNA GIA' LA MATERIA " + subjectTextField.getText() + " NELLA CLASSE " + classroomTeachingTextField.getText());
                        syntaxAlert.show();
                    }
                }
            }
        } catch (RuntimeException e) {
            syntaxAlert.setHeaderText("DEVI SELEZIONARE UN PROFESSORE PER POTERVI INSERIRE L'INSEGNAMENTO!");
            syntaxAlert.show();
        }
    }

    public void clicked(MouseEvent mouseEvent) {
        if(studentsTable.getSelectionModel().getSelectedItem()!=null){
            studentsTable.getSelectionModel().clearSelection();
        }
        try {
            AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
            if (teaching) {
                profSurname.setText(adminBottomTableRow.getSurname());
                profName.setText(adminBottomTableRow.getName());
            }
            teachingTable.getItems().clear();
            teachingTable.getItems().addAll(model.getTeachingsOf(adminBottomTableRow.getUsername()));
            teachingTable.refresh();
        } catch (RuntimeException e) {
            ;
        }
    }

    public void removeTeachingFromTable(ActionEvent actionEvent) {
        AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
        if (adminBottomTableRow != null) {
            if (!(teaching)) {
                if (addingAStudent) {
                    for (int i = 0; i < textes.size(); i++) {
                        textes.get(i).setText("");
                        textes.get(i).setVisible(false);
                        lables.get(i).setVisible(false);
                    }
                    addingAStudent = false;
                } else {
                    for (int i = 0; i < 4; i++) {
                        textes.get(i).setVisible(false);
                        lables.get(i).setVisible(false);
                        textes.get(i).setText("");
                    }
                }
                profName.setVisible(false);
                profSurname.setVisible(false);
                teachingTable.setVisible(true);
                ArrayList<String> teachings = model.getTeachingsOf(adminBottomTableRow.getUsername());
                if (teachingTable.getItems().isEmpty()) {
                    for (int i = 0; i < teachings.size(); i++) {
                        teachingTable.getItems().add(teachings.get(i));
                    }
                }
            }
            if (removing || teaching) {
                String profTeaching = teachingTable.getSelectionModel().getSelectedItem();
                if (profTeaching != null) {
                    String subject = profTeaching.substring(0, profTeaching.lastIndexOf(" "));
                    String classroom = profTeaching.substring(profTeaching.lastIndexOf((" ")) + 1);
                    confirmationAlert.setTitle("SEI SICURO?");
                    confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO INSEGNAMENTO AL PROFESSORE?");
                    confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        teachingTable.getItems().remove(profTeaching);
                        model.removeTeaching(adminBottomTableRow.getUsername(), classroom, subject);
                    }
                    //Ti passo nella funzione remove teaching l'username del prof, la materia e la classroom associata alla materia
                    //se profTeaching="Scienza dei minerali 2A", subject = "Scienza dei minerali", classroom = "2A"
                    //string.substring(0, lastIndexOf("element")) prende la stringa fino all'ultimo elemento specificato
                    //string.substring(lastIndexOf("element") + 1) prende l'ultima parola della stringa (se element = " ")
                }
            }
            removing = true;
        } else {
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("DEVI SELEZIONARE UN PROFESSORE PER POTERVI RIMUOVERE L'INSEGNAMENTO!");
            syntaxAlert.show();
        }
    }

    public void profStudentsTextFieldsEvents(KeyEvent event) {
        switch (event.getCode()) {
            case UP -> {
                int index = -1;
                for (int i = 0; i < textes.size(); i++) {
                    if (textes.get(i).isFocused()) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    if (index == 0) {
                        if (addingAStudent) {
                            classroomTextField.requestFocus();
                        } else {
                            surnameTextField.requestFocus();
                        }
                    } else {
                        textes.get(index - 1).requestFocus();
                    }
                }
            }
            case DOWN -> {
                int index = -1;
                for (int i = 0; i < textes.size(); i++) {
                    if (textes.get(i).isFocused()) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    if (addingAStudent) {
                        if (classroomTextField.isFocused()) {
                            usernameTextField.requestFocus();
                        } else {
                            textes.get(index + 1).requestFocus();
                        }
                    } else {
                        if (surnameTextField.isFocused()) {
                            usernameTextField.requestFocus();
                        } else {
                            textes.get(index + 1).requestFocus();
                        }
                    }
                }
            }
            case ENTER -> {
                //Praticamente mezzo codice di addStudent
                boolean duplicate = false;
                if (addingAStudent) {
                    for (int j = 0; j < 5; j++) {
                        if (textes.get(j).getText().isEmpty()) {
                            syntaxAlert.setTitle("ERRORE");
                            syntaxAlert.setHeaderText("ERRORE");
                            syntaxAlert.setContentText("CI SONO DELLE SEZIONI VUOTE! RIEMPILE PER COMPLETARE L'AGGIUNTA!");
                            syntaxAlert.show();
                            incomplete = true;
                            break;
                        }
                        if (j == 4) {
                            incomplete = false;
                        }
                    }
                    if ((!(usernameTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$")) || !(passwordTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$"))) && !incomplete) {

                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("CI SONO DEI SIMBOLI NON CONSENTITI NELLA SEZIONE USERNAME/PASSWORD! SONO CONSENTITI SOLO ALFANUMERICI, '_' , '-' , '.' E '@', E NON DEVONO ESSERCI SPAZI");
                        syntaxAlert.show();

                        incomplete = true;
                    }
                    if(!incomplete && passwordTextField.getText().length()<5){
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("LA PASSWORD DEVE CONTENERE ALMENO 4 CARATTERI!");
                        syntaxAlert.show();
                        incomplete = true;
                    }

                    if (!(nameTextField.getText().matches("[a-zA-Z\\s']+$") && surnameTextField.getText().matches("[a-zA-Z\\s']+$")) && !incomplete) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI NELLA SEZIONE NOME O NELLA SEZIONE COGNOME! SISTEMA PER COMPLETARE L'AGGIUNTA");
                        syntaxAlert.show();
                        incomplete = true;
                    }

                    if (!(classroomTextField.getText().matches("[1-5][A-Z]")) && !incomplete) {      //inizia dal primo carattere e passa al secondo
                        studentsTable.refresh();                                                           //la regex dice se classe è formato da un numero da 1 a 5 seguito da una lettera e basta
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("NELLA SEZIONE CLASSE DEVI INSERIRE UN NUMERO DA 1 A 5 SEGUITO DA UNA LETTERA E BASTA!!!");
                        syntaxAlert.show();
                        incomplete = true;
                    }

                    if (!(incomplete)) {
                        for (AdminTopTableRow s : studentsTable.getItems()) {
                            if (Objects.equals(s.getUsername(), usernameTextField.getText())) {
                                syntaxAlert.setTitle("ERRORE");
                                syntaxAlert.setHeaderText("ERRORE");
                                syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI!");
                                syntaxAlert.show();
                                duplicate = true;
                                break;
                            }
                        }
                        for (AdminBottomTableRow adminBottomTableRow : profTable.getItems()) {
                            if (Objects.equals(adminBottomTableRow.getUsername(), usernameTextField.getText())) {
                                syntaxAlert.setTitle("ERRORE");
                                syntaxAlert.setHeaderText("ERRORE");
                                syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI A QUELLI DEI PROFESSORI!");
                                syntaxAlert.show();
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            AdminTopTableRow adminTopTableRow;
                            adminTopTableRow = new AdminTopTableRow(usernameTextField.getText(), nameTextField.getText(), surnameTextField.getText(), classroomTextField.getText());
                            studentsTable.getItems().add(adminTopTableRow);
                            model.insertStudent(adminTopTableRow, passwordTextField.getText());
                            for (int i = 0; i < 5; i++) {
                                textes.get(i).setText("");
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 4; j++) {
                        if ((textes.get(j).getText().isEmpty())) {
                            syntaxAlert.setTitle("ERRORE");
                            syntaxAlert.setHeaderText("ERRORE");
                            syntaxAlert.setContentText("CI SONO DELLE SEZIONI VUOTE! RIEMPILE PER COMPLETARE L'AGGIUNTA!");
                            syntaxAlert.show();
                            incomplete = true;
                            break;
                        }
                        if (j == 3) {
                            incomplete = false;
                        }
                    }
                    if ((!(usernameTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$")) || !(passwordTextField.getText().matches("[a-zA-Z0-9_\\-.@]+$"))) && !incomplete) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("CI SONO DEI CARATTERI NON CONSENTITI NELLA SEZIONE USERNAME/PASSWORD! SONO CONSENTITI SOLO ALFANUMERICI, '_' , '-' , '.' , E '@'");
                        syntaxAlert.show();
                        incomplete = true;
                    }
                    if(!incomplete && passwordTextField.getText().length()<5){
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("LA PASSWORD DEVE CONTENERE ALMENO 4 CARATTERI!");
                        syntaxAlert.show();
                        incomplete = true;
                    }
                    if (!(nameTextField.getText().matches("[a-zA-Z\\s']+$") && surnameTextField.getText().matches("[a-zA-Z\\s']+$")) && !incomplete) {
                        syntaxAlert.setTitle("ERRORE");
                        syntaxAlert.setHeaderText("ERRORE");
                        syntaxAlert.setContentText("CI SONO DEI NUMERI/SIMBOLI NELLA SEZIONE NOME O NELLA SEZIONE COGNOME! SISTEMA PER COMPLETARE L'AGGIUNTA");
                        syntaxAlert.show();
                        incomplete = true;
                    }

                    if (!(incomplete)) {
                        duplicate = false;
                        for (AdminTopTableRow s : studentsTable.getItems()) {
                            if (Objects.equals(s.getUsername(), usernameTextField.getText())) {
                                syntaxAlert.setTitle("ERRORE");
                                syntaxAlert.setHeaderText("ERRORE");
                                syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI A QUELLI DEI PROFESSORI!");
                                syntaxAlert.show();
                                duplicate = true;
                                break;
                            }
                        }
                        for (AdminBottomTableRow prof : profTable.getItems()) {
                            if (Objects.equals(prof.getUsername(), usernameTextField.getText())) {
                                syntaxAlert.setTitle("ERRORE");
                                syntaxAlert.setHeaderText("ERRORE");
                                syntaxAlert.setContentText("NON POSSONO ESSERCI STUDENTI CON USERNAME UGUALI!");
                                syntaxAlert.show();
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            AdminBottomTableRow prof;
                            prof = new AdminBottomTableRow(usernameTextField.getText(), nameTextField.getText(), surnameTextField.getText());
                            profTable.getItems().add(prof);
                            model.insertProfessor(prof, passwordTextField.getText());

                            for (int i = 0; i < 4; i++) {
                                textes.get(i).setText("");
                            }
                        }
                    }
                }
                studentClicksCount++;
                removing = false;
            }
        }
    }

    public void removeWithBackSpaceOrCanc(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            if (studentsTable.isFocused()) {
                AdminTopTableRow s = studentsTable.getSelectionModel().getSelectedItem();
                if (s != null) {
                    confirmationAlert.setTitle("SEI SICURO?");
                    confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO STUDENTE?");
                    confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        studentsTable.getItems().remove(s);
                        model.removeStudent(s.getUsername());
                    }

                }
            } else if (profTable.isFocused()) {
                AdminBottomTableRow p = profTable.getSelectionModel().getSelectedItem();
                if (p != null) {
                    confirmationAlert.setTitle("SEI SICURO?");
                    confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO PROFESSORE?");
                    confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        profTable.getItems().remove(p);
                        model.removeProfessor(p.getUsername());
                    }
                }
            }
        }
    }

    public void teachingTextFieldEvents(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.DOWN) {
            if (subjectTextField.isFocused()) {
                classroomTeachingTextField.requestFocus();
            } else if (classroomTeachingTextField.isFocused()) {
                subjectTextField.requestFocus();
            }
        }
        if (code == KeyCode.ENTER) {
            if (subjectTextField.getText().isEmpty() || classroomTeachingTextField.getText().isEmpty()) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("UNO O PIU' CAMPI DI TESTO SONO VUOTI. RIEMPILI PER COMPLETARE L'AGGIUNTA");
                syntaxAlert.show();
            }
            if (!(subjectTextField.getText().matches("[a-zA-Z\\s]+$"))) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("LA MATERIA DEVE ESSERE FORMATA SOLO DA LETTERE");
                syntaxAlert.show();
            } else if (!(classroomTeachingTextField.getText().matches("[1-5][A-Z]"))) {
                syntaxAlert.setTitle("ERRORE");
                syntaxAlert.setHeaderText("ERRORE");
                syntaxAlert.setContentText("LA CLASSE DEVE ESSERE FORMATA DA UNA CIFRA SEGUITA DA UNA LETTERA!");
                syntaxAlert.show();
            } else {
                AdminBottomTableRow prof = profTable.getSelectionModel().getSelectedItem();
                boolean g = model.insertTeaching(prof.getUsername(), classroomTeachingTextField.getText(), subjectTextField.getText());
                if (g) {
                    teachingTable.getItems().add(subjectTextField.getText() + " " + classroomTeachingTextField.getText());
                    subjectTextField.setText("");
                    classroomTeachingTextField.setText("");
                } else {
                    syntaxAlert.setTitle("ERRORE");
                    syntaxAlert.setHeaderText("ERRORE");
                    syntaxAlert.setContentText("IL PROFESSORE INSEGNA GIA' LA MATERIA " + subjectTextField.getText() + " NELLA CLASSE " + classroomTeachingTextField.getText());
                    syntaxAlert.show();
                }
            }
        }
    }

    public void removeTeachingWithBackSpaceOrCanc(KeyEvent event) {
        if (event.getCode().equals(KeyCode.BACK_SPACE) || event.getCode().equals(KeyCode.DELETE)) {
            String profTeaching = teachingTable.getSelectionModel().getSelectedItem();
            AdminBottomTableRow adminBottomTableRow = profTable.getSelectionModel().getSelectedItem();
            if (profTeaching != null) {
                String subject = profTeaching.substring(0, profTeaching.lastIndexOf(" "));
                String classroom = profTeaching.substring(profTeaching.lastIndexOf((" ")) + 1);
                confirmationAlert.setTitle("SEI SICURO?");
                confirmationAlert.setHeaderText("SEI SICURO DI VOLER RIMUOVERE QUESTO INSEGNAMENTO AL PROFESSORE?");
                confirmationAlert.setContentText("PREMI OK PER RIMUOVERE, ANNULLA PER NON RIMUOVERE");
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    teachingTable.getItems().remove(profTeaching);
                    model.removeTeaching(adminBottomTableRow.getUsername(), classroom, subject);
                }
            }
        }
    }

    public void checkRequest(ActionEvent actionEvent) {
        if (!screenOpened) {
            View.requestsWindow();
            requestsStage.setOnCloseRequest(windowEvent -> {
                screenOpened = false;
            });
            screenOpened = true;
        } else {
            syntaxAlert.setTitle("ERRORE");
            syntaxAlert.setHeaderText("ERRORE");
            syntaxAlert.setContentText("LA SCHERMATA E' GIA' APERTA");
            syntaxAlert.show();
        }
    }


    public void setAdminLight(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Light");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : black");
        }
        studentsTable.getStylesheets().clear();
        profTable.getStylesheets().clear();
        studentsTable.setStyle("-fx-background-color: #f2f1f0; -fx-table-cell-border-color: #d6d6d6; -fx-selection-bar: cornflowerblue ");
        profTable.setStyle("-fx-control-inner-background: #f2f1f0; -fx-table-cell-border-color: #d6d6d6;-fx-selection-bar: cornflowerblue");
        teachingTable.setStyle("-fx-control-inner-background: #f2f1f0; -fx-table-cell-border-color: #d6d6d6; -fx-selection-bar: cornflowerblue ");
        leftPane.setStyle("-fx-background-color: whitesmoke");
        rightPane.setStyle("-fx-background-color: whitesmoke");
        profName.setStyle("-fx-text-fill: black");
        profSurname.setStyle("-fx-text-fill: black");
        subjectLabel.setStyle("-fx-text-fill: black");
        classroomTeachingLabel.setStyle("-fx-text-fill: black");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }

    public void setAdminDark(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Dark");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : white");
        }

        studentsTable.setStyle("-fx-control-inner-background: #202020; -fx-table-cell-border-color: white; -fx-selection-bar: dimgrey ");
        profTable.setStyle("-fx-control-inner-background: #202020; -fx-table-cell-border-color: white; -fx-selection-bar: dimgrey ");
        teachingTable.setStyle("-fx-control-inner-background: #202020; -fx-table-cell-border-color: white; -fx-selection-bar: dimgrey ");
        leftPane.setStyle("-fx-background-color: #1a1a1a");
        rightPane.setStyle("-fx-background-color: #1a1a1a");
        profName.setStyle("-fx-text-fill: White");
        profSurname.setStyle("-fx-text-fill: White");
        subjectLabel.setStyle("-fx-text-fill: White");
        classroomTeachingLabel.setStyle("-fx-text-fill: White");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: blue ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }

    public void setAdminPurple(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Purple");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : white");
        }
        studentsTable.setStyle("-fx-control-inner-background: #690099; -fx-table-cell-border-color: white; -fx-selection-bar: #230033 ");
        profTable.setStyle("-fx-control-inner-background: #690099; -fx-table-cell-border-color: white; -fx-selection-bar: #230033 ");
        teachingTable.setStyle("-fx-control-inner-background: #690099; -fx-table-cell-border-color: white; -fx-selection-bar: #230033 ");
        leftPane.setStyle("-fx-background-color: #690099");
        rightPane.setStyle("-fx-background-color: #690099");
        profName.setStyle("-fx-text-fill: White");
        profSurname.setStyle("-fx-text-fill: White");
        subjectLabel.setStyle("-fx-text-fill: White");
        classroomTeachingLabel.setStyle("-fx-text-fill: White");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: gold ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }

    public void setAdminBlue(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Blue");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : white");
        }
        studentsTable.setStyle("-fx-control-inner-background: #000099; -fx-table-cell-border-color: white; -fx-selection-bar: slategrey ");
        profTable.setStyle("-fx-control-inner-background: #000099; -fx-table-cell-border-color: white; -fx-selection-bar: slategrey ");
        teachingTable.setStyle("-fx-control-inner-background: #000099; -fx-table-cell-border-color: white; -fx-selection-bar: slategrey ");
        leftPane.setStyle("-fx-background-color: #000099");
        rightPane.setStyle("-fx-background-color: #000099");
        profName.setStyle("-fx-text-fill: White");
        profSurname.setStyle("-fx-text-fill: White");
        subjectLabel.setStyle("-fx-text-fill: White");
        classroomTeachingLabel.setStyle("-fx-text-fill: White");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: Darkgray ; -fx-text-fill: black ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }

    }

    public void setAdminRed(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Red");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : white");
        }
        studentsTable.setStyle("-fx-control-inner-background: FireBrick; -fx-table-cell-border-color: white; -fx-selection-bar: darkcyan ");
        profTable.setStyle("-fx-control-inner-background: FireBrick; -fx-table-cell-border-color: white; -fx-selection-bar: darkcyan ");
        teachingTable.setStyle("-fx-control-inner-background: FireBrick; -fx-table-cell-border-color: white; -fx-selection-bar: darkcyan ");
        leftPane.setStyle("-fx-background-color: FireBrick");
        rightPane.setStyle("-fx-background-color: FireBrick");
        profName.setStyle("-fx-text-fill: white");
        profSurname.setStyle("-fx-text-fill: white");
        subjectLabel.setStyle("-fx-text-fill: white");
        classroomTeachingLabel.setStyle("-fx-text-fill: white");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: Turquoise; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }

    public void setAdminGreen(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Green");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : white");
        }
        studentsTable.setStyle("-fx-control-inner-background: #0b4d12; -fx-table-cell-border-color: white; -fx-selection-bar: #4d1d0b");
        profTable.setStyle("-fx-control-inner-background: #0b4d12; -fx-table-cell-border-color: white; -fx-selection-bar: #4d1d0b");
        teachingTable.setStyle("-fx-control-inner-background: #0b4d12; -fx-table-cell-border-color: white; -fx-selection-bar: #4d1d0b ");
        leftPane.setStyle("-fx-background-color: #0b4d12");
        rightPane.setStyle("-fx-background-color: #0b4d12");
        profName.setStyle("-fx-text-fill: white");
        profSurname.setStyle("-fx-text-fill: white");
        subjectLabel.setStyle("-fx-text-fill: white");
        classroomTeachingLabel.setStyle("-fx-text-fill: white");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: saddleBrown; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }

    public void setAdminYellow(ActionEvent actionEvent) {
        Model.getInstance().updateTheme(View.username_logged, "Yellow");
        for (Label lable : lables) {
            lable.setStyle("-fx-text-fill : black");
        }
        studentsTable.setStyle("-fx-control-inner-background: goldenrod; -fx-table-cell-border-color: black; -fx-selection-bar:yellowgreen ");
        profTable.setStyle("-fx-control-inner-background: goldenrod; -fx-table-cell-border-color: black;  -fx-selection-bar: yellowgreen");
        teachingTable.setStyle("-fx-control-inner-background: goldenrod; -fx-table-cell-border-color: black; -fx-selection-bar: yellowgreen");

        leftPane.setStyle("-fx-background-color: goldenrod");
        rightPane.setStyle("-fx-background-color: goldenrod");
        profName.setStyle("-fx-text-fill: Black");
        profSurname.setStyle("-fx-text-fill: black");
        subjectLabel.setStyle("-fx-text-fill: black");
        classroomTeachingLabel.setStyle("-fx-text-fill: black");
        for (Button button : addButtons) {
            button.setStyle("-fx-background-color: green ; -fx-text-fill: white ; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 1;");
        }
    }



    public void importer(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Custom Input Dialog");

        SheettyModel sheettyModel = new SheettyModel();

        // Crea un TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");

        // Imposta il testo come HeaderText
        dialog.getDialogPane().setHeaderText(sheettyModel.help);

        // Imposta il contenuto del DialogPane
        dialog.getDialogPane().setContentText("Percorso del file:");

        // Mostra il dialog e ottieni la risposta
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(enteredText -> {

            // Accedi al testo del TextField
            String textFieldValue = dialog.getEditor().getText();
            int i = sheettyModel.importFrom(textFieldValue);
            studentsTable.getItems().clear();
            profTable.getItems().clear();
            studentsTable.getItems().addAll(model.getAdminTopTableRows());
            profTable.getItems().addAll(model.getAdminBottomTableRows());
        });    }

    public void studentclicked(MouseEvent mouseEvent) {
        if(profTable.getSelectionModel().getSelectedItem()!=null){
            profTable.getSelectionModel().clearSelection();
        }
    }
}


