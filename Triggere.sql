DROP trigger Calcul_venit1;
DELIMITER //
CREATE  TRIGGER Calcul_venit1
BEFORE INSERT ON programari
FOR EACH ROW
BEGIN
     DECLARE pret_medic_val int default 0;
     DECLARE pret_total int;
     DECLARE venit_pana_acum int DEFAULT -1;
     DECLARE luna_val int default 0;
     DECLARE an_val int default 0;
     declare id_ang int default 1;
     declare salariu_ang int ;
     declare salariu_total int default 0;
     declare suma_val int default 0;
     
     select pret_medic into pret_medic_val from programari where id_programare = new.id_programare;
     if(pret_medic_val = 0) then select pret into pret_total from servicii where servicii.id = new.id_servicii;
     else set pret_total=pret_medic_val;
     end if;
	
    SET luna_val = MONTH(new.data_programarii);
    SET an_val = YEAR(new.data_programarii);
  
	Select incasari into venit_pana_acum from venit_lunar where luna_val=venit_lunar.luna and an_val = venit_lunar.an;
    
    
    if(venit_pana_acum = -1) then
    insert into venit_lunar(an,luna,incasari) values
    (an_val,luna_val,pret_total);
    else
	SET pret_total = pret_total*4/5 + venit_pana_acum;
    UPDATE venit_lunar
    SET incasari = pret_total
    where luna_val=venit_lunar.luna and an_val = venit_lunar.an;
    end if;
    
    simple_loop: LOOP
	     set salariu_ang = -1;
         select salariu into salariu_ang from angajat where id_angajat=id_ang;
         set id_ang=id_ang+1;
         set salariu_total=salariu_total+salariu_ang;
         if(salariu_ang = -1) THEN
			LEAVE simple_loop;
            END IF;
   END LOOP simple_loop;
    
    
	SET pret_total =salariu_total+1;
    UPDATE venit_lunar
    SET cheltuieli = pret_total,
		venit=incasari-pret_total
    where luna_val=venit_lunar.luna and an_val = venit_lunar.an;
    
    #salarii medici
    select suma into suma_val from salarii where luna_val=salarii.luna and an_val = salarii.an and new.id_medic=salarii.id_angajat;
    update salarii
    set suma= suma_val+pret_total*2/5 
    where  luna_val=salarii.luna and an_val = salarii.an and new.id_medic=salarii.id_angajat;
        
END;
//
DROP trigger Completare_salarii;
DELIMITER //
CREATE  TRIGGER Completare_salarii
BEFORE INSERT ON venit_lunar
FOR EACH ROW
BEGIN
     declare salariu_ang int ;
     declare id_ang int default 1;
    #DEcLARE functie_val enum('inspector resurse umane','expert financiar contabil','receptioner','medic','asistent medical');
    
    simple_loop: LOOP
	     set salariu_ang = -1;
         select salariu into salariu_ang from angajat where id_angajat=id_ang;
         if(salariu_ang = -1) THEN
			LEAVE simple_loop;
            END IF;
		insert into salarii(id_angajat,suma, luna, an) values (id_ang,salariu_ang, new.luna, new.an); 
        set id_ang=id_ang+1;
         
   END LOOP simple_loop;
    
        
END;
//
drop trigger if exists valabilitate_concediu

DELIMITER //
CREATE  TRIGGER valabilitate_concediu
before insert ON concedii
FOR EACH ROW
BEGIN
DECLARE zile_ramase_val int;

select zile_ramase_concediu into zile_ramase_val from angajat where angajat.id_angajat=new.id_angajat;
 if zile_ramase_val-new.nr_zile>=0 then 
 UPDATE angajat
 SET zile_ramase_concediu=zile_ramase_val-new.nr_zile
 where angajat.id_angajat=new.id_angajat;
 else
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Nu sunt suficiente zile de concediu disponibile!';
 end if;

END;
//
DELIMITER //
CREATE  TRIGGER gasire_departament
BEFORE INSERT ON angajat
FOR EACH ROW
BEGIN

    if(new.functia = 'inspector resurse umane') then set new.departament='uman';
    end if;
     if(new.functia = 'receptioner') then set new.departament='uman';
    end if;
    
     if(new.functia = 'expert financiar contabil') then set new.departament='financiar-contabil';
    end if;
    
	if (new.functia = 'medic') then set new.departament='medical';
    end if;
		
	if(new.functia = 'asistent medical') then set new.departament='medical';
    end if;

        
END;
//

DROP trigger Calcul_durata_totala;
DELIMITER //
CREATE  TRIGGER Calcul_durata_totala
BEFORE INSERT ON programari
FOR EACH ROW
BEGIN
   
	declare id_pacient_aux int;
	declare id_raport int;
    declare cnp_pacient varchar(14);
    
    select id_pacient into id_pacient_aux from pacient where cnp_pacient=pacient.cnp;
	select id into id_raport from raport_medical where id_pacient_aux=id_pacient and data_consult=new.data_programarii;
    
    update raport_medical
    SET durata_consultatie = sec_to_time(TIME_TO_SEC(durata_consultatie)+tIME_TO_SEC(new.durata_consultatie))
    where id=id_raport;
END;
//
