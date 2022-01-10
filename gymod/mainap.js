"use strict";
let port = process.env.MPORT || 9977;

const { fork } = require("child_process");
const path  = require('path');
const express = require('express');
const ModbusRTU = require("modbus-serial");
const moment = require('moment');
const app = express();
const cors = require('cors');
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cors());
app.use(express.static('chart'));

const servicePage = require('./service');
app.use('/Chart', servicePage);
const net = require('net');

app.get('/', (req, res) => {
  res.send('<h2>(주)다윈아이씨티 : Posco 광양 4PCM RM Roll Chock Monitoring </h2>\n');
//  console.info(req.query) ;
  if ( req.query.meas ) {
    child.send({"chgmeas": req.query.meas}) ;
    // console.info('time interval :', req.query.meas);
  }

 });

app.get('/reload', (req, res) => {
   res.send('<h2>Data Reload</h2>\n');
   child.send({"reload": 1}) ;
  console.info('getMeasure() call ');
});

app.get('/reset', (req, res) => {
  res.send('<h2>측정주기 reset</h2>\n');
  child.send({"resetmeas": 1}) ;
 console.info('측정주기 reset');
});

app.listen(port, function () {
  console.log('listening on port:' + port);
});

process.on('uncaughtException', function (err) {
  //예상치 못한 예외 처리
  console.error('uncaughtException 발생 : ' + err.stack);

});

console.log("** 4PCM RM Roll 데이터수집 start **") ;

const child = fork(path.join(__dirname, "datins.js"), [process.argv[2] || 502]);
child.on("close", function (code) {
  console.log("child process exited with code " + code);
});