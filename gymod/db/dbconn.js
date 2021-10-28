const mariadb = require('mariadb');
const config = require('./dbinfo').real;

const pool = mariadb.createPool({
      host: config.host,
      port: config.port,
      user: config.user,
      password: config.password,
      database: config.database,
      acquireTimeout : 60000,
      connectionLimit: 15
}) ;

module.exports = {
  getConn : () => { 
    return pool.getConnection() ;
  },
  getPool : () => { return pool ;}
}
