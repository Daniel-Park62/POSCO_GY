package gy.posco.part;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
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

import com.ibm.icu.util.Calendar;

import gy.posco.model.Motehist;

public class RealTime  {

    EntityManager em = AppMain.emf.createEntityManager();
    Composite parent ;
    public RealTime(Composite parent, int style) {
    	this.parent = parent ;
		postConstruct(parent);
	}

    private class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object input) {
			//return new Object[0];
			return ((List<Motehist>)input).toArray();
		}
		@Override
		public void dispose() {
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
    final Image img_tit = AppMain.getMyimage("realtime_t.png");
    
    private Label bottoml ;

    private TableViewer tv;
    private DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat timeFmt = new SimpleDateFormat("HH:mm");

    private Cursor busyc = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
    private Cursor curc ;
	private Button  btnWS, btnDS, btnT, btnB , btnlocB , btnlocI, btnlocW , btnAct, btnInact, btnGr, btnNogr;
	private Button btnR1, btnR2, btnR3 ;
	private Spinner sstd , sseq ;
    
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		curc = parent.getCursor() ;
		
	    Font font2 = SWTResourceManager.getFont("Tahoma", 16, SWT.NORMAL);
	    Font font21 = SWTResourceManager.getFont("Tahoma", 14, SWT.NORMAL);
	    Font font3 = SWTResourceManager.getFont("맑은 고딕", 13, SWT.NORMAL ) ;
	    Color COLOR_T = Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT) ;

		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);

		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).spacing(0, 0).applyTo(composite);

		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Composite composite_15 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(composite_15);
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(composite_15);
		composite_15.setBackground(COLOR_T);
		
		CLabel lbl = new CLabel(composite_15, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(lbl);
		lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lbl.setImage(img_tit);
		lbl.setText("센서별 이력조회");
		lbl.setFont(SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD ));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(50, -1).numColumns(10).equalWidth(false).spacing(20, -1).applyTo(composite_2);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_2);

		Composite composite_22 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(50, -1).numColumns(12).equalWidth(false).applyTo(composite_22);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(composite_22);
		lbl = new CLabel(composite_2, SWT.NONE) ;
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

		lbl = new CLabel(composite_2, SWT.NONE) ;
		lbl.setText("*Stand");
		lbl.setFont(font2);
		lbl.pack();
		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	
		sstd = new Spinner(composite_2, SWT.BORDER | SWT.CENTER);
		sstd.setMinimum(0);
		sstd.setMaximum(5);
		sstd.setIncrement(1);
		sstd.setFont(font21);
		GridDataFactory.fillDefaults().hint(40, -1).align(SWT.CENTER, SWT.CENTER).applyTo(sstd) ;

		lbl = new CLabel(composite_2, SWT.NONE) ;
		lbl.setText("*센서번호");
		lbl.setFont(font2);
		lbl.pack();
		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	
		sseq = new Spinner(composite_2, SWT.BORDER | SWT.CENTER);
		sseq.setMinimum(0);
		sseq.setMaximum(99);
		sseq.setIncrement(1);
		sseq.setFont(font21);
		GridDataFactory.fillDefaults().hint(50, -1).align(SWT.CENTER, SWT.CENTER).applyTo(sseq) ;

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
    	btnlocW.setText("WR");
    	btnlocW.setFont(font21);
    	btnlocW.setSelection(true);
		
        Group  rGroup3 = new Group (composite_2, SWT.NONE);
        rGroup3.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup3.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnAct = new Button(rGroup3, SWT.CHECK);
    	btnAct.setText("Active ");
    	btnAct.setFont(font21);
    	btnAct.setSelection(true);
    	btnInact = new Button(rGroup3, SWT.CHECK);
    	btnInact.setText("Inactive");
    	btnInact.setFont(font21);
    	btnInact.setSelection(true);

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

		lbl = new CLabel(composite_22, SWT.NONE) ;
		lbl.setText("* 기간 Date/Time ");
		lbl.setBackground(COLOR_T);
		lbl.setFont(font2);
		lbl.pack();

		GridData gdinput = new GridData(100,20);
		DateText fromDate = new DateText(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER  );
		fromDate.setLayoutData(gdinput);
		fromDate.setFont(font21);
		fromDate.addMouseListener(madpt);
		Calsel calsel = new Calsel(composite_22, SWT.NONE,fromDate) ;
		TimeText2 fromTm = new TimeText2(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		fromTm.setLayoutData(gdinput);
		fromTm.setFont(font21);
		{
			lbl = new CLabel(composite_22, SWT.NONE) ;
			lbl.setText(" ~ ");
			lbl.setBackground(COLOR_T);
			lbl.setFont(font2);
			lbl.pack();
		}
		DateText toDate = new DateText(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		toDate.setLayoutData(gdinput);
		toDate.setFont(font21);
		toDate.addMouseListener(madpt);
		calsel = new Calsel(composite_22, SWT.NONE,toDate) ;
		TimeText2 toTm = new TimeText2(composite_22, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		toTm.setLayoutData(gdinput);
		toTm.setFont(font21);

        Group  rGroup5 = new Group (composite_22, SWT.NONE);
        rGroup5.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup5.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnGr = new Button(rGroup5, SWT.RADIO);
    	btnGr.setText("요약보기");
    	btnGr.setFont(font21);
    	btnGr.setSelection(true);
    	btnNogr = new Button(rGroup5, SWT.RADIO);
    	btnNogr.setText("모두보기");
    	btnNogr.setFont(font21);
    	btnNogr.setSelection(false);
		
		Button bq = new Button(composite_22, SWT.PUSH);
		bq.setFont(font21);
		bq.setText(" Search ");
		bq.pack();

		bq.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sfrom = fromDate.getText() + " " + fromTm.getText() + ":00";
				String sto = toDate.getText() + " " + toTm.getText() + ":59" ;
				try {
					Timestamp.valueOf(sfrom) ;
					Timestamp.valueOf(sto) ;
				} catch (Exception e2) {
					MessageDialog.openError(parent.getShell(), "일자확인", "날짜를 바르게 입력하세요.") ;
					return ;
				}
				retriveData(sfrom, sto);
			};
		} );
        
		Button bext = new Button(composite_22, SWT.PUSH);
		bext.setFont(font3);
		bext.setText("파일저장");
		bext.setForeground(SWTResourceManager.getColor( SWT.COLOR_DARK_BLUE ));
		bext.pack();
		bext.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleDateFormat sfmt = new SimpleDateFormat("yyMMdd_HH_mm");
				String sfrom = sfmt.format( Timestamp.valueOf(fromDate.getText() + " " + fromTm.getText() + ":00" ) ) ;
				AppMain.exportTable(tv, 0, "RM_Roll_Chock_Temperature_Data_"+ sfrom );
			}
		} ); 
 
		Composite composite_3 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_3 = new GridLayout(1, false);
		gl_composite_3.marginRight = 20;
		gl_composite_3.marginLeft = 20;
//		gl_composite_3.marginBottom = 5;
		
		composite_3.setLayout(gl_composite_3);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		tv = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION |SWT.VIRTUAL );
		
		tv.setUseHashlookup(true);
		
		bottoml = new Label(composite, SWT.NONE) ;
		GridData gd_blabel = new GridData(SWT.CENTER, SWT.CENTER, true, false );
		gd_blabel.heightHint = 50 ;
		bottoml.setLayoutData(gd_blabel);
		bottoml.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottoml.setFont(font3);
//		bottoml.setSize(500, 30);

		
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font3);
		table.setHeaderBackground(AppMain.coltblh);
	    table.addListener(SWT.MeasureItem,  new Listener() {
	    	@Override
	    	public void handleEvent(Event event) {
	    	event.height = (int)(event.gc.getFontMetrics().getHeight() * 1.8) ;
	    	}
	    });		
		
		String[] cols = { "WD", "센서번호", "Chock","장치위치","날짜/시간","상태정보","온도(℃)","배터리(v)", "상태내용"}; 
		int[]    iw   = { 60, 80, 80, 180,220, 120, 120, 120, 200 };

//		TableViewerColumn tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
		for (int i=0; i<cols.length; i++) {
			TableViewerColumn tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
			tvcol.getColumn().setAlignment(SWT.CENTER);
			tvcol.getColumn().setWidth(iw[i]);
			tvcol.getColumn().setText(cols[i]);
		}

		tv.setContentProvider(new ContentProvider());
		tv.setLabelProvider(new MyLabelProvider());
//		Timestamp time_c = em.createQuery("select max(t.tm) from MoteInfo t", Timestamp.class).getSingleResult() ;
//		todt = em.createQuery("select t.lastm from LasTime t ", Timestamp.class).getSingleResult() ;
		todt = AppMain.appmain.getLasTime(1) ;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(todt.getTime()); 
		cal.add(Calendar.HOUR, -24); 
		
		fmdt = new Date(cal.getTime().getTime());
		fromDate.setText(dateFmt1.format(fmdt ) );
		
		fromTm.setText(timeFmt.format(fmdt ) );
		toDate.setText(dateFmt1.format(todt ) );
		toTm.setText(timeFmt.format(todt ) );

		String sfrom = dateFmt.format(fmdt ) ;
		String sto = dateFmt.format(todt ) ;
		
//		retriveData(sfrom, sto);
//		tv.setInput(findMoteinfo.getMoteHists( fmdt, todt, sno));
//		tv.refresh();

	}

	private void retriveData(String sfrom, String sto ) {
		
		try {
			fmdt = Timestamp.valueOf(sfrom) ;
			todt = Timestamp.valueOf(sto) ;
		} catch (Exception e2) {
			MessageDialog.openError(parent.getShell(), "날짜확인", "날짜입력이 바르지 않습니다.") ;
			return ;
		}

		parent.getShell().setCursor( busyc);
		
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		StringBuilder sbr = new StringBuilder() ;
		sbr.append(String.format("where tm between '%s' and '%s' ", sfrom, sto )) ;
		if (sstd.getSelection() > 0 )
			sbr.append(" and stand = " + sstd.getSelection()) ;

		if ( !btnWS.getSelection() || !btnDS.getSelection())
			sbr.append(" and mmgb = " + ( btnWS.getSelection() ? "'1'" : "'2'"));
		if (sseq.getSelection() > 0 ) 
			sbr.append(" and seq = " + sseq.getSelection()) ;
		
		if ( !btnT.getSelection() || !btnB.getSelection())
			sbr.append(" and tb = " + ( btnT.getSelection() ? "'T'" : "'B'"));
		if ( !btnAct.getSelection() || !btnInact.getSelection())
			sbr.append(" and act = " + ( btnAct.getSelection() ? "2" : "0"));
		if ( !btnlocB.getSelection() || !btnlocI.getSelection() || !btnlocW.getSelection()) {
			sbr.append(" and '") ;
			sbr.append( btnlocB.getSelection() ? "B" : "") ;
			sbr.append( btnlocI.getSelection() ? "I" : "") ;
			sbr.append( btnlocW.getSelection() ? "W" : "") ;
			sbr.append("' RLIKE loc") ;
		}
		String strgr = " group by mmgb, seq, date_format(tm,'%Y%m%d%H%i') , rtd1, stand, loc, tb " ;
		
//		List<Motehist> tempList2 = new ArrayList<Motehist>();
//		ArrayList<Motehist> tempList2 = new ArrayList<Motehist>();
		List<Motehist>  tempList2 = em.createNativeQuery("select pkey, mmgb, seq, swseq, stand, batt, act, rtd1, tm, bno, cntgb, loc, tb, temp_w, temp_d, descript, chocknm, concat(locnm,'-1') locnm, gubun, errflag1  from vMotehist t " 
        		+ sbr.toString() + (btnR1.getSelection() ? "" : " and cntgb = 0 ") + (btnGr.getSelection() ? strgr : "") 
        		+ " union select pkey+991, mmgb, seq, swseq, stand, batt, act, rtd2, tm, bno, cntgb, loc, tb, temp_w, temp_d, descript, chocknm, concat(locnm,'-2'), gubun, errflag2  from vMotehist "
        		+ sbr.toString() + " and cntgb = " + (btnR2.getSelection() ? "1 " : "2 ") + (btnGr.getSelection() ? strgr : "") 
        		+ " union select pkey+992, mmgb, seq, swseq, stand, batt, act, rtd3, tm, bno, cntgb, loc, tb, temp_w, temp_d, descript, chocknm, CONCAT(locnm,'-3'), gubun, errflag3  from vMotehist "
        		+ sbr.toString() + " and seq%2 = 1 and cntgb = " + (btnR3.getSelection() ? "1 " : "2 ") + (btnGr.getSelection() ? strgr : "") 
        		+ "order by mmgb, seq, tm desc, stand, loc, tb desc "
        		, Motehist.class)
				.getResultList();
//		tempList2.setParameter("fmdt", fmdt);
//        qMotes.setParameter("todt", todt);
//        qMotes.setHint(QueryHints.READ_ONLY, HintValues.TRUE);
//        qMotes.getResultList(); // .stream().forEach( t -> tempList2.add(t));
        
        tv.setInput(tempList2);
        
		tv.refresh();
		bottoml.setText(String.format("%,d건",tv.getTable().getItemCount()) );
		bottoml.pack();	
		parent.getShell().setCursor( curc);

	}
	
	private Date fmdt, todt ;

    private MouseAdapter madpt = new MouseAdapter() {
    	@Override
    	public void mouseDoubleClick(MouseEvent e) {
    		
    		Point pt = parent.getDisplay().getCursorLocation() ; 
//    		Point ppt = ((Text)e.getSource()).getParent().getLocation() ;
        	CalDialog cd = new CalDialog(Display.getCurrent().getActiveShell() , pt.x, pt.y + 10 );
        	
            String s = (String)cd.open();
            
            if (s != null) {
            	((Text)e.getSource()).setText(s ) ;
            }
    		super.mouseDoubleClick(e);
    	}
	} ;

	private class MyLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			  Motehist mote = (Motehist) element;
			  String[] act = {"Inactive","Wait","active"} ;
		    switch (columnIndex) {
		    case 0:
		    	return mote.getMmgbNm()  ;
		    case 1:
		    	return mote.getSeq()+"";
		    case 2:
		    	return mote.getChocknm();
		    case 3:
		    	return mote.getLocNmlong() ;
		    case 4:
		    	return dateFmt.format( mote.getTm() ) ;
		    case 5:
		    	return act[mote.getAct()] ;
		    case 6:
		    	return String.format("%.2f", mote.getRtd1()) ;
		    case 7:
		    	return String.format( "%.3f", mote.getBatt()/1000.0 );
		    case 8:
		    	return mote.getErrflag1() == 1 ? "비정상": mote.getRtd() > mote.getTempD() ? "경고" : mote.getRtd() > mote.getTempW() ? "주의":"" ;
		    }
			return null;
		}
		
	}
}
