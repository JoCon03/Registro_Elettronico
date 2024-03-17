package com.example.project;

import java.io.*;
import java.util.ArrayList;

// This class allow to insert users automatically from a spreadsheet into the database.
public class SheettyModel {

    public final String help = """
            Questo strumento consente di aggiungere automaticamente studenti e professori a partire da un file con estensione csv.
            
            Per garantire il corretto funzionamento, il file selezionato deve rispettare le seguenti regole:
                       
                La prima riga del file CSV deve contenere l'intestazione delle colonne.
                Le righe successive devono contenere i valori corrispondenti per ciascuna colonna.
                Ogni riga rappresenta un nuovo utente.
                `username`, `password`, `name`, `surname`, `classroom` sono le intestazioni consentite.
                Le prime quattro sono richieste per studenti e professori, mentre `classroom` é richiesta solo per gli studenti.
                Per un file di soli professori la colonna `classroom` puó essere omessa, mentre per un file misto deve essere lasciata in bianco.
                L'ordine delle colonne non è importante. Lo strumento elaborerà il file correttamente indipendentemente dall'ordine delle colonne.
            
            Se è presente un errore di sintassi nel file la funzione di elaborazione si interromperà automaticamente.
            Assicurati che il tuo file rispetti rigorosamente le regole specificate per evitare fastidiose interruzioni.
            Gli utenti caricati prima di incontrare l'errore verranno comunque salvati.
            """;

    // return the number of the inserted users
    public int importFrom(String path)  {

        int inserted = 0;

        if( ! path.endsWith(".csv") ) return inserted;

        try( BufferedReader reader = new BufferedReader( new FileReader(path) ) ) {

            // First line
            String line = reader.readLine();
            String[] row = line.split(",");
            boolean isThereClassroom = false;
            ArrayList<Integer> indexes = new ArrayList<>();

            for(int i = 0; i < 5; i++) {
                switch ( row[i].trim().toLowerCase() ) {
                    case "username":
                        indexes.add(0);
                        break;
                    case "password":
                        indexes.add(1);
                        break;
                    case "name":
                        indexes.add(2);
                        break;
                    case "surname":
                        indexes.add(3);
                        break;
                    case "classroom":
                        indexes.add(4);
                        isThereClassroom = true;
                        break;
                    case "": // Allow an empty column if ther isn't classroom
                        if( i == 4 && (!isThereClassroom) )
                            break;
                    default:
                        return inserted;
                }
            }

            // Other lines
            while( (line = reader.readLine()) != null ){

                row = line.split(",");

                String[] regexes = {"^[a-zA-Z0-9_\\-.@]{4,}$", "^[a-zA-Z0-9_\\-.@]{4,}$", "^[a-zA-Z\\s']{4,}$", "^[a-zA-Z\\s']{4,}$", "^[1-5][A-Z]$"};

                String[] fields = new String[5];
                int i;
                for( i = 0; i < 4; i++ )
                    fields[indexes.get(i)] = row[i].trim();

                if(isThereClassroom) {
                    fields[indexes.get(i)] = row[i];
                    if( fields[indexes.get(i)] != null ) fields[indexes.get(i)] = fields[indexes.get(i)].trim();
                } else fields[i] = "";

                for( Integer index: indexes )
                    if (!fields[index].matches(regexes[index])){
                        if( index == 4 && fields[index].isEmpty() )  // this turn classroom from "" to null
                            fields[index] = null;
                        if (!(index != 4 || fields[index] != null))  // this allow Professors to pass among Students
                            return inserted;
                    }

                if( Model.getInstance().insertUser(fields[0], fields[1], fields[2], fields[3], fields[4]) )
                    ++inserted;
            }

            return inserted;

        } catch (Exception ignored) {
            return inserted;
        }
    }

}
