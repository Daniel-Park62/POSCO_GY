package gy.posco.part;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.json.simple.JSONObject;

import com.ibm.icu.util.Calendar;

import gy.posco.model.TdataAnaly;

public class ViewChart2 {
	
	final Color CIB = Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	final Color BLUE = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	final Color GREEN = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	final Color CYAN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
    private DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dateFmt2 = new SimpleDateFormat("HH:mm");
    final private DateFormat ymd = new SimpleDateFormat("yyyyMMddHHmmss");

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
	private Button  btnWS, btnDS, btnStd, btnSq ;
	private Spinner sstd ;
	
	public ViewChart2(Composite parent, int style, String[] bnos, String fdt ) throws InterruptedException {
		this(parent, style);
		id_list.setSelection(bnos);
		fromDate.setText(fdt.substring(0,10));
		fromTm.setText(fdt.substring(11));
		AppMain.appmain.callf = new IcallFunc() {
			@Override
			public void callFunc() {
			}

			@Override
			public void finalFunc() {
				browser.stop();
			}
		};

		browser.addProgressListener(new ProgressAdapter() {
		  @Override
		  public void completed(ProgressEvent event) {
//				refreshChart( );
//				browser.getShell().setCursor(AppMain.handc);
				System.out.println("aaaaa");
		  }
		});

	}
    public ViewChart2(Composite parent, int style) {
    	
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
    	
		lticon.setText(" 통신상태 분석 " ) ;
		lticon.setFont( SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD ) );
    	
		Composite composite_2 = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(20, -1).numColumns(12).equalWidth(false).applyTo(composite_2);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_2);

//		Composite composite_22 = new Composite(parent, SWT.NONE);
//		GridLayoutFactory.fillDefaults().margins(50, -1).numColumns(12).equalWidth(false).applyTo(composite_22);
//		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_22);

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

        Group  rGroup1 = new Group (composite_2, SWT.NONE);
        rGroup1.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup1.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
        btnStd = new Button(rGroup1, SWT.RADIO);
        btnStd.setText("Stand ");
        btnStd.setFont(font21);
    	btnStd.setSelection(true);
    	btnSq = new Button(rGroup1, SWT.RADIO);
    	btnSq.setText("센서번호");
    	btnSq.setFont(font21);
    	btnSq.setSelection(false);

		sstd = new Spinner(composite_2, SWT.BORDER | SWT.CENTER);
		sstd.setMinimum(1);
		sstd.setMaximum(99);
		sstd.setIncrement(1);
		sstd.setFont(font21);
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.CENTER, SWT.CENTER).applyTo(sstd) ;
		
		Label lbl = new Label(composite_2, SWT.NONE) ;
		lbl.setText("  *조회기간 ");
		lbl.setFont(font2);
		lbl.pack();
		lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		fromDate = new DateText (composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER  );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.FILL, SWT.CENTER).applyTo(fromDate) ;
		fromDate.setFont(font21);
		fromDate.addMouseListener(madpt);
		Calsel calsel = new Calsel(composite_2, SWT.NONE,fromDate) ;
		fromTm = new TimeText2(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.FILL, SWT.CENTER).applyTo(fromTm) ;
		fromTm.setFont(font21);
		
		{
			lbl = new Label(composite_2, SWT.NONE) ;
			lbl.setText(" ~ ");
			lbl.setFont(font2);
			lbl.pack();
			lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}
		toDate = new DateText(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.FILL, SWT.CENTER).applyTo(toDate) ;
		toDate.setFont(font21);
		toDate.addMouseListener(madpt);
		calsel = new Calsel(composite_2, SWT.NONE,toDate) ;
		toTm = new TimeText2(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(60, -1).align(SWT.FILL, SWT.CENTER).applyTo(toTm) ;
		toTm.setFont(font21);

		Button searchb = new Button(composite_2, SWT.PUSH);
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
				browser.stop();
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

//		browser.setBackground(SWTResourceManager.getColor(250, 250, 252));

		String s = System.getenv("MONIP") ;
        if (s == null) s = "localhost" ;
		browser.setUrl("http://"+ s + ":9977/chart01.html");

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
		sbr.append( (btnStd.getSelection() ? "stand = " : "seq = ") + sstd.getSelection()) ;

		if ( !btnWS.getSelection() || !btnDS.getSelection())
			sbr.append(" and mmgb = " + ( btnWS.getSelection() ? "'1'" : "'2'"));

    	String sdt = ymd.format( Timestamp.valueOf(sfrom) ) ;
		String tdt = ymd.format( Timestamp.valueOf(sto) ) ;

		JSONObject jo = new JSONObject() ;
		jo.put("cond", sbr.toString() ) ;
		jo.put("ftm", sdt) ;
		jo.put("ttm", tdt);

		String val = jo.toJSONString() ;
//		System.out.println("updChart(" + val + ",\"" + (btnStd.getSelection() ? "stand " : "센서번호 ") + sstd.getSelection() + "\")");
//		browser.getShell().setCursor( AppMain.busyc);
		browser.execute("updChart(" + val + ",\"" + (btnStd.getSelection() ? "stand " : "센서번호 ") + sstd.getSelection() + "\")" );
		
//		browser.refresh();
    }

}
