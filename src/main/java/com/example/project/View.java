package com.example.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class View extends Application {
    public static Stage currentStage;
    public static String username_logged;
    public static String username_prof;
    public static Stage requestsStage;
    public static Stage absencesStage;
    @Override
    public void start(Stage stage) throws IOException {
        //ogni interfaccia corrente la indicheremo con il current stage per chiuderla all'apertura di un altra
        currentStage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(View.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 516, 361);
        // impostazione della finestra come non ridimensionabile
        stage.setResizable(false);
        //css
        scene.getStylesheets().add(getClass().getResource("/application/" + Model.getInstance().getTheme() + "Login.css").toExternalForm());
        stage.setTitle("REGISTRO ELETTRONICO");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {launch();}
    public static void adminWindow(String username) {
        username_logged = username;
        if(LoginController.themeHasChanged)
            Model.getInstance().updateTheme(username_logged, Model.getInstance().getTheme());
        else
            Model.getInstance().updateTheme(null, Model.getInstance().getTheme(username_logged));
        try {
            FXMLLoader loader = new FXMLLoader(View.class.getResource("Admin.fxml"));
            Parent root = loader.load();
            // crea una nuova finestra
            Stage FinestraAdmin = new Stage();
            FinestraAdmin.setTitle("REGISTRO ELETTRONICO: Schermata Amministratore");
            Scene scene = new Scene(root,900,600);
            FinestraAdmin.setResizable(false);
            FinestraAdmin.setScene(scene);

            // chiudi la finestra principale
            currentStage.close();
            currentStage=FinestraAdmin;
            currentStage.setOnCloseRequest(windowEvent -> {
                //se chiudo l'admin e la schermata di richeste è aperta chiudi anche essa
                if(!(requestsStage==null)) {
                    requestsStage.close();
                }
            });
            FinestraAdmin.show();
        } catch (IOException e) {}
    }

    public static void studentWindow(String username){
        if(LoginController.themeHasChanged)
            Model.getInstance().updateTheme(username, Model.getInstance().getTheme());
        else
            Model.getInstance().updateTheme(null, Model.getInstance().getTheme(username));
        try {
            username_logged = username;
            FXMLLoader loader = new FXMLLoader(View.class.getResource("Student.fxml"));
            AnchorPane root = loader.load();
            //crea una nuova finestra
            Stage FinestraStudente = new Stage();
            FinestraStudente.setTitle("REGISTRO ELETTRONICO: Schermata Studente");

            Scene scene = new Scene(root,900,600);
            scene.getStylesheets().add(View.class.getResource("/application/" + Model.getInstance().getTheme(username) + "Student.css").toExternalForm());
            FinestraStudente.setScene(scene);
            FinestraStudente.setResizable(false);
            //chiudi la finestra corrente
            currentStage.close();
            currentStage=FinestraStudente;
            FinestraStudente.show();
        } catch (IOException e) {
            // gestione dell'eccezione

        }
    }
    public static void professorWindow(String username){
        if(LoginController.themeHasChanged)
            Model.getInstance().updateTheme(username, Model.getInstance().getTheme());
        else
            Model.getInstance().updateTheme(null, Model.getInstance().getTheme(username));
        try {
            username_prof=username;
            FXMLLoader loader = new FXMLLoader(View.class.getResource("Professor.fxml"));
            Parent root = loader.load();

            // crea una nuova finestra
            Stage FinestraProfessore = new Stage();
            FinestraProfessore.setTitle("REGISTRO ELETTRONICO: Schermata Professore");

            Scene scene = new Scene(root,900,600);
            Model.getInstance().updateTheme(username, Model.getInstance().getTheme());
            scene.getStylesheets().add(View.class.getResource("/application/" + Model.getInstance().getTheme(username) + "Professor.css").toExternalForm());
            FinestraProfessore.setScene(scene);

            FinestraProfessore.setResizable(false);
            // chiudi la finestra corrente
            currentStage.close();
            currentStage=FinestraProfessore;
            // visualizza la nuova finestra
            FinestraProfessore.show();
        } catch (IOException e) {
            // gestione dell'eccezione

        }
    }

    public static void passwordRequestWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(View.class.getResource("RequestPasswordScreen.fxml"));
            Parent root = loader.load();

            // crea una nuova finestra
            Stage requestPasswordScreen = new Stage();
            requestPasswordScreen.setTitle("REGISTRO ELETTRONICO: Richiedi Password");
            // imposta il contenuto della finestra sulla radice del tuo file FXML
            Scene scene = new Scene(root,600,400);
            requestPasswordScreen.setScene(scene);

            requestPasswordScreen.setResizable(false);
            // chiudi la finestra corrente
            currentStage.close();
            currentStage=requestPasswordScreen;
            currentStage.setOnCloseRequest(windowEvent -> {
                try{
                    currentStage.close();
                    // richiama il metodo start per aprire la schermata di login
                    new View().start(new Stage());
                } catch (IOException e) {
                    // gestione dell'eccezione
                }
            });
            currentStage.show();
        } catch (IOException e) {
            // gestione dell'eccezione

        }
    }
    public static void requestsWindow(){
        try {
            FXMLLoader loader = new FXMLLoader(View.class.getResource("RequestsScreen.fxml"));
            Parent root = loader.load();

            // crea una nuova finestra
            Stage requestsScreen = new Stage();
            requestsScreen.setTitle("REGISTRO ELETTRONICO: Richeste Password");
            Scene scene = new Scene(root, 600, 400);
            requestsScreen.setScene(scene);
            requestsStage=requestsScreen;
            requestsStage.setResizable(false);
            requestsStage.show();
        } catch (IOException e) {
            // gestione dell'eccezione
        }
    }

    public static void absencesWindow(String profUsername){
        try {
            FXMLLoader loader = new FXMLLoader(View.class.getResource("AbsencesScreen.fxml"));
            Parent root = loader.load();

            // crea una nuova finestra
            Stage absencesScreen = new Stage();
            absencesScreen.setTitle("REGISTRO ELETTRONICO: Gestione Assenze");
            Scene scene = new Scene(root, 640, 400);
            absencesScreen.setScene(scene);
            currentStage.close();
            absencesStage=absencesScreen;
            absencesStage.setResizable(false);
            absencesStage.setOnCloseRequest(windowEvent -> {
                professorWindow(profUsername);
            });
            absencesStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}









