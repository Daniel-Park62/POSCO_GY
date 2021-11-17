/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
use gydb ;

CREATE TABLE IF NOT EXISTS `tchock` (
  `mmgb` char(1) NOT NULL DEFAULT '1',
  `chockno` smallint(5) unsigned NOT NULL DEFAULT 0,
  `chocknm` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`mmgb`,`chockno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='CHOCK NO 명';

/*!40000 ALTER TABLE `tchock` DISABLE KEYS */;
INSERT INTO `tchock` (`mmgb`, `chockno`, `chocknm`) VALUES
	('1', 21, 'CBTW1'),
	('1', 22, 'CBBW1'),
	('1', 23, 'CBTW2'),
	('1', 24, 'CBBW2'),
	('1', 25, 'CBTW3'),
	('1', 26, 'CBBW3'),
	('1', 27, 'CBTW4'),
	('1', 28, 'CBBW4'),
	('1', 29, 'CBTW5'),
	('1', 30, 'CBBW5'),
	('1', 31, 'CBTW6'),
	('1', 32, 'CBBW6'),
	('1', 33, 'CBTW7'),
	('1', 34, 'CBBW7'),
	('1', 35, 'CBTW8'),
	('1', 36, 'CBBW8'),
	('1', 37, 'CBTW9'),
	('1', 38, 'CBBW9'),
	('1', 39, 'CBTW10'),
	('1', 40, 'CBBW10'),
	('1', 41, 'CBTW11'),
	('1', 42, 'CBBW11'),
	('2', 63, 'CBTD1'),
	('2', 64, 'CBBD1'),
	('2', 65, 'CBTD2'),
	('2', 66, 'CBBD2'),
	('2', 67, 'CBTD3'),
	('2', 68, 'CBBD3'),
	('2', 69, 'CBTD4'),
	('2', 70, 'CBBD4'),
	('2', 71, 'CBTD5'),
	('2', 72, 'CBBD5'),
	('2', 73, 'CBTD6'),
	('2', 74, 'CBBD6'),
	('2', 75, 'CBTD7'),
	('2', 76, 'CBBD7'),
	('2', 77, 'CBTD8'),
	('2', 78, 'CBBD8'),
	('2', 79, 'CBTD9'),
	('2', 80, 'CBBD9'),
	('2', 81, 'CBTD10'),
	('2', 82, 'CBBD10'),
	('2', 83, 'CBTD11'),
	('2', 84, 'CBBD11');
/*!40000 ALTER TABLE `tchock` ENABLE KEYS */;

CREATE or replace VIEW `vmotehist` AS SELECT T.*,m.bno,m.cntgb,m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm,
   CONCAT(if(t.mmgb='1','W/S ','D/S '), case m.loc when 'B' then 'BU' when 'I' then 'IM' ELSE m.loc END ,'R ', m.tb) loc_name
FROM motehist t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;

CREATE or replace VIEW `vmoteinfo` AS SELECT T.*, m.bno, m.cntgb, m.loc, m.tb, m.temp_w, m.temp_d , m.descript, c.chocknm,
   CONCAT(if(t.mmgb='1','W/S ','D/S '), case m.loc when 'B' then 'BU' when 'I' then 'IM' ELSE m.loc END ,'R ', m.tb) loc_name
FROM moteinfo t JOIN motestatus m ON(t.mmgb = m.mmgb AND t.seq = m.seq ) 
 LEFT JOIN tchock c ON (t.mmgb = c.mmgb AND m.bno = c.chockno ) ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
