package gy.posco.part;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.json.simple.JSONObject;

import com.ibm.icu.util.Calendar;

import gy.posco.model.TdataAnaly;

public class ViewChart {
	
	final Color CIB = Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	final Color BLUE = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	final Color GREEN = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	final Color CYAN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
    private DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dateFmt2 = new SimpleDateFormat("HH:mm");
    final private DateFormat ymd = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat dtFmt =  new SimpleDateFormat("MM/dd HH:mm.ss") ;
	Timestamp time_c = Timestamp.valueOf("1900-01-01 00:00:00") ;

	org.eclipse.swt.widgets.List id_list; 

	ArrayList<TdataAnaly> tdatarr ;

	final Browser browser ;
	/**
     * Create the composite.
     *
     * @param parent
     * @param style
     * @wbp.parser.entryPoint
     */

	private Label slbl;
	private Combo cbddown ;
	private TableViewer tv; 
	private DateText  fromDate, toDate ;
	private TimeText2 fromTm,  toTm ;
	private Text ftemp, ttemp ; 
	private Button  btnWS, btnDS, btnT, btnB , btnlocB , btnlocI, btnlocW, btnUn, btnR1,btnR2,btnR3;
	private Spinner sstd ;
	
	public ViewChart(Composite parent, int style, String[] bnos, String fdt ) throws InterruptedException {
		this(parent, style);
		id_list.setSelection(bnos);
		fromDate.setText(fdt.substring(0,10));
		fromTm.setText(fdt.substring(11));

		browser.addProgressListener(new ProgressAdapter() {
		  @Override
		  public void completed(ProgressEvent event) {
				refreshChart( );
		  }
		});

	}
    public ViewChart(Composite parent, int style) {
    	
	    final Font font2 = SWTResourceManager.getFont("Tahoma", 16, SWT.NORMAL);
	    final Font font21 = SWTResourceManager.getFont("Tahoma", 14, SWT.NORMAL);
	    final Font font3 = SWTResourceManager.getFont("Tahoma", 18, SWT.NORMAL);
	    final Font fontL = SWTResourceManager.getFont("Microsoft Himalaya", 30, SWT.NORMAL);
	    final Image chart_icon = AppMain.getMyimage("chart_icon.png");

		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(-1, -1, -1, 10).applyTo(parent);

    	Composite comps1 = new Composite(parent, SWT.NONE);
    	comps1.setLayout(new GridLayout(1, false));
    	
    	CLabel lticon = new CLabel(comps1, SWT.NONE);
    	lticon.setImage(chart_icon);
    	GridDataFactory.fillDefaults().align(SWT.FILL, GridData.VERTICAL_ALIGN_CENTER).grab(false, true).applyTo(lticon);
    	lticon.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//    	lticon.setSize(120, 120);
    	
		lticon.setText(" 데이터 분석 " ) ;
		lticon.setFont( SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD ) );
    	
		Composite composite_2 = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(50, -1).numColumns(8).equalWidth(false).spacing(20, -1).applyTo(composite_2);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_2);

		Composite composite_22 = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(50, -1).numColumns(12).equalWidth(false).applyTo(composite_22);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_22);

		Label lbl = new Label(composite_2, SWT.NONE) ;
		lbl.setText("*조회대상선택");
		lbl.setFont(font2);
		lbl.pack();
//		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        Group  rGroup0 = new Group (composite_2, SWT.NONE);
        rGroup0.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup0.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));

        btnWS = new Button(rGroup0, SWT.CHECK);
    	btnWS.setText("W/S ");
    	btnWS.setFont(font21);
    	btnWS.setSelection(true);
    	btnDS = new Button(rGroup0, SWT.CHECK);
    	btnDS.setText("D/S");
    	btnDS.setFont(font21);
    	btnDS.setSelection(true);

		lbl = new Label(composite_2, SWT.NONE) ;
		lbl.setText("*Stand");
		lbl.setFont(font2);
		lbl.pack();
		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	
		sstd = new Spinner(composite_2, SWT.BORDER | SWT.CENTER);
		sstd.setMinimum(1);
		sstd.setMaximum(5);
		sstd.setIncrement(1);
		sstd.setFont(font21);
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.CENTER, SWT.CENTER).applyTo(sstd) ;
		
        Group  rGroup1 = new Group (composite_2, SWT.NONE);
        rGroup1.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup1.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnT = new Button(rGroup1, SWT.CHECK);
    	btnT.setText("Top ");
    	btnT.setFont(font21);
    	btnT.setSelection(true);
    	btnB = new Button(rGroup1, SWT.CHECK);
    	btnB.setText("Bottom");
    	btnB.setFont(font21);
    	btnB.setSelection(true);
    	
        Group  rGroup2 = new Group (composite_2, SWT.NONE);
        rGroup2.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup2.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnlocB = new Button(rGroup2, SWT.CHECK);
    	btnlocB.setText("BUR ");
    	btnlocB.setFont(font21);
    	btnlocB.setSelection(true);
    	btnlocI = new Button(rGroup2, SWT.CHECK);
    	btnlocI.setText("IMR ");
    	btnlocI.setFont(font21);
    	btnlocI.setSelection(true);
    	btnlocW = new Button(rGroup2, SWT.CHECK);
    	btnlocW.setText("WR ");
    	btnlocW.setFont(font21);
    	btnlocW.setSelection(true);
    	btnUn = new Button(rGroup2, SWT.CHECK);
    	btnUn.setText("비접촉");
    	btnUn.setFont(font21);
    	btnUn.setSelection(true);

        Group  rGroup4 = new Group (composite_2, SWT.NONE);
        rGroup4.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup4.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnR1 = new Button(rGroup4, SWT.CHECK);
    	btnR1.setText("비접촉1 ");
    	btnR1.setFont(font21);
    	btnR1.setSelection(true);
    	btnR2 = new Button(rGroup4, SWT.CHECK);
    	btnR2.setText("2 ");
    	btnR2.setFont(font21);
    	btnR2.setSelection(true);
    	btnR3 = new Button(rGroup4, SWT.CHECK);
    	btnR3.setText("3");
    	btnR3.setFont(font21);
    	btnR3.setSelection(true);
    	rGroup4.setVisible(btnUn.getSelection()) ;
    	btnUn.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			rGroup4.setVisible(btnUn.getSelection()) ;
    			super.widgetSelected(e);
    		}
		});
    	
		{
			lbl = new Label(composite_22, SWT.NONE) ;
			lbl.setText("*조회기간 ");
			lbl.setFont(font2);
			lbl.pack();
			lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		}
		
		fromDate = new DateText (composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER  );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.FILL, SWT.CENTER).applyTo(fromDate) ;
		fromDate.setFont(font21);
		fromDate.addMouseListener(madpt);
		Calsel calsel = new Calsel(composite_22, SWT.NONE,fromDate) ;
		fromTm = new TimeText2(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.FILL, SWT.CENTER).applyTo(fromTm) ;
		fromTm.setFont(font21);
		
		{
			lbl = new Label(composite_22, SWT.NONE) ;
			lbl.setText(" ~ ");
			lbl.setFont(font2);
			lbl.pack();
			lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}
		toDate = new DateText(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.FILL, SWT.CENTER).applyTo(toDate) ;
		toDate.setFont(font21);
		toDate.addMouseListener(madpt);
		calsel = new Calsel(composite_22, SWT.NONE,toDate) ;
		toTm = new TimeText2(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.FILL, SWT.CENTER).applyTo(toTm) ;
		toTm.setFont(font21);
		{
			lbl = new Label(composite_22, SWT.NONE) ;
			lbl.setText("  *온도 ");
			lbl.setFont(font2);
			lbl.pack();
		}
		
		VerifyListener vl = new VerifyListener() {
	        @Override
	        public void verifyText(VerifyEvent e) {
			       if (e.text.isEmpty()) { 
			           e.doit = true; 
			          } else if (e.keyCode == SWT.ARROW_LEFT || 
			             e.keyCode == SWT.ARROW_RIGHT || 
			             e.keyCode == SWT.BS || 
			             e.keyCode == SWT.DEL || 
			             e.keyCode == SWT.CTRL || 
			             e.keyCode == SWT.SHIFT) { 
			           e.doit = true; 
			          } else { 
			           e.doit = e.text.matches("^-?[0-9]*\\.?[0-9]*") ;
			          } 
	        }
	    };

		ftemp = new Text(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER);
		ftemp.setText("0");
		ftemp.setFont(font21);
		ftemp.addVerifyListener(vl);
		GridDataFactory.fillDefaults().hint(50, 20).align(SWT.CENTER, SWT.CENTER).applyTo(ftemp);
		ttemp = new Text(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER);
		ttemp.setText("80");
		ttemp.addVerifyListener(vl);
		ttemp.setFont(font21);
		GridDataFactory.fillDefaults().hint(50, 20).align(SWT.CENTER, SWT.CENTER).applyTo(ttemp);
		
		Button searchb = new Button(composite_22, SWT.PUSH);
		searchb.setFont(font2);
		searchb.setText(" Search ");
		searchb.pack();
		searchb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sfrom = fromDate.getText() + " " + fromTm.getText() + ":00" ;
				String sto = toDate.getText() + " " + toTm.getText() + ":59" ;
				
				try {
					Timestamp ts_dt = Timestamp.valueOf(sfrom) ;
					ts_dt = Timestamp.valueOf(sto) ;
				} catch (Exception e2) {
					MessageDialog.openError(null, "날짜확인", "날짜입력을 바르게 하세요.") ;
					return ;
				}
//				refreshChart( cbddown.getSelectionIndex(), sfrom, sto , ftemp.getText(), ttemp.getText());
				refreshChart( );

			}
		}); 

		Date todt = AppMain.appmain.time_c ;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(todt.getTime()); 
		cal.add(Calendar.HOUR, -24); 
		
		Timestamp fmdt = new Timestamp(cal.getTime().getTime());
		fromDate.setText(dateFmt1.format(fmdt ) );
		fromTm.setText(dateFmt2.format(fmdt ) );
		toDate.setText(dateFmt1.format(todt ) );
		toTm.setText(dateFmt2.format(todt ) );
		
//		Composite comp_b = new Composite(parent,  SWT.NONE);
//		GridDataFactory.fillDefaults().align(SWT.FILL,  SWT.FILL).grab(true, true).applyTo(comp_b);
//		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10, 20).spacing(5, 10) .applyTo(comp_b);
//		comp_b.setBackground(SWTResourceManager.getColor(250, 250, 252));
		
		browser = new Browser(parent, SWT.BORDER | SWT.EMBEDDED);
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(browser);
    	final BrowserFunction function = new CustomFunction (browser, "javaFunction", this);
//		browser.setBackground(SWTResourceManager.getColor(250, 250, 252));

		String s = System.getenv("MONIP") ;
        if (s == null) s = "localhost" ;
		browser.setUrl("http://"+ s + ":9977/chart02.html");

		browser.setJavascriptEnabled(true); 
//		browser.addProgressListener(new ProgressAdapter() {
//			  @Override
//			public void completed(ProgressEvent event) {
//				  browser.execute("changesize(" + val + ");");
//			  }
//		});

		/*
		tv = new TableViewer(parent, SWT.FULL_SELECTION |SWT.VIRTUAL );
		tv.setUseHashlookup(true);
		Table table = tv.getTable();
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).hint(-1, 200).applyTo(table);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font21);
		table.setHeaderBackground(AppMain.coltblh);
		
		table.addListener(SWT.MeasureItem,  new Listener() {
	    	@Override
	    	public void handleEvent(Event event) {
	    		event.height = (int)(event.gc.getFontMetrics().getHeight() * 1.6) ;
	    	}
	    });	
		
		String[] cols = { " 구분", "X1", "X2","Y1","Y2","X2-X1","Y2-Y1", "Max","Min","Avg."}; 
		int[]    iw   = {150,      200, 200, 100, 100, 150   , 100,     100,  100,  100 };

		TableViewerColumn tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);

		for (int i=0; i<cols.length; i++) {
			tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
			tvcol.getColumn().setAlignment(SWT.CENTER);
			tvcol.getColumn().setWidth(iw[i]);
			tvcol.getColumn().setText(cols[i]);
		}

		tv.setContentProvider(new ContentProvider());
		tv.setLabelProvider(new MyLabelProvider());
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = (int) (event.gc.getFontMetrics().getHeight() * 1.5);
			}

		});
		*/
		
    }
    private class MeasuresProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return (String) element;
		}
		
	}
    
    private MouseAdapter madpt = new MouseAdapter() {
    	@Override
    	public void mouseDoubleClick(MouseEvent e) {
    		
    		Point pt = AppMain.appmain.getShell().getDisplay().getCursorLocation() ; 
//    		Point ppt = ((Text)e.getSource()).getParent().getLocation() ;
        	CalDialog cd = new CalDialog(Display.getCurrent().getActiveShell() , pt.x, pt.y + 10 );
        	
            String s = (String)cd.open();
            
            if (s != null) {
            	((Text)e.getSource()).setText(s ) ;
            }
    		super.mouseDoubleClick(e);
    	}
	} ;
//	
    private void refreshData( String sfrom, String sto) {
    	int gb = cbddown.getSelectionIndex() ;
		String ids = String.join(",",  id_list.getSelection() );
		
	    EntityManager em = AppMain.emf.createEntityManager();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
        String qstr = String.format("WITH vt AS " + 
        		"(SELECT x.loc_name , x.rtd1 y2temp FROM vmotehist X join " + 
        		"(SELECT seq, MAX(tm) as max_tm FROM vmotehist WHERE %s IN (%s) " +
        		"and tm BETWEEN '%s' AND '%s' GROUP BY seq ) Y " + 
        		"ON x.seq = y.seq AND x.tm = y.max_tm) " 
        		+ "SELECT %s , min(tm  ) stm, MAX(tm) etm , TIMEDIFF(MAX(tm), MIN(tm)) dftm,"
        		+ " FIRST_VALUE(rtd1) OVER ( PARTITION by seq ORDER BY tm) AS  y1temp, "
        		+ " y2temp, "
        		+ " min(rtd1) stemp, max(rtd1) etemp, AVG(rtd1) atemp " + 
        		"FROM vmotehist a join vt b on a.seq = b.seq " + 
        		"WHERE tm BETWEEN '%s' AND '%s' " + 
        		"  AND %s in ( %s ) "  +
        		"GROUP BY a.seq ", gb == 1 ? "seq":"bno", ids,  sfrom,sto, 
        				gb == 1 ? "a.seq":"bno", sfrom,sto, gb == 1 ? "a.seq":"bno", ids ) ;
        
//        System.out.println(qstr);
        List<Object[]> rstl = em.createNativeQuery(qstr ).getResultList() ;
        
        tdatarr = rstl.stream().map( t -> new TdataAnaly( (int)t[0] , (Date)t[1], (Date)t[2],(Time)t[3], 
        		                                       (float)t[4], (float)t[5], (float)t[6], (float)t[7], (double)t[8] ) )
        		.collect(Collectors.toCollection(ArrayList::new));
        
//        System.out.println(tdatarr.size());
//        
//        for (TdataAnaly t : (List<TdataAnaly>)tdatarr) {
//        	System.out.println(t.toString());
//        }

        tv.setInput( tdatarr ) ;
        tv.refresh();

		em.close();
    
    }
    
	private void refreshChart() {

		String sfrom = fromDate.getText() + " " + fromTm.getText() + ":00";
		String sto = toDate.getText() + " " + toTm.getText() + ":59";
//		System.out.println(sfrom + " - " + sto);
		try {
			Timestamp ts_dt = Timestamp.valueOf(sfrom) ;
			ts_dt = Timestamp.valueOf(sto) ;
		} catch (Exception e2) {
			MessageDialog.openError( null, "날짜확인", "날짜입력을 바르게 하세요.") ;
			return ;
		}
		StringBuilder sbr = new StringBuilder() ;
		sbr.append("stand = " + sstd.getSelection()) ;

		if ( !btnWS.getSelection() || !btnDS.getSelection())
			sbr.append(" and mmgb = " + ( btnWS.getSelection() ? "'1'" : "'2'"));
		if ( !btnT.getSelection() || !btnB.getSelection())
			sbr.append(" and tb = " + ( btnT.getSelection() ? "'T'" : "'B'"));
		if ( !btnlocB.getSelection() || !btnlocI.getSelection() || !btnlocW.getSelection()) {
			sbr.append(" and '") ;
			sbr.append( btnlocB.getSelection() ? "B" : "") ;
			sbr.append( btnlocI.getSelection() ? "I" : "") ;
			sbr.append( btnlocW.getSelection() ? "W" : "") ;
			sbr.append("' RLIKE loc") ;
		}

    	String sdt = ymd.format( Timestamp.valueOf(sfrom) ) ;
		String tdt = ymd.format( Timestamp.valueOf(sto) ) ;


		JSONObject jo = new JSONObject() ;
		jo.put("cond", sbr.toString() ) ;
		jo.put("ftemp", ftemp.getText()) ;
		jo.put("ttemp", ttemp.getText());
		jo.put("ftm", sdt) ;
		jo.put("ttm", tdt);
		if (btnUn.getSelection() && btnR1.getSelection() ) jo.put("rtd1", "1" ) ;
		if (btnUn.getSelection() && btnR2.getSelection() ) jo.put("rtd2", "1" ) ;
		if (btnUn.getSelection() && btnR3.getSelection() ) jo.put("rtd3", "1" ) ;
/*		
		String val = "{ \"gb\" : \""  + ( gb == 0 ? "bno":"seq") + "\" ," 
				   + " \"sq\" : [" + ids + "] ,"
				   + " \"ftemp\" :\"" + ftemp.getText() + "\" ,"
				   + " \"ttemp\" :\"" + ttemp.getText() + "\" ,"
				   + " \"ftm\" :\"" + sdt + "\" ,"
				   + " \"ttm\" :\"" + tdt + "\" }"
*/
		   ;
		String val = jo.toJSONString() ;
//		System.out.println(val);
		browser.execute("updChart(" + val + ");");
		browser.requestLayout() ;
//		browser.refresh();
    }

	private class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object input) {
			//return new Object[0];
			@SuppressWarnings("unchecked")
			ArrayList<TdataAnaly> list = (ArrayList<TdataAnaly>)input;
			return list.toArray();
		}
		@Override
		public void dispose() {
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class MyLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			TdataAnaly tdata = (TdataAnaly)element ;
//			System.out.println(tdata.toString());
		    switch (columnIndex) {
		    case 1:
		    	return tdata.getSeq()+"";
		    case 2:
		    	return dtFmt.format(tdata.getStm());
		    case 3:
		    	return dtFmt.format(tdata.getEtm()) ;
		    case 4:
		    	return String.format("%.1f", tdata.getY1temp()) ;
		    case 5:
		    	return String.format("%.1f", tdata.getY2temp()) ;
		    case 6:
		    	return tdata.getDftm().toString() ;
		    case 7:
		    	return String.format("%.1f", Math.abs(tdata.getY2temp() - tdata.getY1temp()) ) ;
		    case 8:
		    	return String.format("%.1f", tdata.getEtemp()) ;
		    case 9:
		    	return String.format("%.1f", tdata.getStemp()) ;
		    case 10:
		    	return String.format("%.1f", tdata.getAtemp()) ;
		    default:
		    	return "";
		    }
		}
		
	}
	
	private static  int sw = 0;
	private static String from_dt, to_dt ; 
	
	static class CustomFunction extends BrowserFunction {
	 
		ViewChart vchart ;
		
		private SimpleDateFormat dtFmt =  new SimpleDateFormat("yyyy-MM-dd HH:mm.ss") ;
		
	    CustomFunction (Browser browser, String name, ViewChart vchart) {
	        super (browser, name);
	        this.vchart = vchart ;
	    }
	    
	    public Object function (Object[] arguments) {
	        Object returnValue = new Object[] {
	                "Java Argument",
	        };
	        if (sw == 0) {
				from_dt =   arguments[0].toString()  ;
				vchart.tdatarr.clear();
				
				vchart.tv.refresh();
				
	        	sw = 1;
	        } else {
				to_dt =  arguments[0].toString()  ;
	        	sw = 0;
	        	if ( from_dt.compareTo(to_dt) > 0) 
	        		vchart.refreshData(  to_dt, from_dt );
	        	else
	        		vchart.refreshData( from_dt,  to_dt );
	        }
//	        System.out.println(arguments[0] + ", " + arguments[1] + ", " + sw );
	        return returnValue;
	    }
	}

}
