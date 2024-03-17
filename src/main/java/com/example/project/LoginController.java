package com.example.project;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import static com.example.project.View.currentStage;


public class LoginController {
    public static boolean themeHasChanged = false;
    @FXML
    public Label passwordForgotten;

    @FXML
    private TextField nome_utente;
    @FXML
    private TextField password_utente;
    @FXML
    private Button first_button;

    public void CheckButton(ActionEvent actionEvent) {
        String username = nome_utente.getText();
        String password = password_utente.getText();
        Model model = Model.getInstance();

        int result = model.loginCheck(username, password); //vede se il nome utente combacia con la passw
        if (result == -1) {
            //-1== non combacia
            first_button.setStyle("-fx-background-color: crimson;");
            showErrorAlert("ATTENZIONE,ASSICURATI DI AVER INSERITO CORRETTAMENTE LE CREDENZIALI!");
            String theme = model.getTheme();
            first_button.setStyle(null);
            //per far ritornare il bottone normale dopo l'errore(in base al tema)
            if(theme.equals("Light") || theme.equals("Dark")){first_button.getStyleClass().add("button_style_blue");}
            if(theme.equals("Purple")){first_button.getStyleClass().add("button_style_gold");}
            if(theme.equals("Blue")){first_button.getStyleClass().add("button_style_white");}
            if(theme.equals("Green")){first_button.getStyleClass().add("button_style_sienna");}
            if(theme.equals("Red")){first_button.getStyleClass().add("button_style_white");}
            if(theme.equals("Yellow")){first_button.getStyleClass().add("button_style_green");}
        }
        if (result == 0) {
            //0 è l'admin
            View.adminWindow(username);
        }
        if(result==1){

            //1 è un professore
           View.professorWindow(username);

        }

        if (result == 2) {
            //2 è uno studente
            View.studentWindow(username);
        }
    }

    public static void showErrorAlert(String messaggio) {
        //metodo per l'errore
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERRORE!");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void Submit(KeyEvent event) {
        //stessa cosa di sopra ma prende l'evento dal tasto invio
        if (event.getCode() == KeyCode.ENTER) {
            String username = nome_utente.getText();
            String password = password_utente.getText();
            Model model = Model.getInstance();
            int result = model.loginCheck(username, password);
            if (result == -1) {
                first_button.setStyle("-fx-background-color: crimson;");
                showErrorAlert("ATTENZIONE,ASSICURATI DI AVER INSERITO CORRETTAMENTE LE CREDENZIALI!");
                String theme = model.getTheme();
                first_button.setStyle(null);
                if (theme.equals("Light") || theme.equals("Dark")) {
                    first_button.getStyleClass().add("button_style_blue");
                }
                if (theme.equals("Purple")) {
                    first_button.getStyleClass().add("button_style_gold");
                }
                if(theme.equals("Blue")) {
                    first_button.getStyleClass().add("button_style_white");
                }
                if(theme.equals("Green")) {
                    first_button.getStyleClass().add("button_style_sienna");
                }
                if(theme.equals("Red")) {
                    first_button.getStyleClass().add("button_style_white");
                }
                if(theme.equals("Yellow")) {
                    first_button.getStyleClass().add("button_style_green");
                }
            }

            if (result == 0) {
                first_button.setStyle("-fx-background-color:forestGreen;");
                View.adminWindow(username);
            }
            if (result == 1) {
                first_button.setStyle("-fx-background-color:forestGreen;");
                View.professorWindow(username);
            }

            if (result == 2) {
                first_button.setStyle("-fx-background-color:forestGreen;");
                View.studentWindow(username);
            }
        }
    }

    public void set_login_theme(String theme) {
        //metodo per settare i temi e cambiare i css
        Model.getInstance().updateTheme(null, theme);
        Scene scene = currentStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/application/" + theme + "Login.css").toExternalForm());
        themeHasChanged = true;
    }
    public int set_login_light(ActionEvent actionEvent) {
        set_login_theme("Light");
        return 0;
    }
    public int set_login_dark(ActionEvent actionEvent) {
        set_login_theme("Dark");
        return 1;
    }
    public int set_login_purple(ActionEvent actionEvent) {
        set_login_theme("Purple");
        return 2;
    }
    public int set_login_blue(ActionEvent actionEvent) {
        set_login_theme("Blue");
        return 3;
    }
    public int set_login_green(ActionEvent actionEvent) {
        set_login_theme("Green");
        return 4;
    }
    public int set_login_red(ActionEvent actionEvent) {
        set_login_theme("Red");
        return 5;
    }
    public int set_login_Yellow(ActionEvent actionEvent) {
        set_login_theme("Yellow");
        return 6;
    }
    public void arrowEvents(KeyEvent event) {
        //muoversi con le frecce attraverso le label
        if(event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)){
            if(nome_utente.isFocused()){
                password_utente.requestFocus();
            } else if (password_utente.isFocused()) {
                nome_utente.requestFocus();
            }
        }
    }

    //il cursore diventa una mano
    public void hover(MouseEvent mouseEvent) {
        passwordForgotten.setCursor(Cursor.HAND);
    }
    //poi torna di defaul
    public void backToNormal(MouseEvent mouseEvent) {
        passwordForgotten.setCursor(Cursor.DEFAULT);
    }
    //apre la schermata
    public void clickedLabel(MouseEvent mouseEvent) {
        View.passwordRequestWindow();
    }
}