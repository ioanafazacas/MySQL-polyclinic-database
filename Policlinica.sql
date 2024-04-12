CREATE DATABASE IF NOT EXISTS policlinici7;
use policlinici7;

create table if not exists policlinica
(id_policlinica int primary key, id_UM int,id_venit_policlinica int);

create table if not exists adresa
(id int auto_increment primary key,oras varchar(50),
strada varchar(50));

create table if not exists persoana
(CNP varchar(14) primary key, nume varchar(50),
prenume varchar(50), id_adresa int, nr_telefon varchar(11),email varchar(30),
IBAN varchar(20));

create table if not exists angajat
(id_angajat int primary key,CNP varchar(14), nr_contract int, data_angajari date,
departament enum('uman','financiar-contabil','medical'),
functia enum('inspector resurse umane','expert financiar contabil','receptioner','medic','asistent medical'),
salariu int, nr_ore int);

create table if not exists utilizator
(id_angajat int primary key, username varchar(50), parola varchar(50),
tip enum('angajat','administrator','super-administrator'));

create table if not exists asistent_medical
(id_angajat int primary key,tip enum('generalist','laborator','radiologie'),
grad enum ('principal','secundar'));

create table if not exists medic
(id_angajat int primary key, id_specialitati int, grad enum('specialist','primar'),
cod_parafa varchar(30),id_competente int, titlu_stiintific enum('doctorand','doctor in stiinte medicale'),
postul_didactic enum('preparator','asistent','sef de lucrari','conferentiar', 'profesor'),
procent_aditional int);



create table if not exists competente
(id int primary key, ecografie boolean,endoscopie_digitala boolean, ecocardiografie boolean,cardiologie_interventionala boolean, bronhoscopie boolean,
EEG boolean, EMG boolean, dializa boolean,chirurgie_laparoscopica boolean,chirurgie_toracica boolean,chirurgie_spinala boolean,
CT boolean);

alter table competente add column id_medic int after id;
alter table competente add column id_servicii int after id_medic;


create table if not exists specialitati
(id int primary key,boli_infectioase boolean, cardiologie boolean, chirurgie boolean, dermato_estetica boolean,
dermatovenerologie boolean, diabet_nutritie boolean, medicina_generala boolean, ORL boolean, neurologie boolean,
obstretica_ginecologie boolean, pediatrie boolean, ortopedie boolean, oftalmologie boolean);

create table if not exists servicii
(id int primary key, nume_serviciu varchar(50),pret int not null, durata time,id_competente int, id_specialitate int);
alter table servicii drop column id_competente;

create table if not exists concedii
(id int primary key, id_angajat int, data_incepere date, data_finalizare date,nr_zile int,zile_ramase int default 30);

drop table salarii;
create table if not exists salarii
(id_salarii int primary key auto_increment, id_angajat int, suma int, an int,luna int);

create table if not exists programari
(id_programare int primary key auto_increment, data_programarii date, id_servicii int, id_medic int,pret_medic int, durata_consultatie time,durata_consultatie_medic int);
alter table programari add column cnp_pacient varchar(14) after id_programare;
create table if not exists pacient
(id_pacient int primary key, cnp varchar(14), id_raport int);


drop table raport_medical;
create table if not exists raport_medical
(id int primary key auto_increment, id_pacient int, id_medic int, id_asistent int, data_consult date, bon int, simptome text,
id_investigatii int, diagnostic varchar(50), recomandari text);
alter table raport_medical modify id int auto_increment;
create table if not exists unitate_medicala
(id int primary key, denumire varchar(50), id_adresa int, id_servicii int, program text);

create table if not exists istoric
(id int primary key,id_raport int);

drop table venit_lunar;
create table if not exists venit_lunar
(id int auto_increment PRIMARY KEY,an int,luna int,venit int, incasari int, cheltuieli int );

create table if not exists investigatii
(id_raport int , id_servicii int,
PRIMARY KEy(id_raport,id_servicii));

create table if not exists program_angajat_unitate
(id int primary key, id_angajat int, id_unitate int,timp_start time, timp_finish time);

SET FOREIGN_KEY_CHECKS=0;
SET SQL_SAFE_UPDATES = 0;

alter table policlinica add constraint foreign key (id_policlinica) references venit_lunar(id);
drop table raport_medical;
alter table angajat add column zile_ramase_concediu int default 30;
alter table concedii drop column zile_ramase;
alter table program_angajat_unitate add constraint foreign key (id_angajat) references angajat(id_angajat);
alter table program_angajat_unitate add constraint foreign key (id_unitate) references unitate_medicala(id);

alter table raport_medical add column durata_consultatie time;

alter table unitate_medicala add constraint foreign key (id_adresa) references adresa(id);
alter table unitate_medicala add constraint foreign key (id_servicii) references servicii(id);

alter table persoana add constraint foreign key (id_adresa) references adresa(id);

alter table angajat add constraint foreign key (CNP) references persoana(CNP);

alter table utilizator add constraint foreign key (id_angajat) references angajat(id_angajat);

alter table medic add constraint foreign key (id_angajat) references angajat(id_angajat);
alter table medic add constraint foreign key (id_competente) references competente(id);
alter table medic add constraint foreign key (id_specialitati) references specialitati(id);

alter table asistent_medical add constraint foreign key (id_angajat) references angajat(id_angajat);

alter table raport_medical add constraint foreign key (id_medic) references medic(id_angajat);
alter table raport_medical add constraint foreign key (id_asistent) references asistent_medical(id_angajat);
alter table raport_medical add constraint foreign key (id_pacient) references pacient(id_pacient);
#alter table raport_medical add constraint foreign key (id_investigatii) references investigatii(id);

alter table investigatii add foreign key (id_raport) references raport_medical(id); 
alter table investigatii add foreign key (id_servicii) references servicii(id); 

alter table competente add constraint foreign key (id_medic) references medic(id_angajat);
alter table competente add constraint foreign key (id_servicii) references servicii(id);

alter table pacient add constraint foreign key (CNP) references persoana(CNP);
alter table pacient add constraint foreign key (id_raport) references raport_medical(id);

alter table istoric add constraint foreign key (id_raport) references raport_medical(id);

alter table programari add constraint foreign key (id_servicii) references servicii(id);
alter table programari add constraint foreign key (id_medic) references medic(id_angajat);
alter table programari add constraint foreign key (cnp_pacient) references persoana(cnp);

alter table policlinica add constraint foreign key (id_UM) references unitate_medicala(id);

alter table salarii add constraint foreign key (id_angajat) references angajat(id_angajat);

alter table concedii add constraint foreign key(id_angajat) references angajat(id_angajat);