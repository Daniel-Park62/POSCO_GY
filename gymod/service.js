const express = require('express');
const router = express.Router();
const gydb = require('./db/dbconn');
const util = require('util') ;
let con = gydb.getPool() ;

const moment = require('moment');

router.use(express.static('chart'));

router.all('/*', function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  next();
});

router.post('/stand', (req, res) => {
  let fdt = moment(req.body.ftm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;
  let tdt = moment(req.body.ttm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;
  let where_str = req.body.cond + util.format(" and tm between '%s' and '%s' and rtd1 between %d and %d ", fdt,tdt,req.body.ftemp, req.body.ttemp)  ;
  let where_str2 = req.body.cond2 + util.format(" and tm between '%s' and '%s' and cntgb = 1 and rtd2 between %d and %d ", fdt,tdt,req.body.ftemp, req.body.ttemp)  ;
  let where_str3 = req.body.cond2 + util.format(" and tm between '%s' and '%s' and cntgb = 1 and rtd3 between %d and %d and seq%2 = 1 ", fdt,tdt,req.body.ftemp, req.body.ttemp)  ;
  // console.log(req.body, where_str) ;
  // bodym = JSON.parse(JSON.stringify(req.body));
  // console.log(bodym) ;

  con.query(
      "SELECT loc_name as seq, rtd1 temp,  date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str + " and cntgb = 0 "
      + (req.body.rtd1 ? " union select concat(locnm,seq,'-1'), rtd1, date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str2 + " and cntgb = 1 " : "") 
      + (req.body.rtd2 ? " union select concat(locnm,seq,'-2'), rtd2, date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str2 + " and cntgb = 1 " : "") 
      + (req.body.rtd3 ? " union select concat(locnm,seq,'-3'), rtd3, date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str3 + " and seq%2=1 and cntgb = 1 " : "")  +" order by seq, tm" )
    .then( dt => {
        // motesmac = JSON.parse(JSON.stringify(dt)) ;

        let rdata = [];
        let tdata = [];
        let cat = [];
        let sv_seq = dt.length > 0 ? dt[0].seq : '1';
        let sw=0 ;
        dt.forEach((e,i) => {
          if  (sv_seq != e.seq  ) {
            rdata.push( {name : sv_seq, lineWidth: 2, data: tdata} ) ;
            sv_seq = e.seq ;
            tdata = [] ;
            sw = 1;
          }
          tdata.push(e.temp);
          if (sw == 0) cat.push(e.tm);
        }) ;
        if (dt.length > 0 )
          rdata.push( {name : sv_seq, lineWidth: 2, data: tdata} ) ;

        let sdata =  JSON.stringify( {
          series : rdata,
          categorie: cat} );
          // console.log(sdata) ;
        res.json(sdata);
      })
      .catch( err => {
        console.error(err.sql) ;
        res.send(err);
      }) ;
});

router.post('/stat', (req, res) => {
  // console.log(req.body) ;
  let fdt = moment(req.body.ftm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;
  let tdt = moment(req.body.ttm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;
  let where_str = req.body.cond + " and tm between '" + fdt + "' and '" + tdt +"'";
  // console.log(req.body, where_str) ;
  // bodym = JSON.parse(JSON.stringify(req.body));
  // console.log(bodym) ;

  con.query(
      "SELECT date_format(tm,'%Y-%m-%d %T') tm, min(if(act < 2,0,1)) act  FROM motehist  \
      where "+ where_str + " group by tm order by tm " )
    .then( dt => {
        // motesmac = JSON.parse(JSON.stringify(dt)) ;

        let rdata = [];
        let tdata = [];
        let cat = [];

        dt.forEach((e,i) => {
          tdata.push(e.act);
          cat.push(e.tm);
        }) ;
        if (dt.length > 0 )
          rdata.push( { lineWidth: 2, data: tdata} ) ;

        let sdata =  JSON.stringify( {
          series : rdata,
          categorie: cat} );
          // console.log(sdata) ;
        res.json(sdata);
      })
      .catch( err => {
        console.error(err.sql) ;
        res.send(err);
      }) ;
});


module.exports = router;