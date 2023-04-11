Date.prototype.hhmmss = function() {
  var hh = this.getHours().toString();
  var mm = this.getMinutes().toString();
  var ss = this.getSeconds().toString();
  return (hh[1] ? hh : "0" + hh[0]).concat( (mm[1] ? mm : "0" + mm[0]) , (ss[1] ? ss : "0" + ss[0]));
};
Date.prototype.yyyymmdd = function() {
   var yyyy = this.getFullYear().toString();
   var mm = (this.getMonth() + 1).toString();
   var dd = this.getDate().toString();
   return  yyyy.concat( (mm[1] ? mm : "0" + mm[0]) , (dd[1] ? dd : "0" + dd[0]));
};
let chart ;

let standno=0 ;
let categories = ['ahjhjkhj01','ajhhjdeed02','adssddsds03','adssdsddsds04','adsdsdssd05','afdfffd06','afdfdfd07','afdfdfdfd08','afdfdfdf09','afddffdfd10'];
let chartx = {
  chart: {
      height: 650 ,
      // zoomType: 'x',
      type: 'spline',
  },
  // exporting: {
  //   enabled:false
  // },
  title: {
      text: null
  },
  credits: {
      enabled: false
  },
  legend: {
    enabled : false ,
    layout: 'vertical',
    align: 'right',
    verticalAlign: 'middle'
  },
  // subtitle: {
  //     text: 'Source: DawinICT.com'
  // },
  xAxis: {
    crosshair: {
        width: 1,
        color: 'green'
    },
    tickInterval: 50,
    gridLineWidth: 1,
    type: 'datetime',
    labels: {
      formatter: function () {
              if ( typeof(this.value) == 'string' )
                  {
                    return (this.value.substr(5,11));
                  }
              return "";
            },
    },

    categories: ["stand0"]
  },
  yAxis: {
      min :-1 , max :2, tickInterval : 1, 
      title: {
          text: null
      },
      labels: {
        formatter: function () {
            return this.value == 0 ? "InActive" : this.value == 1 ? "Active" : "" ;
            // return Highcharts.numberFormat(this.value, 0) ;
        }
    }
  
  },
  tooltip: {
      // crosshairs: true,
      shared: true,
      outside: true,
      positioner: function(labelWidth, labelHeight, point) {
         var tooltipX = point.plotX + labelWidth;
         return {
             x: tooltipX,
             y: this.chart.plotHeight
         };
     }

  },
  plotOptions: {
      // spline: {
      //     marker: {
      //         radius: 3,
      //         lineColor: '#666666',
      //         lineWidth: 1
      //     }
      // },
      // bar: { showInLegend: false },
      series: {
        // cursor: 'pointer',
        point: {
            events: {
                click: function (e) {
                    // alert('Category: ' + this.category + ', value: ' + this.y);
                    javaFunction( this.category, this.y) ;
                     var label = chart.renderer.label(
                         this.category + "<br>" +
                         ' y: ' + this.y,
                         e.x,
                         e.y
                     )
                         .attr({
                             fill: 'yellow', //Highcharts.getOptions().colors[0],
                             'stroke-width': 2,
                             stroke: 'blue',
                             padding: 10,
                             r: 5,
                             zIndex: 5
                         })
                         // .css({
                         //     color: '#FFFFFF'
                         // })
                         .add();

                     setTimeout(function () {
                         label.fadeOut();
                     }, 3000);
                }
            }
        },
        marker: {
            lineWidth: 1
        }
      }
  },
  series: [{
      name: '1',
      lineWidth: 2,
      marker: {
          symbol: 'square'
      },
      // data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 27, 23.3, 18.3, 13.9, 9.6]

  }, {
      name: '2',
      lineWidth: 2,
      marker: {
          symbol: 'diamond'
      },
      // data: [5, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
  }]
};

chart = Highcharts.chart('container',chartx );
var val= { "cond" : "stand = 1" , "ftm" :"2021120101010101" , "ttm" :"2021122401010101" } ;
// console.info(location.hostname) ;
  // updChart(val,"Stand-01");

function changesize(h) {
chartx.chart.height = h;
}

function updChart(qdata, nm) {
  
  chartx.xAxis.categories = ['조회중'] ;
  chartx.series = [{data:0}];
  chart = Highcharts.chart('container',chartx );

$.post( "http://"+ location.hostname + ":9977/Chart/stat", qdata )
.done(function( data ) {
  let obj = JSON.parse(data);
  console.log( obj );
  chartx.series = [] ;
  // obj.series.forEach((d,i) => chartx.series.push(d) ) ;
  obj.series.forEach( function(d,i) {chartx.series.push(d); chartx.series[0].name = nm; });
  categories = obj.categorie ;
  chartx.xAxis.categories = obj.categorie ;
  chartx.xAxis.tickInterval = obj.categorie.length / 10 ;
  // changesize(700) ;
  chart = Highcharts.chart('container',chartx );

  // chart.reflow();
}).fail(function() {
  alert( "error" + qdata );
});
}
