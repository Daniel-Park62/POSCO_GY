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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ibm.icu.util.Calendar;

import gy.posco.model.Motehist;

public class MoteChanged  {

    EntityManager em = AppMain.emf.createEntityManager();
    Composite parent ;
    public MoteChanged(Composite parent, int style) {
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
    private DateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
    private Combo cbddown ;
    private Cursor busyc = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
    private Cursor curc ;
    private String[] slist, slistA ;
    private Button  btnm ,btnb ,btnS, btnWS, btnDS;
    
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
		lbl.setText("센서 변경 이력");
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
		TimeText fromTm = new TimeText(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
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
		TimeText toTm = new TimeText(composite_2, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		toTm.setLayoutData(gdinput);
		toTm.setFont(font21);

		Button bq = new Button(composite_2, SWT.PUSH);
		bq.setFont(font21);
		bq.setText(" Search ");
		bq.pack();

		bq.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sfrom = fromDate.getText() + " " + fromTm.getText() ;
				String sto = toDate.getText() + " " + toTm.getText() ;
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
		bext.setFont(font21);
		bext.setText("파일저장");
		bext.setForeground(SWTResourceManager.getColor( SWT.COLOR_DARK_BLUE ));
		bext.pack();
		bext.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleDateFormat sfmt = new SimpleDateFormat("yyMMdd_HH_mm_ss");
				String sfrom = sfmt.format( Timestamp.valueOf(fromDate.getText() + " " + fromTm.getText()) ) ;
				AppMain.exportTable(tv, 0, "RM_Roll_changed_"+ sfrom );
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
		
		String[] cols = { "WD구분", "날짜/시간", "장치위치", "센서번호"}; 
		int[]    iw   = { 100, 200, 250, 200 };

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
		todt = AppMain.appmain.time_c ;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(todt.getTime()); 
		cal.add(Calendar.HOUR, -24); 
		
		fmdt = new Timestamp(cal.getTime().getTime());
		fromDate.setText(dateFmt1.format(fmdt ) );
		
		fromTm.setText(timeFmt.format(fmdt ) );
		toDate.setText(dateFmt1.format(todt ) );
		toTm.setText(timeFmt.format(todt ) );


	}

	private void retriveData(String sfrom, String sto ) {
		
		try {
			Timestamp.valueOf(sfrom) ;
			Timestamp.valueOf(sto) ;
		} catch (Exception e2) {
			MessageDialog.openError(parent.getShell(), "날짜확인", "날짜입력이 바르지 않습니다.") ;
			return ;
		}
		String qval = "";
		String qcol = (btnS.getSelection() ? "'" + cbddown.getText() +"'"  : cbddown.getText()) ;
		if (btnb.getSelection()) qcol = qcol.substring(0, 1) ; 
		if  (cbddown.getSelectionIndex() > 0) {
			qval = "where " +  (btnm.getSelection() ? "seq " : btnb.getSelection() ? "loc " : "stand " ) + "= " + qcol;
		}

		parent.getShell().setCursor( busyc);
		
		String qstr = String.format( "WITH VT AS " + 
				"(SELECT pkey,mmgb, seq, loc, stand ,tb,  tm,  " + 
				"        lag(stand,1) OVER (PARTITION BY SEQ ORDER BY TM) LSTAND " + 
				"FROM vmotehist  %s  ) " + 
				"SELECT * FROM VT WHERE STAND <> IFNULL(LSTAND,'') and tm between '%s' and '%s' " , qval , sfrom , sto );

		List<Motehist> motelist = em.createNativeQuery(qstr, Motehist.class)
				.getResultList();
		tv.setInput(motelist);
		tv.refresh();
		bottoml.setText(String.format("%,d건",tv.getTable().getItemCount()) );
		bottoml.pack();	
		parent.getShell().setCursor( curc);

	}
	private Date fmdt, todt; 

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
		    switch (columnIndex) {
		    case 0:
		    	return mote.getMmgbNm() ;
		    case 1:
		    	return dateFmt.format( mote.getTm() ) ;
		    case 2:
		    	return mote.getLocNmlong();
		    case 3:
		    	return mote.getSeq()+"";

		    }
			return null;
		}
		
	}
}
