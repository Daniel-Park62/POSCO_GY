"use strict";
let WS_NUM = 3;
let DS_NUM = 3;
let SSNUM = 3;
const TAGPORT = process.argv[2] ;
const DEVPORT = 1503;

let GWIP_WS = process.env.GWIP_WS || "192.168.0.223";
let GWIP_DS = process.env.GWIP_DS || "192.168.0.223";

console.info("(GWIP_WS)", GWIP_WS, "(GWIP_DS)", GWIP_DS);
console.info("Tag Port", TAGPORT);

const moment = require('moment');

const gydb = require('./db/dbconn');

const con = gydb.getPool() ;

let MEAS = 10;
let UPDAUTO = true;

let svtime = moment().subtract(34, "s");
const net = require('net');

let ws_buffarr = new Array() ;
let ds_buffarr = new Array() ;

const ModbusRTU = require("modbus-serial");

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

let ws_ss = [];
let ds_ss = [];

let BATTL = 3500;
let mainto ;
process.on("message", async (dat) => {
  if (dat.reload) {
    setTimeout(getMeasure, 0) ;
    return ;
  }
  if (dat.resetmeas) {
    ws_ss.forEach(ss => { ss[8] == 'B' && ss.Mac && ws_sensor_push(ss.Mac,  1) }  ) ;
    ds_ss.forEach(ss => { ss[8] == 'B' && ss.Mac && ds_sensor_push(ss.Mac,  1) }  ) ;
    return ;
  }

  dat.chgmeas *= 1;
  if ( typeof dat.chgmeas == 'number' ) {
    con.query("SELECT measure, batt, updauto FROM MOTECONFIG LIMIT 1")
    .then(row => {
      MEAS = row[0].measure;
      BATTL = row[0].batt;
      UPDAUTO = row[0].updauto == 'Y';
    })
    .catch(err => console.error(err) )
    .finally(() => { 
      console.info('time interval :' , MEAS, " Battery Limit :" , BATTL, UPDAUTO );
      console.log("Interval 변경:",MEAS) ;
      clearTimeout(mainto) ;
      mainto = setTimeout(main_loop, 2000) ;
    });

    ws_ss.forEach(ss => { ss[8] == 'B' && ss.Mac && ws_sensor_push(ss.Mac,  ss[2]) }  );
    ds_ss.forEach(ss => { ss[8] == 'B' && ss.Mac && ds_sensor_push(ss.Mac,  ss[2]) }  );
  }
  
});

function getMeasure() {

   con.query("SELECT seq,act,bno, stand, batt , swseq, spare, gubun, cntgb, loc \
                FROM motestatus  where mmgb = '1' ")
    .then(rows => { 
      rows.forEach((e, i) => { ws_ss[i] = []; 
        ws_ss[i] = [e.act, e.bno, e.stand, e.batt, e.spare, e.gubun, e.seq, e.swseq, e.loc ] ;
        });
    })
    .catch(err => console.error(err));

   con.query("SELECT seq,act,bno,stand, batt , swseq, spare, gubun, loc \
                FROM motestatus  where mmgb = '2' ")
    .then(rows => {
      rows.forEach((e, i) => { ds_ss[i] = []; 
        ds_ss[i] = [e.act, e.bno, e.stand, e.batt, e.spare, e.gubun, e.seq, e.swseq, e.loc] 
        });
    })
    .catch(err => console.error(err));

   con.query("SELECT measure, batt, updauto FROM MOTECONFIG LIMIT 1")
    .then(row => {
      MEAS = row[0].measure;
      BATTL = row[0].batt;
      UPDAUTO = row[0].updauto == 'Y';
    })
    .catch(err => MEAS = 10)
    .finally(() => console.info('time interval :' , MEAS, " Battery Limit :" , BATTL, UPDAUTO ));
  ;
   con.query("SELECT max(seq)  as ssnum FROM motestatus where spare = 'N' and GUBUN = 'S' ")
    .then(rows => SSNUM = rows[0].ssnum)
    .catch(err => SSNUM = 10)
    .finally(() => console.info('Sensor num :' + SSNUM));

   con.query("SELECT sum(IF(MMGB='1',1,0)) as ws_num, sum(IF(MMGB='2',1,0)) as ds_num FROM motestatus where spare = 'N' ")
    .then(row => { WS_NUM = row[0].ws_num ;  DS_NUM = row[0].ds_num ; 
            console.info('W/S num :' , WS_NUM, ' D/S num :', DS_NUM)})
    .catch(err => WS_NUM = 0, DS_NUM = 0 ) ;

   con.query("SELECT lastm FROM lastime LIMIT 1 ")
    .then(row => {
      svtime = moment(row[0].lastm);
      console.info('last time :' + svtime.format('YYYY-MM-DD HH:mm:ss'));
    })
    .catch(err => { svtime = moment() ; console.log(err)});

}

/*
con.query( ' delete from motehist where tm < DATE_ADD( now() , interval -12 month)',
        (err,res) => { if(err) console.log(err);  } ) ;
*/
function getDevs(con) {
  const cli_dev = new ModbusRTU();
  cli_dev.connectTCP(GWIP_WS, { port: DEVPORT })
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
        //        if ( rapdev[i] == 0) continue ;
        let d = (Math.floor(i / 6) + 1);
        let vmac = (rapdev[i] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 1] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 1] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 2] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 2] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 3] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 3] >>> 8).toString(16).padStart(2, '0');
        let vbatt = rapdev[i + 5];
        if (rapdev[i + 4] > 2 || rapdev[i + 4] < 0 ) continue ;

        let mote = { "seq": d, "mac": vmac, "act": rapdev[i + 4], "batt": vbatt };
        // console.log(mote);
        ws_ss.every((ss,i) => {
          if (ss[6] == d) {
            if ( ss[5] == 'S' && ss[8] == 'B' && ss.Mac == undefined  ) {
              ss.Mac = mote.mac.replace(/:/gi,'');
              ws_sensor_push(ss.Mac, ss[8] == 'B' ? ss[2] : 2 ) ;
            }
            if ( ss[0] == mote.act && ss[3] == vbatt)  return false ;
            ws_ss[i][0] = mote.act;
            // console.log(mote, ws_ss[i][0], ss[0]) ;
            if (mote.act > 0) {
              ws_ss[i][3] = vbatt;
              con.query("UPDATE motestatus SET MAC = ?, ACT = ? , BATT = ?  where mmgb = '1' and seq = ? ", [mote.mac, mote.act, mote.batt, d])
                .catch(err => console.error("Update motestatus :", err)) ;
            } else {
                con.query("UPDATE motestatus  SET MAC = ?,ACT = 0  where mmgb = '1' and seq = ?  ", [mote.mac, d])
                  .catch(err => console.error("Update motestatus :", err));
              }
            return false ;
          } else 
            return true ;
        });

      }
    })
    .catch((e) => {
      console.error("getDevs() connect error");
      console.info(e);
    });

    // DS READ
    const cli_dev2 = new ModbusRTU();
    if (DS_NUM > 0 )
    cli_dev2.connectTCP(GWIP_DS, { port: DEVPORT })
    .then(async () => {
      let vincr = (DS_NUM * 6 > 100) ? 100 : DS_NUM * 6;
      let rapdev = [];
      await cli_dev2.setID(1);
      for (let ii = 1; ii < DS_NUM * 6; ii += vincr) {
        await cli_dev2.readInputRegisters(ii, vincr)
          .then((d) => { rapdev = rapdev.concat(d.data); })
          .catch((e) => {
            console.error("apdev register read error");
            console.info(e);
          });
      }
      cli_dev2.close();      
      //      let rapdev = new Uint16Array(rdev);
      for (let i = 0; i < DS_NUM * 6; i += 6) {
        //        if ( rapdev[i] == 0) continue ;
        let d = (Math.floor(i / 6) + 1);
        let vmac = (rapdev[i] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 1] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 1] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 2] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 2] >>> 8).toString(16).padStart(2, '0') + ':'
          + (rapdev[i + 3] & 255).toString(16).padStart(2, '0') + ':' + (rapdev[i + 3] >>> 8).toString(16).padStart(2, '0');
        let vbatt = rapdev[i + 5];
        if (rapdev[i + 4] > 2 || rapdev[i + 4] < 0 ) continue ;

        let mote = { "seq": d, "mac": vmac, "act": rapdev[i + 4], "batt": vbatt };
        // console.log(mote);
        ds_ss.every((ss,i) => {
          if (ss[6] == d) {
            if ( ss[5] == 'S' && ss[8] == 'B'  && ss.Mac == undefined ) {
              ss.Mac = mote.mac.replace(/:/gi,'');
              ds_sensor_push(ss.Mac, ss[8] == 'B' ? ss[2] : 2) ;
            }
            if ( ss[0] == mote.act && ss[3] == vbatt)  return false ;
            ds_ss[i][0] = mote.act;

            if (mote.act > 0) {
              ds_ss[i][3] = vbatt;
                con.query("UPDATE motestatus SET MAC = ?, ACT = ? , BATT = ?  where mmgb = '2' and seq = ? ", [mote.mac, mote.act, mote.batt, d])
                  .catch(err => console.error("Update motestatus :", err)) ;
            } else {
                con.query("UPDATE motestatus  SET MAC = ?,ACT = 0  where mmgb = '2' and seq = ?  ", [mote.mac, d])
                  .catch(err => console.error("Update motestatus :", err));
              }
            return false ;
          } else
            return true ;
        });

      }
    })
    .catch((e) => {
      console.error("getDevs() connect error");
      console.info(e);
    }) ;

}

function insTemp(con) {

  const client = new ModbusRTU();
  
  // client.close();
  if (sv_nextt.isSame(nextt))   return ;
  const today = nextt;  //moment();
  sv_nextt = nextt ;
  // console.log("insTemp : ", nextt.format("HH:mm:ss")) ;
  const tm = today.format('YYYY-MM-DD HH:mm:ss');

  client.connectTCP(GWIP_WS, { port: TAGPORT })
    .then(async () => {
      client.setID(1);
      let rtags = new Int16Array(124);
      let motearr = new Array();
      await client.readInputRegisters(1, 124)
        .then(async (d) => {
          
          rtags = new Int16Array(d.data);
          let vrtd1 = 0, vrtd2 = 0, vrtd3 = 0, vtemp = 0;
          for(let i = 0; i < ws_ss.length ; i++){
            if (ws_ss[i][4] == 'Y'  || ws_ss[i][5] != 'S' ) continue ;

            await client.readHoldingRegisters((ws_ss[i][6]-1)*8+1,8)
            .then( d => {
              let mdata = new Uint16Array(d.data) ;
              if (ws_ss[i][8] == 'B' && mdata[3] != ws_ss[i][2] ) {
                ws_ss[i][2] = mdata[3] ;
                if (ws_ss[i].Mac) ws_sensor_push(ws_ss[i].Mac, ws_ss[i][2]) ;
                client.writeRegister((ws_ss[i][6]-1)*8+3, mdata[3] == 0 ? 9999 : MEAS ) 
                .catch( e => console.error(e));
                let qstr = "UPDATE motestatus set stand = ?" + (ws_ss[i][2] == 0 ? ",errflag1 = 0, errcnt1 = 0 ":"") + " where mmgb = '1' and seq = ?";
                if (UPDAUTO) con.query(qstr, [ws_ss[i][2], ws_ss[i][6]]);
              }
              // if (mdata[6] == 1 && ws_ss[i][7]  != mdata[7] || mdata[6] == 0 && ws_ss[i][7] > 0 ) {
              //   ws_ss[i][7] = mdata[6] == 0 ? 0 : mdata[7] ;
              //   con.query("UPDATE motestatus set swseq = ?  where mmgb = '1' and seq = ?", [ws_ss[i][7], ws_ss[i][6]]) ;
              // }
              
            });   
            let ws_err = {} ;        
            await con.query("SELECT errflag1, errflag2, errflag3, prtd1, prtd2, prtd3  FROM motestatus  where mmgb = '1' and seq = ?", [ws_ss[i][6]]) 
                .then(rows => { ws_err = rows[0] } ) 
                .catch(err => console.error(err));

            let ix = ws_ss[i][7] > 0 ? ws_ss[i][7] : ws_ss[i][6] ;
            if (ix < 43)  {
              let a = ws_ss[i][6] * 2 - 1 ;
              vrtd1 = rtags[a -1] / 100;
              vtemp = rtags[a] / 100;
            } else if (ix < 53) {
              let a = Math.abs(43 - ix) * 4 + 84 ;
              vrtd1 = rtags[a] / 100;
              vrtd2 = rtags[a+1] / 100;
              vrtd3 = rtags[a+2] / 100;
              vtemp = rtags[a+3] / 100;
            } else continue ;
      /*
            if ( isNaN(vrtd1) || vrtd1 == 0.09) vrtd1 = 0 ;
            if ( isNaN(vrtd2) || vrtd2 == 0.09) vrtd2 = 0 ;
            if ( isNaN(vrtd3) || vrtd3 == 0.09) vrtd3 = 0 ;
            if ( isNaN(vtemp) || vtemp == 0.09) vtemp = 0 ;
             if ( vrtd1 < 0) vrtd1 = -52 ;
            if ( vrtd2 < 0) vrtd2 = -52 ;
            if ( vrtd3 < 0) vrtd3 = -52 ;
            if ( vtemp < 0) vtemp = -52 ;
            if ( vrtd1 > 200) vrtd1 = 650 ;
            if ( vrtd2 > 200) vrtd2 = 650 ;
            if ( vrtd3 > 200) vrtd3 = 650 ;
            if ( vtemp > 200) vtemp = 650 ;
            if ( ws_err.errflag1 == 1 ) vrtd1 = ws_err.prtd1 ;
            if ( ws_err.errflag2 == 1 ) vrtd2 = ws_err.prtd2 ;
            if ( ws_err.errflag3 == 1 ) vrtd3 = ws_err.prtd3 ;
 */            
            motearr.push(['1', ws_ss[i][6],  ws_ss[i][2] ,ws_ss[i][0],ws_ss[i][3], vrtd1,vrtd2,vrtd3, vtemp, tm] ) ;
            // con.query('INSERT INTO moteinfo ( mmgb, seq, stand, act, batt, rtd1,rtd2,rtd3,temp, tm ) value (?,?,?,?,?,?,?,?,?,?) ',
            //   ['1', ws_ss[i][6],  ws_ss[i][2] ,ws_ss[i][0],ws_ss[i][3], vrtd1,vrtd2,vrtd3, vtemp, tm])
            //   .catch(err => console.error(err, ws_ss[i]));
          }

        })
        .catch((e) => {
          console.error(" ** register read error **");
          console.info(e);
        });
        con.batch('INSERT INTO moteinfo ( mmgb, seq, stand, act, batt, rtd1,rtd2,rtd3,temp, tm ) values (?,?,?,?,?,?,?,?,?,?) ',
         motearr  )
        .catch(err => console.error(err, motearr));

        con.query('UPDATE lastime SET lastm = ? ', [tm])
          .catch(err => console.log("update lastime :" + err));

    })
    .catch((e) => {
      console.error("insTemp()  W/S error 발생");
      console.info(e);
    }) 
    .finally( () => client.close() );
    
    // DS READ
    const client2 = new ModbusRTU();
    if (DS_NUM > 0)
    client2.connectTCP(GWIP_DS, { port: TAGPORT })
    .then(async () => {
      client2.setID(1);
      let rtags = new Int16Array(84);
      let motearr = new Array();
      await client2.readInputRegisters(1, 84)
        .then(async (d) => {
          rtags = new Int16Array(d.data);
          let vrtd1 = 0, vrtd2 = 0, vrtd3 = 0, vtemp = 0 ;
          for(let i = 0; i < ds_ss.length ; i++){
            if (ds_ss[i][4] == 'Y'  || ds_ss[i][5] != 'S' ) continue ;
            await client2.readHoldingRegisters((ds_ss[i][6]-1)*8+1,8)
            .then( d => {
              let mdata = new Uint16Array(d.data) ;

              if (ds_ss[i][8] == 'B' && mdata[3] != ds_ss[i][2] ) {
                ds_ss[i][2] = mdata[3] ;
                ds_sensor_push(ds_ss[i].Mac, ds_ss[i][2]) ;
                client.writeRegister((ds_ss[i][6]-1)*8+3, mdata[3] == 0 ? 9999 : MEAS ) 
                .catch( e => console.error(e));
                let qstr = "UPDATE motestatus set stand = ?" + (ds_ss[i][2] == 0 ? ",errflag1 = 0, errcnt1 = 0 ":"") + " where mmgb = '2' and seq = ?";
                if (UPDAUTO) con.query(qstr, [ds_ss[i][2], ds_ss[i][6]]) ;
              }
              // if (mdata[6] == 1 && ds_ss[i][7]  != mdata[7] || mdata[6] == 0 && ds_ss[i][7] > 0 ) {
              //   ds_ss[i][7] = mdata[6] == 0 ? 0 : mdata[7] ;
              //   con.query("UPDATE motestatus set swseq = ?  where mmgb = '2' and seq = ?", [ds_ss[i][7], ds_ss[i][6]]) ;
              // }
            });       
            
            let ds_err = {} ;         
            await con.query("SELECT errflag1,  prtd1  FROM motestatus  where mmgb = '2' and seq = ?", [ws_ss[i][6]]) 
                .then(rows => ds_err = rows[0] ) 
                .catch(err => console.error(err));
            
            let ix = ds_ss[i][7] > 0 ? ds_ss[i][7] : ds_ss[i][6] ;
            if (ix < 43)  {
              let a = ix * 2 - 1 ;
              vrtd1 = rtags[a -1] / 100;
              vtemp = rtags[a] / 100;
          /*
              if ( isNaN(vrtd1) || vrtd1 == 0.09) vrtd1 = 0 ;
              if ( isNaN(vtemp) || vtemp == 0.09) vtemp = 0 ;
              
               if ( vrtd1 < 0) vrtd1 = -52 ;
              if ( vtemp < 0) vtemp = -52 ;
              if ( vrtd1 > 200) vrtd1 = 650 ;
              if ( vtemp > 200) vtemp = 650 ;
              if ( ds_err.errflag1 == 1 ) vrtd1 = ds_err.prtd1 ;
 */

              motearr.push( ['2', ds_ss[i][6], ds_ss[i][2], ds_ss[i][0], ds_ss[i][3], vrtd1,vrtd2,vrtd3, vtemp, tm]) ;
            } 
          }

        })
        .catch((e) => {
          console.error(" ** register read error **");
          console.info(e);
        }) ;

        con.batch('INSERT INTO moteinfo ( mmgb, seq, stand, act, batt, rtd1,rtd2,rtd3,temp, tm ) values (?,?,?,?,?,?,?,?,?,?) ',
         motearr  )
         .catch(err => console.error(err, motearr));

    })
    .catch((e) => {
      console.error("insTemp() D/S error 발생");
      console.info(e);
    })
    .finally( () => client2.close() );

}

async function main()  {
  
  getMeasure();
  // client.connectTCP(GWIP, { port: TAGPORT });

  async () => { while (ws_ss[1][0] == "undefined") await sleep(1000); }
  setInterval(getDevs, 2000, con);

  mainto = setTimeout(main_loop, 2000);
  // mainto = setInterval(main_loop, MEAS * 1000 );
/*   
  setInterval(() => {
    con.getConnection()
    .then( conn => {
    conn.query('INSERT INTO motehist (pkey, mmgb, seq, swseq, stand, batt, act, rtd1,rtd2,rtd3, temp, tm) \
              select pkey, mmgb, seq, swseq, stand, batt, act, rtd1,rtd2,rtd3, temp, tm \
              from moteinfo x where not exists (select 1 from motehist where pkey = x.pkey) ; ')
      // .then( res =>    console.log("hist insert :", res.affectedRows) )
      .catch(err => console.log(err)) ;
    conn.commit() ;
    conn.end() ;
    })
  }, 30000);
  */
  setInterval(() => {
    con.getConnection()
    .then( conn => {
     conn.query( ' delete from moteinfo where tm < DATE_ADD( now() , interval -24 HOUR) ;')
    //  .then( res =>    console.log("delete :",res.affectedRows) )
     .catch(err => console.log(err) ) ;
       conn.commit() ;
       conn.end() ;
       })
     }, 600000) ;
 
   setInterval(() => {
    let buff = ws_buffarr.shift() ;
    if (buff != undefined) setTimeout( ws_sensor_set,0,buff) ;
    let buff2 = ds_buffarr.shift() ;
    if (GWIP_WS != GWIP_DS && buff2 != undefined) setTimeout( ds_sensor_set,0, buff2) ;
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
  setImmediate(insTemp,con) ;
  // await sleep( (MEAS -2) * 1000  );
  
  csec = moment().get('second');
  nextt = moment(moment().set({ 'second': Math.ceil(csec / MEAS) * MEAS, 'millisecond': 0 }));
  
  // console.log(nextt.format("HH:mm:ss")) ;
  mainto = setTimeout(main_loop, nextt - moment());
}

async function main2_loop() {
  let tm1 = new Date();
  con.getConnection()
  .then( async conn => {
    await getDevs(conn) ;
    conn.commit() ;
    conn.release() ;
  });
  let tm2 = new Date();
  let delay = 1000 - (tm2 - tm1) - 10;
  // setTimeout(main2_loop, 9);
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

function ds_sensor_push(mac, act ) {
	//    if (act == -1) return ;
		if (mac.indexOf("00000000") != -1) return;
    let idx = ds_buffarr.findIndex( item => item.toString('hex').substr(0,16) == mac ) ;
    if (idx > -1) ds_buffarr.splice(idx,1) ;
    let buf1 = Buffer.alloc(14);
    buf1.writeInt32LE(act != 0 ? MEAS : 9999,10);
    buf1.write(mac,'hex');
    buf1[8] = 1;
    buf1[9] = 4;
    ds_buffarr.push(buf1) ;
	// console.info(" ds push :" , buf1.toString('hex') );
}

function ws_sensor_set( buf1 ) {

  let socket = net.connect( {port : 40000, host: GWIP_WS}, () => {
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

function ds_sensor_set( buf1 ) {

  let socket = net.connect( {port : 40000, host: GWIP_DS}, () => {
//      console.info(buf1.toString('hex'));
    socket.setNoDelay(false);
    try {
      let ret = socket.write(buf1) ;
    } catch (e) {
      console.log("ds socket write error :", e);
      ds_buffarr.unshift(buf1) ;
      socket.end();
    }
  } );
  socket.on('data', function (data) {
    // let rdata = new Int8Array(data);
    console.log(" *** %s ds return data : %s" , moment().format('MM-DD HH:mm:ss') , data.slice(0,8).toString('hex'), data[8] );
    // if (data[8] != 0)          buffarr.push(buf1) ;
      socket.end();
  });
  socket.on('error', function (err) {
      // console.error(" *** ds Error : ", Date() ,buf1.toString('hex') , JSON.stringify(err));
      // ds_buffarr.unshift(buf1) ;
      setTimeout( ds_sensor_set,500,buf1) ;

  });
  // writeData(socket, buf1) ;
}
/* 
function writeData(socket, data){
  var success = !socket.write(data);
  if (!success){
    (function(socket, data){
      socket.once('drain', function(){
        writeData(socket, data);
      });
    })(socket, data);
  }
}
 */