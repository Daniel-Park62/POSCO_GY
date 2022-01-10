const express = require('express');
const router = express.Router();
const gydb = require('./db/dbconn');
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
  let where_str = req.body.cond + " and tm between '" + fdt + "' and '" + tdt + "' and rtd" ;
  // console.log(req.body, where_str) ;
  // bodym = JSON.parse(JSON.stringify(req.body));
  // console.log(bodym) ;

  con.query(
      "SELECT if(cntgb=1,concat(locnm,'-1'),loc_name) as seq, rtd1 temp,  date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str + "1 between ? and ? "  // + (req.body.rtd1 ? "" : "and cntgb=2 ") 
      + " union select concat(locnm,'-2'), rtd2, date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str + "2 between ? and ? and " + (req.body.rtd2 ? "cntgb=1 " : "cntgb=2 ")
      +" union select concat(locnm,'-3'), rtd3, date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ where_str + "3 between ? and ? and seq%2 = 1 and " + (req.body.rtd3 ? "cntgb=1 " : "cntgb=2 ") +" order by seq, tm",
       [ req.body.ftemp, req.body.ttemp,req.body.ftemp, req.body.ttemp,req.body.ftemp, req.body.ttemp] )
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
      "SELECT date_format(tm,'%Y-%m-%d %T') tm, min(if(act < 2,0,1)) act  FROM vmotehist  \
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