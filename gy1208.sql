USE gydb;
ALTER TABLE `motestatus`
	CHANGE COLUMN `status` `status` SMALLINT(6) UNSIGNED NOT NULL DEFAULT '0' COMMENT '0.정상 1.주의 2.경고온도 ' AFTER `act`,
	ADD COLUMN `errflag1` SMALLINT NULL DEFAULT '0' AFTER `xrang`,
	ADD COLUMN `errcnt1` SMALLINT NULL DEFAULT '0' AFTER `errflag1`,
	ADD COLUMN `errflag2` SMALLINT NULL DEFAULT '0' AFTER `errcnt1`,
	ADD COLUMN `errcnt2` SMALLINT NULL DEFAULT '0' AFTER `errflag2`,
	ADD COLUMN `errflag3` SMALLINT NULL DEFAULT '0' AFTER `errcnt2`,
	ADD COLUMN `errcnt3` SMALLINT NULL DEFAULT '0' AFTER `errflag3`,
	ADD COLUMN `prtd1` FLOAT(12) NULL DEFAULT '0' AFTER `errcnt3`,
	ADD COLUMN `prtd2` FLOAT(12) NULL DEFAULT '0' AFTER `prtd1`,
	ADD COLUMN `prtd3` FLOAT(12) NULL DEFAULT '0' AFTER `prtd2` ;

CREATE OR replace VIEW `vmotehist` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm, m.locnm,gubun,m.errflag1, m.errflag2, m.errflag3,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BUR' when 'I' then 'IMR' when 'W' then 'WR' ELSE m.loc END , m.tb) loc_name
FROM motehist t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;

CREATE OR replace VIEW `vmoteinfo` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm, m.locnm,m.gubun,m.errflag1, m.errflag2, m.errflag3,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BUR' when 'I' then 'IMR' when 'W' then 'WR' ELSE m.loc END , m.tb) loc_name
FROM moteinfo t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;

DELIMITER //
CREATE EVENT `evt_insert_hist` ON SCHEDULE EVERY 30 SECOND STARTS '2021-11-04 17:52:08' ON COMPLETION PRESERVE ENABLE DO BEGIN

INSERT INTO motehist (pkey, mmgb, seq, swseq, stand, batt, act, rtd1,rtd2,rtd3, temp, tm) 
        select pkey, mmgb, seq, swseq, stand, batt, act, rtd1,rtd2,rtd3, temp, tm 
          from moteinfo x where not exists (select 1 from motehist where pkey = x.pkey) ;

END//


CREATE TRIGGER `moteinfo_before_insert` BEFORE INSERT ON `moteinfo` FOR EACH ROW BEGIN

UPDATE motestatus SET 
 errflag1 = case when errflag1 = 0 AND ( NEW.rtd1 <= 0 OR NEW.rtd1 = 650 OR ( ABS(prtd1 - NEW.rtd1) > 15 and errcnt1 > 1)) then 1 ELSE errflag1 END,
 errcnt1 = case when errflag1 = 0 AND ABS(prtd1 - NEW.rtd1) > 15 then errcnt1 + 1 ELSE 0 END,
 prtd1 = case when errflag1 = 1 and NEW.rtd1 > 0 then -10 ELSE NEW.rtd1 end,
 errflag2 = case when cntgb = 1 and errflag2 = 0 AND ( NEW.rtd2 <= 0 OR NEW.rtd2 = 650 OR ( ABS(prtd2 - NEW.rtd2) > 15 and errcnt2 > 1)) then 1 ELSE errflag2 END,
 errcnt2 = case when cntgb = 1 and errflag2 = 0 AND ABS(prtd2 - NEW.rtd2) > 15 then errcnt2 + 1 ELSE 0 END,
 prtd2 = case when errflag2 = 1 and NEW.rtd2 > 0 then -10 ELSE NEW.rtd2 end,
 errflag3 = case when cntgb = 1 AND (seq MOD 2) = 1 AND errflag3 = 0 AND ( NEW.rtd3 <= 0 OR NEW.rtd3 = 650 OR ( ABS(prtd3 - NEW.rtd3) > 15 and errcnt3 > 1)) then 1 ELSE errflag3 END,
 errcnt3 = case when cntgb = 1 AND (seq MOD 2) = 1 and errflag3 = 0 AND ABS(prtd3 - NEW.rtd3) > 15 then errcnt3 + 1 ELSE 0 END,
 prtd3 = case when errflag3 = 1 and NEW.rtd3 > 0 then -10 ELSE NEW.rtd3 end,
 status = ( case when NEW.rtd1 >= temp_d then 2 when NEW.rtd1 >= temp_w then 1 ELSE 0 END )
WHERE mmgb = NEW.mmgb AND seq = NEW.seq AND ( errflag1 = 0 OR errflag2 = 0 OR errflag3 = 0) ;
 
END//

DELIMITER ;