"use strict";
let WS_NUM = 3;
let DS_NUM = 3;
let SSNUM = 3;
const TAGPORT = 1502;
const DEVPORT = 1503;

let GWIP_WS = process.env.GWIP_WS || "192.168.0.223";
let GWIP_DS = process.env.GWIP_DS || "192.168.0.223";

console.info("(GWIP_WS)", GWIP_WS, "(GWIP_DS)", GWIP_DS);

const moment = require('moment');

const gydb = require('./db/dbconn');

const con = gydb.getPool() ;

let MEAS = 10;

let svtime = moment().subtract(34, "s");

const ModbusRTU = require("modbus-serial");

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

let ws_ss = [];
let ds_ss = [];

let BATTL = 3500;
let mainto ;
process.on("message", async (dat) => {
  if (dat.reload) {
    setImmediate(getMeasure) ;
    return ;
  }
  dat.chgmeas *= 1;
  if ( typeof dat.chgmeas == 'number' ) {
    MEAS = dat.chgmeas ;
    console.log("Interval 변경:",MEAS) ;
    clearTimeout(mainto) ;
    mainto = setInterval( main_loop, MEAS * 1000 - 500) ;
  }
});

function getMeasure() {

   con.query("SELECT seq,act,bno, stand, batt , swseq, spare, gubun, cntgb FROM motestatus  where mmgb = '1' or cntgb = 1 ")
    .then(rows => { 
      rows.forEach((e, i) => { ws_ss[i] = []; ws_ss[i] = [e.act, e.bno, e.stand, e.batt, e.spare, e.gubun, e.seq, e.swseq ] });
    })
    .catch(err => console.error(err));

   con.query("SELECT seq,act,bno,stand, batt , swseq, spare, gubun FROM motestatus  where mmgb = '2' and cntgb = 0 ")
    .then(rows => {
      rows.forEach((e, i) => { ds_ss[i] = []; ds_ss[i] = [e.act, e.bno, e.stand, e.batt, e.spare, e.gubun, e.seq, e.swseq ] });
    })
    .catch(err => console.error(err));

   con.query("SELECT measure, batt FROM MOTECONFIG LIMIT 1")
    .then(row => {
      MEAS = row[0].measure;
      BATTL = row[0].batt;
    })
    .catch(err => MEAS = 10)
    .finally(() => console.info('time interval :' + MEAS, " Battery Limit :" + BATTL));
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
      let rtags = new Uint16Array(124);
      let motearr = new Array();
      await client.readInputRegisters(1, 124)
        .then(async (d) => {
          
          rtags = new Uint16Array(d.data);
          let vrtd1 = 0, vrtd2 = 0, vrtd3 = 0, vtemp = 0;
          for(let i = 0; i < ws_ss.length ; i++){
            if (ws_ss[i][4] == 'Y'  || ws_ss[i][5] != 'S' ) continue ;

            await client.readHoldingRegisters(i*8+1,8)
            .then( d => {
              let mdata = new Uint16Array(d.data) ;

              if (mdata[3] > 0 && mdata[3] != ws_ss[i][2] ) {
                ws_ss[i][2] = mdata[3] ;
                con.query("UPDATE motestatus set stand = ? where mmgb = '1' and seq = ?", [ws_ss[i][2], ws_ss[i][6]]);
              }
              if (mdata[6] == 1 && ws_ss[i][7]  != mdata[7] || mdata[6] == 0 && ws_ss[i][7] > 0 ) {
                ws_ss[i][7] = mdata[6] == 0 ? 0 : mdata[7] ;
                con.query("UPDATE motestatus set swseq = ?  where mmgb = '1' and seq = ?", [ws_ss[i][7], ws_ss[i][6]]) ;
              }
            });            
            let ix = ws_ss[i][7] > 0 ? ws_ss[i][7] : ws_ss[i][6] ;
            if (ix < 43)  {
              let a = ws_ss[i][6] * 2 - 1 ;
              vrtd1 = rtags[a -1] / 100;
              vtemp = rtags[a] / 100;
            } else if (ix < 52) {
              let a = Math.abs(43 - ix) * 4 + 84 ;
              vrtd1 = rtags[a] / 100;
              vrtd2 = rtags[a+1] / 100;
              vrtd3 = rtags[a+2] / 100;
              vtemp = rtags[a+3] / 100;
            } else continue ;
            if ( isNaN(vrtd1) || vrtd1 > 600 ) vrtd1 = 0 ;
            if ( isNaN(vrtd2) || vrtd2 > 600 ) vrtd2 = 0 ;
            if ( isNaN(vrtd3) || vrtd3 > 600 ) vrtd3 = 0 ;
            if ( isNaN(vtemp) || vtemp > 600 ) vtemp = 0 ;
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
        
        con.query("UPDATE motestatus A, moteinfo B  SET  a.status = ( case when b.temp > a.temp_d then 2 when b.temp > a.temp_w then 1 ELSE 0 END ) \
                    WHERE a.mmgb = '1' and a.mmgb = b.mmgb and A.seq = b.seq AND b.tm = (SELECT lastm FROM lastime ) ")
                    .catch(err => console.log("update motestatus :" + err));

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
      let rtags = new Uint16Array(84);
      let motearr = new Array();
      await client2.readInputRegisters(1, 84)
        .then(async (d) => {
          rtags = new Uint16Array(d.data);
          let vrtd1 = 0, vrtd2 = 0, vrtd3 = 0, vtemp = 0 ;
          for(let i = 0; i < ds_ss.length ; i++){
            if (ds_ss[i][4] == 'Y'  || ds_ss[i][5] != 'S' ) continue ;
            await client2.readHoldingRegisters(i*8+1,8)
            .then( d => {
              let mdata = new Uint16Array(d.data) ;

              if (mdata[3] > 0 && mdata[3] != ds_ss[i][2] ) {
                ds_ss[i][2] = mdata[3] ;
                con.query("UPDATE motestatus set stand = ? where mmgb = '2' and seq = ?", [ds_ss[i][2], ds_ss[i][6]]) ;
              }
              if (mdata[6] == 1 && ds_ss[i][7]  != mdata[7] || mdata[6] == 0 && ds_ss[i][7] > 0 ) {
                ds_ss[i][7] = mdata[6] == 0 ? 0 : mdata[7] ;
                con.query("UPDATE motestatus set swseq = ?  where mmgb = '2' and seq = ?", [ds_ss[i][7], ds_ss[i][6]]) ;
              }
            });            
            let ix = ds_ss[i][7] > 0 ? ds_ss[i][7] : ds_ss[i][6] ;
            if (ix < 43)  {
              let a = ix * 2 - 1 ;
              vrtd1 = rtags[a -1] / 100;
              vtemp = rtags[a] / 100;
              if ( isNaN(vrtd1) || vrtd1 > 600 ) vrtd1 = 0 ;
              if ( isNaN(vtemp) || vtemp > 600 ) vtemp = 0 ;
  
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

        con.query("UPDATE motestatus A, moteinfo B  SET  a.status = ( case when b.temp > a.temp_d then 2 when b.temp > a.temp_w then 1 ELSE 0 END ) \
                    WHERE a.mmgb = '2' and a.mmgb = b.mmgb and A.seq = b.seq AND b.tm = (SELECT lastm FROM lastime ) ")
                  .catch(err => console.log("update motestatus :" + err));

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
  console.log("** 4PCM RM Roll 데이터수집 start **") ;
  setInterval(getDevs, 2000, con);

  // mainto = setTimeout(main_loop, nextt - moment());
  mainto = setInterval(main_loop, MEAS * 1000 );
  
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
     
}

let csec = moment().get('second');
let nextt = moment(moment().set({ 'second': Math.ceil(csec / MEAS) * MEAS, 'millisecond': 0 }));
let sv_nextt = moment() ;
main() ;

function main_loop() {
  // console.info(nextt) ;
  // let tm1 = moment();
  // await getDevs();
  setTimeout(() => {
        insTemp(con) ;
    }    ,nextt - moment() ) ;
  
  // await sleep( (MEAS -2) * 1000  );
  
  csec = moment().get('second');
  nextt = moment(moment().set({ 'second': Math.ceil(csec / MEAS) * MEAS, 'millisecond': 0 }));
  
  // console.log(nextt.format("HH:mm:ss")) ;
  // mainto = setTimeout(main_loop, nextt - moment());
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
