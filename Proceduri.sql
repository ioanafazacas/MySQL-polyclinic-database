

drop procedure afisari_profit_policlinica;
DELIMITER //
CREATE PROCEDURE afisari_profit_policlinica()   

  BEGIN
    select an, luna , venit from venit_lunar;
  END //
DELIMITER ;


drop procedure afisari_salarii_angajat;
DELIMITER //
CREATE PROCEDURE afisari_salarii_angajat(id_angajat_val int )   

  BEGIN
    select an, luna , suma from salarii where id_angajat_val=salarii.id_angajat;
  END //
DELIMITER ;


drop procedure if exists programare2;
DELIMITER //
CREATE PROCEDURE programare2(IN id_servicii1 INT, IN data_programare1 DATE,in pret_medic1 int, in cnp_pacient1 varchar(14), in ora_cons text)
BEGIN
  declare id_medic_encounter int default 0;
  declare contor int default 1;
  declare ind int default 0;
  declare c1 boolean;
  declare durata_cons time;
  
  if(id_servicii1=1) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.ecografie=1 limit 1;
	end if;
    
  if(id_servicii1=2) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.endoscopie_digitala=1 limit 1;
	end if;
    
  if(id_servicii1=3) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.ecocardiografie=1 limit 1;
	end if;

  if(id_servicii1=4) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.cardiologie_interventionala=1 limit 1;
	end if;
    
  if(id_servicii1=5) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.bronhoscopie=1 limit 1;
	end if;
    
  if(id_servicii1=6) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.EEG=1 limit 1;
	end if;
    
  if(id_servicii1=7) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.EMG=1 limit 1;
	end if;
    
  if(id_servicii1=8) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.dializa=1 limit 1;
	end if;
    
  if(id_servicii1=9) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.chirurgie_laparoscopica=1 limit 1;
	end if;
    
  if(id_servicii1=10) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.chirurgie_toracica=1 limit 1;
	end if;
    
  if(id_servicii1=11) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.chirurgie_spinala=1 limit 1;
	end if;
    
  if(id_servicii1=12) then
  select m.id_angajat into id_medic_encounter from medic m 
	inner join competente c on m.id_competente=c.id 
    where c.CT=1 limit 1;
	end if;
	
    select durata into durata_cons from servicii where servicii.id=id_servicii1;
    
     insert into programari(data_programarii,id_medic,id_servicii,pret_medic,durata_consultatie,cnp_pacient,ora_programare) values
(data_programare1,id_medic_encounter,id_servicii1,pret_medic1,durata_cons,cnp_pacient1,ora_cons);
   
END;
//
DELIMITER ;

drop procedure if exists print_pacienti;
DELIMITER //
CREATE PROCEDURE print_pacienti(IN data_ date,in id_medic1 int)
BEGIN
	select nume, prenume,nr_telefon from persoana, programari where id_medic1=programari.id_medic and data_=programari.data_programarii and persoana.CNP=programari.cnp_pacient;
END;
//
DELIMITER ;



drop procedure if exists print_raport_pacienti;
DELIMITER //
CREATE PROCEDURE print_raport_pacienti(in cnp1 varchar(14))
BEGIN
	select nume, prenume,diagnostic,recomandari,nume_serviciu from pacient p, raport_medical r,servicii s,investigatii inv,angajat ang, persoana pers,medic m where cnp1=p.cnp and p.id_pacient=r.id_pacient and r.id_medic=m.id_angajat and m.id_angajat=ang.id_angajat and ang.cnp=pers.cnp and r.id=inv.id_raport and inv.id_servicii=s.id;
END;
//
DELIMITER ;


drop procedure if exists completare_raport;
DELIMITER //
CREATE PROCEDURE completare_raport(in nume_p varchar(50),in prenume_p varchar(50), in nume_m varchar(50),in prenume_m varchar(50), in nume_a varchar(50),in prenume_a varchar(50), in data_cons date, in simptome text,in id_investigatie int ,in diagnostic varchar(50), in recomandari text)
BEGIN
	
	declare id_pacient_aux int;
    declare cnp_pacient varchar(14);
    declare cnp_asistent varchar(14);
    declare cnp_medic varchar(14);
	declare id_medic_aux int;
	declare id_asistent_aux int;
	declare id_raport int default 0;
    declare durata_val time;
	declare parafa_aux varchar(30) default null;
    
    select cnp into cnp_pacient from persoana where nume_p=persoana.nume and prenume_p=persoana.prenume;
    select id_pacient into id_pacient_aux from pacient where cnp_pacient=pacient.cnp;
    
	select cnp into cnp_asistent from persoana where nume_a=persoana.nume and prenume_a=persoana.prenume;
    select id_angajat into id_asistent_aux from angajat where cnp_asistent=angajat.cnp;
    
	select cnp into cnp_medic from persoana where nume_m=persoana.nume and prenume_m=persoana.prenume;
    select id_angajat into id_medic_aux from angajat where cnp_medic=angajat.cnp;
    
    select id into id_raport from raport_medical where id_pacient_aux=id_pacient and data_consult=data_cons;
    select parafa into parafa_aux from raport_medical where id_pacient_aux=id_pacient and data_consult=data_cons;
    
    if(id_raport = 0) then
    
  #  select durata into durata_val from servicii s, investigatii i where i.id_investigatie=id_investigatie  and s.id=i.id_servicii;
   #!!!! 
    insert into raport_medical (id_pacient,id_medic,id_asistent,data_consult,id_investigatii,diagnostic,simptome,recomandari,durata_consult) values
    (id_pacient_aux,id_medic_aux,id_asistent_aux,data_cons,id_investigatie,diagnostic,simptome,recomandari,durata_val);
	end if;
    if(id_raport <> 0 and parafa_aux is null) then
    UPDATE raport_medical
SET id_pacient = id_pacient_aux,
    id_medic = id_medic_aux,
    id_asistent = id_asistent_aux,
    data_consult = data_cons,
    id_investigatii = id_investigatie,
    diagnostic = diagnostic,
    simptome = simptome,
    recomandari = recomandari
WHERE id_raport = id;
end if;
END;
//
DELIMITER ;
#in nume_p varchar(50),in prenume_p varchar(50), in nume_m varchar(50),in prenume_m varchar(50), in nume_a varchar(50),in prenume_a varchar(50), in data_cons date, in simptome text,in id_investigatie int ,in diagnostic varchar(50), in recomandari text)


DELIMITER //
CREATE PROCEDURE parafare_raport(in nume_p varchar(50),in prenume_p varchar(50),in data_cons date, in parafa_val varchar(30))
BEGIN
	declare id_pacient_aux int;
	declare id_raport int;
    declare cnp_pacient varchar(14);
    
	select cnp into cnp_pacient from persoana where nume_p=persoana.nume and prenume_p=persoana.prenume;
    select id_pacient into id_pacient_aux from pacient where cnp_pacient=pacient.cnp;
	select id into id_raport from raport_medical where id_pacient_aux=id_pacient and data_consult=data_cons;

     UPDATE raport_medical
SET parafa=parafa_val
WHERE id_raport = id;
    
END;
//
DELIMITER ;


set FOREIGN_KEY_CHECKS=0;
INSERT INTO `policlinici7`.`concedii` (`id`, `id_angajat`, `data_incepere`, `data_finalizare`, `nr_zile`) VALUES ('7', '2', '2023-05-05', '2023-05-15', '10');
drop procedure if exists update_concediu;
DELIMITER //

CREATE PROCEDURE update_concediu(IN id_val INT, IN id_angajat_val INT, IN data_incepere_val DATE, IN data_finalizare_val DATE, IN nr_zile_val INT)
BEGIN
    DECLARE zile_ramase_concediu INT;
    
    set zile_ramase_concediu=-1;
    SELECT zile_ramase INTO zile_ramase_concediu FROM concedii WHERE id = id_val;
    
    IF nr_zile_val <= zile_ramase_concediu THEN
        UPDATE concedii
        SET data_incepere = data_incepere_val,
            data_finalizare = data_finalizare_val,
            zile_ramase = zile_ramase_concediu - nr_zile_val,
            nr_zile = nr_zile_val
        WHERE id = id_val;
    END IF;
END;
//
DELIMITER ;

call update_concediu(7,3,'2023-09-10','2023-09-25',16);
DELIMITER //

CREATE PROCEDURE afisare_orar_inspector(IN nume varchar(45), IN prenume varchar(45), in functia enum('inspector resurse umane','expert financiar contabil','receptioner','medic','asistent medical'),out result int)
BEGIN
    DECLARE id_angajat_val INT ;
    set id_angajat_val=-1;
    SELECT id_angajat INTO id_angajat_val FROM angajat a, persoana p WHERE  p.CNP= a.CNP and p.nume=nume and p.prenume=prenume and a.functia=functia;
    
    Select * from program_angajat_unitate p where p.id_angajat=id_angajat_val;
   if (id_angajat_val=-1) then set result =0;
   else set result =1;
   end if;
END;
//
DELIMITER ;
set FOREIGN_KEY_CHECKS=0;


drop procedure if exists verificare_concediu;

DELIMITER //
CREATE PROCEDURE verificare_concediu(IN data_actuala date, IN id_angajat int,out result int)
BEGIN
     DECLARE id_angajat_val INT ;
	set id_angajat_val =-1;
 SELECT id_angajat INTO id_angajat_val FROM angajat a, concedii c WHERE  c.id_angajat= id_angajat and c.data_incepere<=data_actuala and c.data_finalizare>=data_actuala    LIMIT 1; 
       
       #Select * FROM angajat a, concedii c WHERE  c.id_angajat= id_angajat and c.data_incepere<=data_actuala and c.data_finalizare>=data_actuala    LIMIT 1;
       if (id_angajat_val=-1) then set result =1;
   else set result =0;
   end if;
END;
//
DELIMITER ;


drop procedure print_concedii_angajat;

DELIMITER //
CREATE PROCEDURE print_concedii_angajat(IN id_angajat int,out result int)
BEGIN
     DECLARE id_angajat_val INT ;
	set id_angajat_val =-1;
 SELECT id_angajat INTO id_angajat_val FROM angajat a, concedii c WHERE  c.id_angajat= id_angajat LIMIT 1; 
       
       #Select * FROM angajat a, concedii c WHERE  c.id_angajat= id_angajat and c.data_incepere<=data_actuala and c.data_finalizare>=data_actuala    LIMIT 1;
       if (id_angajat_val=-1) then set result =1;
   else 
   set result =0;
	Select * from concedii where id_angajat_val=concedii.id_angajat;
   end if;
END;
//
DELIMITER ;
