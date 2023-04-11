module.exports = {
  local: { // localhost
    host: 'localhost',
    port: 3306,
    user: 'pocusr',
    password: 'dawinit1',
    database: 'gydb'
  },
  real: { // real server db info
    host: process.env.DBIP  || 'localhost',
    port: process.env.DBPORT || 3306 ,
    user: 'pocusr',
    password: 'dawinit1',
    database: 'gydb'
  },
  dev: { // dev server db info
    host: '',
    port: 0,
    user: '',
    password: '',
    database: ''
  }
};
