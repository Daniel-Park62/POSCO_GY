use gydb ;
DELIMITER //

CREATE or replace PROCEDURE `sp_reset_invalid`(
	IN `_pkey` INT,
	IN `_px` INT
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT '비정상센서 reset'
BEGIN

UPDATE motestatus SET errflag1 = 0, errcnt1 = 0 WHERE pkey = _pkey AND _px = 1 ;
UPDATE motestatus SET errflag2 = 0, errcnt2 = 0 WHERE pkey = _pkey AND _px = 2 ;
UPDATE motestatus SET errflag3 = 0, errcnt3 = 0 WHERE pkey = _pkey AND _px = 3 ;

END //

DELIMITER ;