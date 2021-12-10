const mariadb = require('mariadb');
const config = require('./dbinfo').real;

const pool = mariadb.createPool({
      host: config.host,
      port: config.port,
      user: config.user,
      password: config.password,
      database: config.database,
      idleTimeout : 60,
      acquireTimeout : 180000,
      connectionLimit: 10
}) ;

module.exports = {
  getConn : () => { 
    return  mariadb.createConnection({
      host: config.host,
      port: config.port,
      user: config.user,
      password: config.password,
      database: config.database
    }) ;
  },
  getPool : () => { return pool ;}
}
