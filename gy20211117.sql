
use gydb ;

CREATE TABLE IF NOT EXISTS `twarnhist` (
  `gbn` smallint(6) NOT NULL DEFAULT 1 COMMENT '1.온도이상 2.통신이상',
  `stand` smallint(6) NOT NULL DEFAULT 0,
  `warn_desc` varchar(100) DEFAULT NULL,
  `tm` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`gbn`,`stand`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `twarnhist` (`gbn`, `stand`, `warn_desc`, `tm`) VALUES
	(1, 1, '', now()),
	(1, 2, '', now()),
	(1, 3, '', now()),
	(1, 4, '', now()),
	(1, 5, '', now()),
	(2, 1, '', now()),
	(2, 2, '', now()),
	(2, 3, '', now()),
	(2, 4, '', now()),
	(2, 5, '', now()) ;

DELIMITER //
CREATE EVENT `evt_update_warnhist` ON SCHEDULE EVERY 15 SECOND STARTS '2021-11-17 13:05:13' ON COMPLETION PRESERVE ENABLE DO BEGIN

	DECLARE vstand SMALLINT ;
	SET vstand = 1;
	loop1: LOOP
		update twarnhist w,
		(SELECT stand, CONCAT ( if(rtd1>temp_d,'위험','경고') ,'온도:', loc_name, DATE_FORMAT(tm, ' %Y-%m-%d %H:%i:%s') ) warn_DESC
		,tm  FROM vmoteinfo WHERE stand = vstand AND rtd1 > temp_w order by pkey desc LIMIT 1) h
		SET w.warn_desc = h.warn_desc , w.tm = h.tm
		WHERE w.gbn = 1 and w.stand = h.stand ;

		update twarnhist w,
		(SELECT stand, CONCAT ( '통신이상:', loc_name, DATE_FORMAT(tm, ' %Y-%m-%d %H:%i:%s') ) warn_DESC
		,tm  FROM vmoteinfo WHERE stand = vstand AND act = 2 && rtd1 = 0 order by pkey desc LIMIT 1) h
		SET w.warn_desc = h.warn_desc , w.tm = h.tm
		WHERE w.gbn = 2 and w.stand = h.stand ;

		set vstand 	= vstand + 1;
		if vstand < 6 then
			iterate loop1 ;
		END if ;
		leave loop1 ;
	END LOOP loop1 ;

 
END//
DELIMITER ;
