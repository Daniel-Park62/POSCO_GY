package gy.posco.part;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ibm.icu.util.Calendar;

import gy.posco.model.Motehist;

public class ViewAlert extends Dialog {

	final Font font = SWTResourceManager.getFont("Calibri", 14, SWT.NORMAL);
	final Font fonth = SWTResourceManager.getFont("맑은 고딕", 13, SWT.NORMAL);
	List<Motehist> tempList ;
	EntityManager em = AppMain.emf.createEntityManager();	
	TableViewer tv ;
	private int gb = 0; // 1.low batt  2. OB
	private class ContentProvider implements IStructuredContentProvider {
		/**
		 * 
		 */
		@Override
		public Object[] getElements(Object input) {
			return ((List<Motehist>) input).toArray();
		}
		@Override
		public void dispose() {
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected ViewAlert(Shell parentShell, int gb ) {
		super(parentShell);
		this.gb = gb ;
		// TODO Auto-generated constructor stub
	}
    private MouseAdapter madpt = new MouseAdapter() {
    	@Override
    	public void mouseDoubleClick(MouseEvent e) {
    		
    		Point pt = AppMain.appmain.getShell().getDisplay().getCursorLocation() ; 
//    		Point ppt = ((Text)e.getSource()).getParent().getLocation() ;
        	CalDialog cd = new CalDialog(Display.getCurrent().getActiveShell() , pt.x, pt.y + 20 );
        	
            String s = (String)cd.open();
            
            if (s != null) {
            	((Text)e.getSource()).setText(s ) ;
            }
    		super.mouseDoubleClick(e);
    	}
	} ;

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("나눔고딕코딩", 12, SWT.NORMAL));
		// TODO Auto-generated method stub
		Composite container = (Composite)super.createDialogArea(parent);
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		parent.setBackgroundMode(SWT.INHERIT_FORCE);

        GridLayoutFactory.fillDefaults().applyTo(container);
        
        Composite comp_tit = new Composite(container, 0);
        GridLayoutFactory.fillDefaults().numColumns(7).equalWidth(false).margins(5, 5).applyTo(comp_tit);
        Label lblt = new Label( comp_tit,0) ;
        lblt.setText((gb == 1 ? "Low Battery 이력조회" : "온도경고 이력조회") );
        lblt.setFont(font);

		DateText fromDate = new DateText(comp_tit, SWT.SINGLE | SWT.BORDER | SWT.CENTER  );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.END, SWT.CENTER).applyTo(fromDate);
		fromDate.setFont(font);
		fromDate.addMouseListener(madpt);
		Calsel calsel = new Calsel(comp_tit, SWT.NONE,fromDate) ;
//		calsel.setTarget(fromDate);

		lblt = new Label(comp_tit, SWT.NONE) ;
		lblt.setText(" ~ ");
		lblt.pack();

		DateText toDate = new DateText(comp_tit, SWT.SINGLE | SWT.BORDER | SWT.CENTER );
		GridDataFactory.fillDefaults().hint(100, -1).align(SWT.END, SWT.CENTER).applyTo(toDate);
		toDate.setFont(font);
		toDate.addMouseListener(madpt);
		calsel = new Calsel(comp_tit, SWT.NONE,toDate) ;
		
		Date todt = AppMain.appmain.time_c ;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(todt.getTime()); 
		cal.add(Calendar.DATE, -1); 
		
		Timestamp fmdt = new Timestamp(cal.getTime().getTime());
		fromDate.setText(fmt1.format(fmdt ) );
		toDate.setText(fmt1.format(todt ) );
		
		Button searchb = new Button(comp_tit, SWT.PUSH);
		searchb.setFont(font);
		searchb.setText(" 조회 ");
		searchb.pack();
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(searchb);
		searchb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sfrom = fromDate.getText()  ;
				String sto = toDate.getText()  ;
				
//				try {
//					Timestamp ts_dt = Timestamp.valueOf(sfrom) ;
//					ts_dt = Timestamp.valueOf(sto) ;
//				} catch (Exception e2) {
//					MessageDialog.openError(parent.getShell(), "날짜확인", "날짜입력을 바르게 하세요.") ;
//					return ;
//				}
				try {
					refreshList(sfrom, sto);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}); 
		
		tv = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION );
		tv.setUseHashlookup(true);
		Table table = tv.getTable();
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);
		
		table.setHeaderBackground(AppMain.coltblh);

		String[] cols = { "날짜/시간", "Chock", "센서번호","장치위치","상태정보","온도","배터리", "상태내용"}; 
		int[]    iw   = { 200, 80, 80, 180, 80, 100, 100, 100 };

		TableViewerColumn tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
		for (int i=0; i<cols.length; i++) {
			tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
			tvcol.getColumn().setAlignment(SWT.CENTER);
			tvcol.getColumn().setWidth(iw[i]);
			tvcol.getColumn().setText(cols[i]);
		}

		tv.setContentProvider(new ContentProvider());
		tv.setLabelProvider(new MyLabelProvider());

		tv.setContentProvider(new ContentProvider());
		tv.setLabelProvider(new MyLabelProvider());
		tv.setInput(tempList);
		
	    table.addListener(SWT.MeasureItem,  new Listener() {
	    	@Override
	    	public void handleEvent(Event event) {
	    	event.height = (int)(event.gc.getFontMetrics().getHeight() * 1.8) ;
	    	}

	    });		
	    
        // CellEditor 생성 
        CellEditor[] CELL_EDITORS = new CellEditor[cols.length];
        for(int i=1; i < CELL_EDITORS.length ; i++) {
            CELL_EDITORS[i] = new TextCellEditor(table);
        }        
        tv.setCellEditors(CELL_EDITORS);
	    
		return container ;
	}
	
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, "닫기", false).setFont(fonth);

//        parent.getShell().pack();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(1000, 600);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
		if (gb == 1) 
			shell.setText("Low Battery 이력조회");
		else
			shell.setText("Out Bound 이력조회");
    }
    
    SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd") ;
    
	public void refreshList(String fdt, String tdt) throws ParseException {
		
		Date fd = fmt1.parse(fdt) ;
		Date td = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tdt + " 23:59:59") ;

		
		tempList = new ArrayList<Motehist>();
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
        String qstr ;
        if (gb ==  1)
        	qstr = "select t from Motehist t, Moteconfig c where t.act = 2 and t.batt > 0 and t.batt < c.batt and t.tm between ?1 and ?2  order by t.tm desc" ;
        else 
        	qstr = "select t from Motehist t where t.tempD < t.temp and t.tm between ?1 and ?2 order by t.tm desc" ;
		tempList = em.createQuery(qstr ,Motehist.class)
				.setParameter(1,  fd)
				.setParameter(2,  td )
				.getResultList() ;

		tv.setInput(tempList);
		tv.refresh();

//		}

	}

	private class MyLabelProvider implements ITableLabelProvider {
	  /**
	   * Returns the image
	   * 
	   * @param element
	   *            the element
	   * @param columnIndex
	   *            the column index
	   * @return Image
	   */
	
	  @Override
	public Image getColumnImage(Object element, int columnIndex) {
	    return null;
	  }

	  /**
	   * Returns the column text
	   * 
	   * @param element
	   *            the element
	   * @param columnIndex
	   *            the column index
	   * @return String
	   */
	  @Override
	public String getColumnText(Object element, int columnIndex) {
		  Motehist mote = (Motehist) element;
		  String[] act = {"Inactive","Wait","active"} ;
		  DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    switch (columnIndex) {
	    case 1:
	    	return dateFmt.format( mote.getTm() ) ;
	    case 2:
	    	return mote.getChocknm();
	    case 3:
	    	return mote.getSeq()+"";
	    case 4:
	    	return mote.getMmgbNm() + " " + mote.getLocNmlong();
	    case 5:
	    	return act[mote.getAct()] ;
	    case 6:
	    	return String.format("%.2f", mote.getRtd()) ;
	    case 7:
	    	return mote.getBatt() +"";
	    case 8:
	    	return mote.getRtd() > mote.getTempD() ? "위험" : mote.getRtd() > mote.getTempW() ? "경고":"" ;
	    }
		return null;
	  }

	  /**
	   * Adds a listener
	   * 
	   * @param listener
	   *            the listener
	   */
	  @Override
	public void addListener(ILabelProviderListener listener) {
	    // Ignore it
	  }

	  @Override
	public void dispose() {
	    // Nothing to dispose
	  }

	  @Override
	public boolean isLabelProperty(Object element, String property) {
	    return false;
	  }


	  @Override
	public void removeListener(ILabelProviderListener listener) {
	    // Ignore
	  }
	}

}
