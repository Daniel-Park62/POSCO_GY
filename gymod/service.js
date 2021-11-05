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


router.post('/sq', (req, res) => {
  let fdt = moment(req.body.ftm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;
  let tdt = moment(req.body.ttm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;

  // console.log(req.body) ;
  // bodym = JSON.parse(JSON.stringify(req.body));
  // console.log(bodym) ;
  con.query(
      "SELECT " + req.body.gb + " as seq, temp,  date_format(tm,'%Y-%m-%d %T') tm  FROM vmotehist  \
      where "+ req.body.cond + " and tm between ? and ?  and temp between ? and ? order by seq, tm " ,
       [ fdt, tdt, req.body.ftemp, req.body.ttemp] )
    .then( dt => {
        // motesmac = JSON.parse(JSON.stringify(dt)) ;

        let rdata = [];
        let tdata = [];
        let cat = [];
        let sv_seq = dt.length > 0 ? dt[0].seq : 1;
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
        console.error(err) ;
        res.send(err);
      }) ;
});

router.get('/stand/:stand', (req, res) => {

   // console.log(req.params);
  con.query(
      // "SELECT temp_w, temp_d  FROM tb_stand  where standno = ?  "  ,[req.params.stand],
      // 2020 11 23 변경
      "SELECT a.temp_d t_d, b.temp_d b_d FROM tb_stand2 a,tb_stand2 b  where a.stand = ? and b.stand = ?  "
      ,[req.params.stand+'T', req.params.stand+'B']
      ,(err, dat) => {
      if (!err) {
        // motesmac = JSON.parse(JSON.stringify(dt)) ;
        let rdata = {t_d : dat[0].t_d, b_d : dat[0].b_d } ;
        let sdata =  JSON.stringify( rdata ) ;
        // console.log(sdata) ;
        res.json(sdata);
      } else {
        console.error(req,"err->",err) ;
        res.json(err);
      }
  });

});

router.get('/stand/:stand/:ftm', (req, res) => {

  let fdt = moment(req.params.ftm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss') ;

  let whereStr = "a.stand = " + req.params.stand ;
  if (req.params.ftm ) whereStr += " and a.tm = '" +fdt +  "'" ;
  //  console.log("1",whereStr);
  con.query(
   "SELECT a.tb, a.temp temp, date_format(a.tm,'%m/%d %T') tm  FROM vmoteinfo a  where stand = ? and tm = ? "
      ,[req.params.stand,fdt])
    .then( (dt) =>    {

        // motesmac = JSON.parse(JSON.stringify(dt)) ;
        let rdata = {} ;

        dt.forEach((e,i) => {
          if (e.tb == 'T' )
            { rdata.top = [ e.tm, e.temp ] ; }
          else
            { rdata.bottom = [e.tm, e.temp ] ; }
        }) ;

        let sdata =  JSON.stringify( rdata ) ;
        res.json(sdata);
      })
      .catch( err => {
        console.log("err->",err) ;
        res.json(err);
      }) ;

});

router.get('/stand/:stand/:ftm/:ttm', (req, res) => {

  let fdt ="";
  let tdt = moment(req.params.ttm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss');
  if ( req.params.ftm <= 24)
    fdt = moment(tdt,'YYYY-MM-DD HH:mm:ss').subtract(req.params.ftm,"h").format('YYYY-MM-DD HH:mm:ss');
  else
    fdt = moment(req.params.ftm,["YYYYMMDDHHmmss","YYYYMMDD"]).format('YYYY-MM-DD HH:mm:ss');
// console.log(req.params, fdt,tdt);
  // if ( req.params.ftm > req.params.ttm ) tdt = moment(fdt).format('YYYY-MM-DD HH:mm:ss') ;
  let whereStr = "a.stand = " + req.params.stand ;
  if (req.params.ftm ) whereStr += " and a.tm between '" +fdt + "' and '" + tdt + "'" ;
  // console.log("2" ,whereStr);
  con.query(
      "SELECT a.temp atemp, b.temp btemp,  date_format(a.tm,'%m/%d %T') tm  FROM vmoteinfo a , vmoteinfo b \
      where a.tm = b.tm and a.tb = 'T' and b.tb = 'B' and a.stand = b.stand and " + whereStr )
    .then( dt => {
        // motesmac = JSON.parse(JSON.stringify(dt)) ;
        let rdata = [{name:"" , data:[] }] ;
        let tdata = [];
        let bdata = [];
        let cat = [];
        dt.forEach((e,i) => {
          tdata.push(e.atemp);
          bdata.push(e.btemp);
          cat.push(e.tm);
        }) ;
        // rdata[0].name = "TOP" ;
        rdata[0].data = tdata ;

        rdata.push( { data: bdata });

        let sdata =  JSON.stringify( {
          series : rdata ,
          categorie: cat  } );
        res.json(sdata);
        // console.info(sdata) ;
      })
      .catch( err => res.send(err) );
});

module.exports = router;