package gy.posco.part;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ibm.icu.util.Calendar;

import gy.posco.model.Motehist;
import gy.posco.model.Moteinfo;

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
			return ((ArrayList<Motehist>)input).toArray();
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
    private Combo cbddown ;
    private Cursor busyc = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
    private Cursor curc ;
    private String[] slist, slistA ;
    private Button  btnm ,btnb ,btnS,  btnWS, btnDS;
    
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
		GridLayout gl_in = new GridLayout(14,false);
		gl_in.marginRight = 50;
		gl_in.marginLeft = 40;
		
		composite_2.setLayout(gl_in);
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
			.grab(true, false).hint(SWT.DEFAULT, 60).applyTo(composite_2);

        Group  rGroup0 = new Group (composite_2, SWT.NONE);
        rGroup0.setLayout(new RowLayout(SWT.VERTICAL));
        rGroup0.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
    	btnWS = new Button(rGroup0, SWT.CHECK);
    	btnWS.setText("W/S");
    	btnWS.setFont(font3);
    	btnWS.setSelection(true);
    	btnDS = new Button(rGroup0, SWT.CHECK);
    	btnDS.setText("D/S");
    	btnDS.setFont(font3);
    	btnDS.setSelection(true);

        Group  rGroup1 = new Group (composite_2, SWT.NONE);
        rGroup1.setLayout(new RowLayout(SWT.HORIZONTAL));
        rGroup1.setFont(SWTResourceManager.getFont(  "", 1, SWT.NORMAL));
        
    	btnm = new Button(rGroup1, SWT.RADIO);
    	btnm.setText("센서#");
    	btnm.setFont(font2);
    	btnm.setSelection(true);
    	btnm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				slist = AppMain.appmain.sno ;
				slistA = new String[slist.length + 1] ;
				
				System.arraycopy(new String[] {" ALL "}, 0, slistA, 0, 1);
				System.arraycopy(slist,0,slistA, 1, slist.length ) ;

				cbddown.setItems(slistA);
				cbddown.select(0);
				cbddown.pack();
			}
		});

    	btnb = new Button(rGroup1, SWT.RADIO);
    	btnb.setText("설치위치 ");
    	btnb.setFont(font2);

    	btnb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				cbddown.setItems(new String[] {"ALL","BUR","IMR","WR"});
				cbddown.select(0);
				cbddown.pack();
			}
		});

    	btnS = new Button(rGroup1, SWT.RADIO);
    	btnS.setText("Stand#");
    	btnS.setFont(font2);

    	btnS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				slist = AppMain.appmain.stand ;
				slistA = new String[slist.length + 1] ;
				
				System.arraycopy(new String[] {" ALL "}, 0, slistA, 0, 1);
				System.arraycopy(slist,0,slistA, 1, slist.length ) ;

				cbddown.setItems(slistA);
				cbddown.select(0);
				cbddown.pack();
			}
		});

		cbddown = new Combo(composite_2, SWT.DROP_DOWN | SWT.BORDER);
		cbddown.setFont(font2);
		
		slist = AppMain.appmain.sno ;
		slistA = new String[slist.length + 1] ;
		
		System.arraycopy(new String[] {" ALL "}, 0, slistA, 0, 1);
		System.arraycopy(slist,0,slistA, 1, slist.length ) ;

		cbddown.setItems(slistA);
		cbddown.select(0);
		cbddown.pack();
        
		
		{
			lbl = new CLabel(composite_2, SWT.NONE) ;
			lbl.setText("  * 기간 Date/Time ");
			lbl.setBackground(COLOR_T);
			lbl.setFont(font2);
			lbl.pack();
		}
		GridData gdinput = new GridData(100,20);
		DateText fromDate = new DateText(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER  );
		fromDate.setLayoutData(gdinput);
		fromDate.setFont(font21);
		fromDate.addMouseListener(madpt);
		Calsel calsel = new Calsel(composite_2, SWT.NONE,fromDate) ;
		TimeText2 fromTm = new TimeText2(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		fromTm.setLayoutData(gdinput);
		fromTm.setFont(font21);
		{
			lbl = new CLabel(composite_2, SWT.NONE) ;
			lbl.setText(" ~ ");
			lbl.setBackground(COLOR_T);
			lbl.setFont(font2);
			lbl.pack();
		}
		DateText toDate = new DateText(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		toDate.setLayoutData(gdinput);
		toDate.setFont(font21);
		toDate.addMouseListener(madpt);
		calsel = new Calsel(composite_2, SWT.NONE,toDate) ;
		TimeText2 toTm = new TimeText2(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		toTm.setLayoutData(gdinput);
		toTm.setFont(font21);

		Button bq = new Button(composite_2, SWT.PUSH);
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
        
		Button bext = new Button(composite_2, SWT.PUSH);
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
		
		int sno = 0 ;
		if  (cbddown.getSelectionIndex() > 0) sno = Integer.parseInt(cbddown.getText() ) ;
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
//		System.out.println(sfrom + " " + sto);
		int gb = 0 ;
		String qval = "";
		if  (cbddown.getSelectionIndex() > 0) {
			qval = cbddown.getText() ;
			if (btnm.getSelection() ) gb = 1;
			if (btnb.getSelection() ) gb = 2;
			if (btnS.getSelection() ) gb = 3;
		}

		parent.getShell().setCursor( busyc);
		
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		String sseqif ;
		switch (gb) {
		case 1:
			sseqif = "and t.seq = " + qval ;
			break;
		case 2:
			sseqif = "and t.loc = '" + qval.substring(0,1) + "'";
			break;
		case 3:
			sseqif = "and t.stand = " + qval ;
			break;
		default:
			sseqif = "" ;
			break;
		}
		
		if (btnWS.getSelection() && !btnDS.getSelection()) 
			sseqif += " and t.mmgb = '1' " ;
		else if ( ! btnWS.getSelection() && btnDS.getSelection()) 
			sseqif += " and t.mmgb = '2' " ;
		
		ArrayList<Motehist> tempList2 = new ArrayList<Motehist>();
        TypedQuery<Motehist> qMotes = em.createQuery("select t from Motehist t " 
        		+ "where t.tm between :fmdt and :todt " + sseqif + " order by t.mmgb, t.seq, t.bno, t.stand, t.loc, t.tm desc"
        		, Motehist.class);

        qMotes.setParameter("fmdt", fmdt);
        qMotes.setParameter("todt", todt);
        qMotes.setHint(QueryHints.READ_ONLY, HintValues.TRUE);
        qMotes.getResultList().stream().forEach( t -> tempList2.add(t));
        
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
		    	return mote.getLocNmlong();
		    case 4:
		    	return dateFmt.format( mote.getTm() ) ;
		    case 5:
		    	return act[mote.getAct()] ;
		    case 6:
		    	return String.format("%.2f", mote.getRtd()) ;
		    case 7:
		    	return String.format( "%.3f", mote.getBatt()/1000.0 );
		    case 8:
		    	return mote.getRtd() > mote.getTempD() ? "위험" : mote.getRtd() > mote.getTempW() ? "경고":"" ;
		    }
			return null;
		}
		
	}
}
