"use strict" ;

const { appendFile } = require('fs');
const moment = require('moment');

appendFile(moment().format("./sYYYYMMDD") + '.dat', "jkasdfkjdfsjk", (err) => console.log ) ;

