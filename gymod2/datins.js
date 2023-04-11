"use strict";

const moment = require('moment');
const { appendFileSync } = require('fs');
const { connect } =  require('net');
const ModbusRTU = require("modbus-serial");

const WS_NUM = process.env.SCOUNT || 3;

const DEVPORT = process.env.DEVPORT || 1503;
const TAGPORT = process.env.TAGPORT || 1502 ;
const GWIP = process.env.GWIP || "192.168.0.223";
const MEAS = process.env.TimeInterval || 5;
const DIRNM = process.env.DIRNM || '.';

console.info("Gateway IP  (GWIP) :", GWIP);
console.info("Tag Port (TAGPORT) :", TAGPORT);
console.info("Dev Port (DEVPORT) :", DEVPORT);
console.info("Sensor num(SCOUNT) :", WS_NUM);
console.info("File name (DIRNM) :", DIRNM);
console.info("TimeInterval      :", MEAS);

let svtime = moment().subtract(34, "s");

let ws_buffarr = new Array() ;

const delay = (ms) => { return new Promise(resolve => setTimeout(resolve, ms)) };

let ws_ss = [];

process.on('SIGBREAK', async () => {
  console.info("Sensor reset") ;
  ws_ss.forEach(ss => { ss.mac && ws_sensor_push(ss.mac,  1) }  ) ;
});

/*
con.query( ' delete from motehist where tm < DATE_ADD( now() , interval -12 month)',
        (err,res) => { if(err) console.log(err);  } ) ;
*/
function getDevs() {
  const cli_dev = new ModbusRTU();
  cli_dev.connectTCP(GWIP, { port: DEVPORT })
    .then(async () => {
      let vincr = (WS_NUM * 6 > 100) ? 100 : WS_NUM * 6;
      let rapdev = [];
      cli_dev.setID(1);
      for (let ii = 1; ii < WS_NUM * 6; ii += vincr) {
        await cli_dev.readInputRegisters(ii, vincr)
          .then((d) => { rapdev = rapdev.concat(d.data); })
          .catch((e) => {
            console.error("apdev register read error");
            console.info(e);
          });
      }
      cli_dev.close();
      // console.log(rapdev);
      for (let i = 0; i < WS_NUM * 6; i += 6) {
        if (rapdev[i + 4] > 2 || rapdev[i + 4] < 0 ) continue ;

        let d = (Math.floor(i / 6) + 1);
        let vmac = (rapdev[i] & 255).toString(16).padStart(2, '0') + (rapdev[i] >>> 8).toString(16).padStart(2, '0') 
          + (rapdev[i + 1] & 255).toString(16).padStart(2, '0') + (rapdev[i + 1] >>> 8).toString(16).padStart(2, '0') 
          + (rapdev[i + 2] & 255).toString(16).padStart(2, '0') + (rapdev[i + 2] >>> 8).toString(16).padStart(2, '0') 
          + (rapdev[i + 3] & 255).toString(16).padStart(2, '0') + (rapdev[i + 3] >>> 8).toString(16).padStart(2, '0');
        let vbatt = rapdev[i + 5];
        const ss = ws_ss.find( (it) =>  { 
          if (it.mac == vmac) return true; 
        }) ;
        if (ss == undefined ) {
          ws_ss.push({"mac":vmac, "act": rapdev[i + 4]}) ;
          console.log(ws_ss);
          ws_sensor_push(vmac, rapdev[i + 4] ) ;
        } else if (ss.act != rapdev[i + 4] ) {
          ss.act = rapdev[i + 4] ;
          ws_sensor_push(vmac, ss.act ) ;
        }
      }
    })
    .catch((e) => {
      console.error("getDevs() connect error");
      console.info(e);
    });


}

function insTemp() {

  const client = new ModbusRTU();
  
  // client.close();
  if (sv_nextt.isSame(nextt))   return ;
  const today = nextt;  //moment();
  sv_nextt = nextt ;
  // console.log("insTemp : ", nextt.format("HH:mm:ss")) ;
  const tm = today.format('YYYY-MM-DD HH:mm:ss');
  const filenm = DIRNM + today.format('/YYYYMMDD') + '.dat' ;

  client.connectTCP(GWIP, { port: TAGPORT })
    .then(async () => {
      client.setID(1);
      let rtags = new Int16Array(WS_NUM * 2);
      let motearr = new Array();
      await client.readInputRegisters(1, WS_NUM * 2)
        .then(async (d) => {
          
          rtags = new Int16Array(d.data);
          let vrtd = 0, sq = 0, vtemp = 0;
          let ny = `"${tm}"` ;
          for(let i = 0; i+1 < rtags.length ; i+=2 ){
              vrtd = rtags[i] / 100;
              vtemp = rtags[i+1] / 100;
              sq = Math.floor(i/2) + 1 ;
              ny += `\t${vrtd}` ;
          }
          appendFileSync(filenm, ny + "\n", (err) => console.log ) ;
        })
        .catch((e) => {
          console.error(" ** register read error **");
          console.info(e);
        });
    })
    .catch((e) => {
      console.error("insTemp()  connect error 발생");
      console.info(e);
    }) 
    .finally( () => client.close() );
}

async function main()  {

  setInterval(getDevs, 1000);

  setTimeout(main_loop, 2000);
 
  setInterval(() => {
    let buff = ws_buffarr.shift() ;
    if (buff != undefined) setTimeout( ws_sensor_set,0,buff) ;
  }, 3000) ;
  
  return "";
}

let csec = moment().get('second');
let nextt = moment(moment().set({ 'second': Math.ceil(csec / MEAS) * MEAS, 'millisecond': 0 }));
let sv_nextt = moment() ;
main() ;

function main_loop() {
  // console.info(nextt) ;
  // let tm1 = moment();
  // await getDevs();
  setImmediate(insTemp) ;
  // await sleep( (MEAS -2) * 1000  );
  
  csec = moment().get('second');
  nextt = moment(moment().set({ 'second': Math.ceil(csec / MEAS) * MEAS, 'millisecond': 0 }));
  
  // console.log(nextt.format("HH:mm:ss")) ;
  setTimeout(main_loop, nextt - moment());
}

process.on('uncaughtException', function (err) {
  //예상치 못한 예외 처리
  console.error('uncaughtException 발생 : ' + err.stack);

});

function ws_sensor_push(mac, act ) {
	//    if (act == -1) return ;
		if (mac.indexOf("00000000") != -1) return;
    let idx = ws_buffarr.findIndex( item => item.toString('hex').substr(0,16) == mac ) ;
    if (idx > -1) ws_buffarr.splice(idx,1) ;
    let buf1 = Buffer.alloc(14);
    buf1.writeInt32LE(act != 0 ? MEAS : 9999,10);
    buf1.write(mac,'hex');
    buf1[8] = 1;
    buf1[9] = 4;
    ws_buffarr.push(buf1) ;
	// console.info(" ws push :" , buf1.toString('hex') );
}

function ws_sensor_set( buf1 ) {

  let socket = connect( {port : 40000, host: GWIP}, () => {
//      console.info(buf1.toString('hex'));
    socket.setNoDelay(false);
    try {
      let ret = socket.write(buf1) ;
    } catch (e) {
      console.log("ws socket write error :", e);
      ws_buffarr.unshift(buf1) ;
      socket.end();
    }
  } );
  socket.on('data', function (data) {
      console.log(" *** %s ws return data : %s" , moment().format('MM-DD HH:mm:ss') , data.slice(0,8).toString('hex'),data[8] );
      socket.end();
      // if ( data[8] != 0 )   ws_sensor_set( buf1 ) ;
  });
  socket.on('error', function (err) {
      // console.error(" *** ws Error : ", Date() ,buf1.toString('hex') , JSON.stringify(err));
      // ws_buffarr.unshift(buf1) ;
      setTimeout( ws_sensor_set,500,buf1) ;
  });
  // writeData(socket, buf1) ;
}
