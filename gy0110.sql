use gydb;
DELIMITER //
alter EVENT `evt_delete_hist` ON SCHEDULE EVERY 1 WEEK STARTS '2021-12-05 12:00:00' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
delete from motehist where tm < DATE_SUB(NOW(),INTERVAL 6 MONTH);
END//
DELIMITER ;

