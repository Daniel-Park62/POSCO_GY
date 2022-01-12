
use gydb ;

DROP TRIGGER `moteinfo_before_insert`;

DELIMITER //
CREATE  TRIGGER `moteinfo_before_insert` BEFORE INSERT ON `moteinfo` FOR EACH ROW BEGIN

UPDATE motestatus SET errflag1 = 0, errcnt1 = 0
WHERE mmgb = NEW.mmgb AND seq = NEW.seq AND errflag1 = 1 AND prtd1 = 0 and NEW.rtd1 BETWEEN 2 AND 200  ;
UPDATE motestatus SET errflag2 = 0, errcnt2 = 0
WHERE mmgb = NEW.mmgb AND seq = NEW.seq AND errflag2 = 1 AND prtd2 = 0 and NEW.rtd2 BETWEEN 2 AND 200  ;
UPDATE motestatus SET errflag3 = 0, errcnt3 = 0
WHERE mmgb = NEW.mmgb AND seq = NEW.seq AND errflag3 = 1 AND prtd3 = 0 and NEW.rtd3 BETWEEN 2 AND 200  ;

UPDATE motestatus SET 
 errcnt1 = case when errflag1 = 0 AND ABS(prtd1 - NEW.rtd1) > 15 then errcnt1 + 1 ELSE 0 END,
 prtd1 = case when errflag1 = 1  then prtd1 ELSE NEW.rtd1 end,
 abdate1 = case when errflag1 = 0 AND ( NEW.rtd1 <= 0 OR truncate(NEW.rtd1 + 0.001 ,2) = 1.9 OR NEW.rtd1 > 200 OR  errcnt1 > 2 ) then NEW.tm ELSE abdate1 END,
 errflag1 = case when errflag1 = 0 AND ( NEW.rtd1 <= 0 OR truncate(NEW.rtd1 + 0.001,2) = 1.9 OR NEW.rtd1 > 200 OR  errcnt1 > 2 ) then 1 ELSE errflag1 END,
 errcnt2 = case when cntgb = 1 and errflag2 = 0 AND ABS(prtd2 - NEW.rtd2) > 15 then errcnt2 + 1 ELSE 0 END,
 prtd2 = case when errflag2 = 1 then prtd2 ELSE NEW.rtd2 end,
 abdate2 = case when cntgb = 1 and errflag2 = 0 AND ( NEW.rtd2 <= 0 OR truncate(NEW.rtd2 + 0.001,2) = 1.9 OR NEW.rtd2 > 200 OR errcnt2 > 2 ) then NEW.tm ELSE abdate2 END,
 errflag2 = case when cntgb = 1 and errflag2 = 0 AND ( NEW.rtd2 <= 0 OR truncate(NEW.rtd2 + 0.001,2) = 1.9 OR NEW.rtd2 > 200 OR errcnt2 > 2 ) then 1 ELSE errflag2 END,
 errcnt3 = case when cntgb = 1 AND (seq MOD 2) = 1 and errflag3 = 0 AND ABS(prtd3 - NEW.rtd3) > 15 then errcnt3 + 1 ELSE 0 END,
 prtd3 = case when errflag3 = 1 then prtd3 ELSE NEW.rtd3 end,
 abdate3 = case when cntgb = 1 AND (seq MOD 2) = 1 AND errflag3 = 0 AND ( NEW.rtd3 <= 0 OR truncate(NEW.rtd3 + 0.001,2) = 1.9 OR NEW.rtd3 > 200 OR errcnt3 > 2 ) then NEW.tm ELSE abdate3 END,
 errflag3 = case when cntgb = 1 AND (seq MOD 2) = 1 AND errflag3 = 0 AND ( NEW.rtd3 <= 0 OR truncate(NEW.rtd3 + 0.001,2) = 1.9 OR NEW.rtd3 > 200 OR errcnt3 > 2 ) then 1 ELSE errflag3 END,
 status = ( case when NEW.rtd1 >= temp_d then 2 when NEW.rtd1 >= temp_w then 1 ELSE 0 END )
WHERE mmgb = NEW.mmgb AND seq = NEW.seq and stand > 0 AND ( errflag1 = 0 OR errflag2 = 0 OR errflag3 = 0) ;

UPDATE motestatus SET errflag1 = 0, errcnt1 = 0, errflag2 = 0, errcnt2 = 0, errflag3 = 0, errcnt3 = 0
WHERE mmgb = NEW.mmgb AND seq = NEW.seq AND stand = 0 AND loc = 'B' ;


END; //

DELIMITER ;
