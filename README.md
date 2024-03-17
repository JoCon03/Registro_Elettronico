###Credits: 
- JoCon03 --> AdminController/fxml, RequestPasswordController/fxml, RequestsScreenController/fxml, ProfController, AbsencesController, AbsencesScreen.fxml
- Lorybilo02 --> ProfController, Professor.fxml, StudentController/fxml, LoginController/fxml, View, all CSS files 
- Sim23F --> Model, ShettyModel, all TableRows Classes, all Databases, README 

# Registro Elettronico (ITA)
Il **Registro Elettronico** é una piattoforma digitale che gestisce e immagazina in maniera sicura ed efficiente i dati di un istituto scolastico, rendendo facile consultarli e modificarli tramite un interfaccia grafica minimale ed intuitiva.

Gli utenti della piattaforma sono divisi in tre categorie: Amministratore, Professori e Studenti. 
Ad ogni utente é associato un username univoco ed una password, con i quali puó accedere al sistema.

L'utente **Amministratore** é destinato ai gestori della scuola, come la presidenza o la segreteria.
Esso viene configurato manualmente dal personale autoratizzato, al momento dell'acquisto dell’applicazione.
All’amministratore é affidata la configurazione iniziale del sistema e la creazione degli altri utenti.

Gli utenti di tipo **Professore** sono destinati ai docenti scolastici.
Ad ogni Professore sono assegnati nome, cognome e  insegnamenti (associazioni classe-materia).
I Professori possono utilizzare il sistema per registrare le presenze e i voti assegnati agli studenti.

Gli utenti di tipo **Studente** sono destinati agli studenti che frequentano la scuola.
Ad ogni Studente sono assegnati nome, cognome, classe frequentata, numero di assenze, voti e informazioni correlate.

## DataBase Structure
- Admin( **id**, school, password, theme, login_theme, password_requests )
- Professor( **id**, name, surname, password, theme )
- Student( **id**, name, surname, *classroom*, password, theme )
- Teaching( **id**, *professor*, *classroom*, *subject* ) 
- Grade( **id**, *student*, *teaching*, date, comment, value )
- Absence( **id**, *student*, date )
- Classroom( **id** )
- Subject( **id** )

## Database Creation Query 
create table Admin(id text primary key, school txt, password text, theme text, login_theme text, password_requests text);
create table Professor(id text primary key, name txt, surname txt, password text, theme text);
create table Student(id text primary key, name txt, surname txt, classroom txt, password text, theme text, 
  foreign key(classroom) references Classroom(id));
create table Classroom(id text primary key);
create table Subject(id text primary key);
create table Teaching(id integer primary key autoincrement, professor text, classroom text, subject text,
  foreign key(professor) references Professor(id), foreign key (classroom) references Classroom(id), foreign key (subject) references Subject(id));
create table Grade(id integer primary key autoincrement, student text, teaching integer, date text, comment text, value real, 
  foreign key(student) references Student(id), foreign key(teaching) references Teaching(id));
create table Absence(id integer primary key autoincrement, student text, date text, foreign key(student) references Student(id));

### Admin Profile Configuration Example
- username: Admin
- password: 1234
insert into admin(id, password, school, theme, login_theme, password_requests) 
  values("Admin", "$2a$12$5JvPIwVk2fDdTeRtH3l4XOy2TlTvDVigZMqK58Eao/fEtbRF8w2dW", "School Name", "Light", "Light", "");  
