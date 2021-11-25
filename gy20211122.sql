
use gydb ;

CREATE or replace VIEW `vmotehist` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BU' when 'I' then 'IM' ELSE m.loc END ,'R ', m.tb) loc_name
FROM motehist t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;

CREATE or replace VIEW `vmoteinfo` AS SELECT T.*, m.bno, m.cntgb, m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm,
   CONCAT(if(t.mmgb='1','W/S ','D/S '),t.stand,' ', case m.loc when 'B' then 'BU' when 'I' then 'IM' ELSE m.loc END ,'R ', m.tb) loc_name
FROM moteinfo t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;
