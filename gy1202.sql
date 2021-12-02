use gydb ;

alter VIEW `vmotehist` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm, m.locnm,gubun,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BUR' when 'I' then 'IMR' when 'W' then 'WR' ELSE m.loc END , m.tb) loc_name
FROM motehist t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno )  ;

alter VIEW `vmoteinfo` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm, m.locnm,gubun,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BUR' when 'I' then 'IMR' when 'W' then 'WR' ELSE m.loc END , m.tb) loc_name
FROM moteinfo t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno )  ;

DELIMITER //
ALTER EVENT `evt_update_warnhist`
	ON SCHEDULE
		EVERY 10 SECOND STARTS '2021-11-17 13:05:13'
	ON COMPLETION PRESERVE
	ENABLE
	COMMENT ''
	DO BEGIN

	DECLARE vstand SMALLINT ;
	SET vstand = 1;
	UPDATE twarnhist SET warn_desc = '' ;
	loop1: LOOP
		update twarnhist w LEFT join
		(SELECT stand, CONCAT ( if(rtd1>temp_d,'경고','주의') ,'온도:', loc_name, DATE_FORMAT(tm, ' %Y-%m-%d %H:%i:%s') ) warn_DESC
		,tm  FROM vmoteinfo WHERE stand = vstand and tm = (select lastm from lastime LIMIT 1) AND rtd1 > temp_w order by rtd1 desc LIMIT 1) h
		ON (w.stand = h.stand)
		SET w.warn_desc = ifnull(h.warn_desc,'') , w.tm = ifnull(h.tm,NOW())
		WHERE w.gbn = 1 and w.stand = vstand ;

		update twarnhist w LEFT join
		(SELECT stand, CONCAT ( '통신이상:', loc_name, DATE_FORMAT(tm, ' %Y-%m-%d %H:%i:%s') ) warn_DESC
		,tm  FROM vmoteinfo WHERE stand = vstand and tm = (select lastm from lastime LIMIT 1) AND act = 2 && rtd1 = 0 order by pkey desc LIMIT 1) h
		ON (w.stand = h.stand)
		SET w.warn_desc = ifnull(h.warn_desc,'') , w.tm = ifnull(h.tm,NOW())
		WHERE w.gbn = 2 and w.stand = vstand ;

		set vstand 	= vstand + 1;
		if vstand < 6 then
			iterate loop1 ;
		END if ;
		leave loop1 ;
	END LOOP loop1 ;
 
END	//
DELIMITER ;