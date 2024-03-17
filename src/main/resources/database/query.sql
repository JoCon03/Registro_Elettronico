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
