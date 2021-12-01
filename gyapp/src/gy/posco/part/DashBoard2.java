package gy.posco.part;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;

import gy.posco.model.Moteinfo;

public class DashBoard2 {

	Image img_top2 = AppMain.getMyimage("dashboard2_t1.png");

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Font font1 = SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD  ) ;
    Font font2 = SWTResourceManager.getFont("맑은 고딕", 13, SWT.NORMAL);
    Font font13 = SWTResourceManager.getFont("Calibri", 13, SWT.NORMAL ) ;
    Font font12 = SWTResourceManager.getFont("맑은 고딕", 12, SWT.NORMAL ) ;
    
	
	private Date time_c = new Date() ;
    private TableViewer tv, tv2;

    EntityManager em = AppMain.emf.createEntityManager();

	public DashBoard2(Composite parent, int style) {

//		super(parent, style) ;
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(0, 0, 0, 10).applyTo(composite);
		
		Composite composite_l = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(10, 0).applyTo(composite_l);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(composite_l);
		CLabel lbl = new CLabel(composite_l, SWT.NONE);
		lbl.setImage(img_top2);
		lbl.setText("스탠드별 센서상태");
		lbl.setFont(font1);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(lbl);
		lbl = new CLabel(composite_l, SWT.NONE);
		lbl.setText("Work side												Drive side");
		lbl.setFont(font2);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(lbl);
		
		Composite composite_d = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(10, -1).numColumns(2).equalWidth(true).applyTo(composite_d);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(composite_d);

		tv = new TableViewer(composite_d, SWT.BORDER | SWT.VIRTUAL | SWT.HIDE_SELECTION );
		create_table(tv) ;
		tv2 = new TableViewer(composite_d, SWT.BORDER | SWT.VIRTUAL | SWT.HIDE_SELECTION );
		create_table(tv2) ;
		
		refreshSensorList();
		composite_d.layout();
		
		AppMain.appmain.callf = new IcallFunc() {
			
			@Override
			public void callFunc() {
				refreshSensorList();
			}
			@Override
			public void finalFunc() {
				// TODO Auto-generated method stub
				
			}
		};

	}

	void create_table(TableViewer tvv) {
		tvv.setContentProvider(new ContentProvider());
		
		Table tbl = tvv.getTable();
		tbl.setHeaderVisible(true);
		tbl.setLinesVisible(true);
		tbl.setFont(font2);
		
		tbl.setHeaderBackground(AppMain.coltblh);
	    tbl.addListener(SWT.MeasureItem,  new Listener() {
	    	@Override
	    	public void handleEvent(Event event) {
	    		event.height = (int)(event.gc.getFontMetrics().getHeight() * 1.4) ;
	    	}
	    });		
//	    tbl.layout();
		tvv.setUseHashlookup(true);	

		TableViewerColumn tvc = new TableViewerColumn(tvv, SWT.CENTER);
		tvc.getColumn().setWidth(0);
		tvc.setLabelProvider(new ColumnLabelProvider() {} );

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(60) ;
		tvc.getColumn().setAlignment(SWT.CENTER);
		tvc.getColumn().setText("센서");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				return element == null ? "" :String.format( "%02d",((Moteinfo)element).getSeq() )  ;
			}
		});

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(80) ;
		tvc.getColumn().setAlignment(SWT.CENTER);
		tvc.getColumn().setText("센서명");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				return element == null ? "" :((Moteinfo)element).getDescript()   ;
			}
		});

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(80) ;
		tvc.getColumn().setAlignment(SWT.CENTER);
		tvc.getColumn().setText("Chock");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				return element == null ? "" :((Moteinfo)element).getChocknm()   ;
			}
		});

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(140) ;
		tvc.getColumn().setAlignment(SWT.CENTER);
		tvc.getColumn().setText("장치위치");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				return element == null ? "" :((Moteinfo)element).getLocNmlong()   ;
			}
		});

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(80) ;
		tvc.getColumn().setAlignment(SWT.CENTER);
		tvc.getColumn().setText("온도(℃)");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				return element == null || ((Moteinfo)element).getRtd() == 0 ? "" : String.format( "%.2f",((Moteinfo)element).getRtd())   ;
			}
		});

		tvc = new TableViewerColumn(tvv, SWT.NONE);
		tvc.getColumn().setWidth(100) ;
		tvc.getColumn().setAlignment(SWT.LEFT);
		tvc.getColumn().setText("배터리(v)");

		tvc.setLabelProvider(new myColProvider() {
			@Override
			public String getText(Object element) {
				int batt = ((Moteinfo)element).getBatt() ;
				return element == null || batt == 0 ? "" : String.format( "%.3f", batt/1000.0 )  ;
			}
			@Override
			public Image getImage(Object element) {
				if (element == null)  return super.getImage(element);
				Moteinfo m = (Moteinfo)element ;
				if (  m.getBatt() == 0 ) return super.getImage(element) ;
				if (  m.getBatt() < AppMain.MOTECNF.getBatt() - 1000 ) 
					return AppMain.img_danger ;
				else if ( m.getBatt() < AppMain.MOTECNF.getBatt() )
					return AppMain.img_lowb ;
				else
					return AppMain.img_act ;
			}
			
		});

		tvc = new TableViewerColumn(tvv, SWT.LEFT);
		tvc.getColumn().setWidth(120) ;
		tvc.getColumn().setAlignment(SWT.LEFT);
		tvc.getColumn().setText("상태정보");
		tvc.setLabelProvider(new myColProvider() {
			@Override
			public Color getForeground(Object e) {
				Color col = SWTResourceManager.getColor(49, 134, 255) ;
				if (e == null) return col ;
				if (((Moteinfo)e).getAct() == 0) 
					col = AppMain.colinact2 ; 

				return col ;
			}
			@Override
			public String getText(Object element) {
				if (element == null)  return "" ;
				if (((Moteinfo)element).getAct() == 2) 
					return "Active" ; 
				else if (((Moteinfo)element).getAct() == 1) 
					return "Wait" ; 
				else 
					return "InActive" ;
			}
			@Override
			public Image getImage(Object element) {
				if (element == null)  return AppMain.img_discon ;
				Moteinfo mote = (Moteinfo)element ;
				if (mote.getAct() == 2) 
					if (mote.getStatus() == 2)
						return AppMain.img_danger ;
					else if (mote.getStatus() == 1)
						return AppMain.img_warn ;
					else
						return AppMain.img_act ;
				else 
					return AppMain.img_inact ;
				
			}
		});

//		tvc = new TableViewerColumn(tvv, SWT.NONE);
//		tvc.getColumn().setWidth(80) ;
//		tvc.getColumn().setAlignment(SWT.CENTER);
//		tvc.getColumn().setText("스위치");
//		tvc.setLabelProvider(new myColProvider() {
//			@Override
//			public String getText(Object element) {
//				return element == null ? "" : ((Moteinfo)element).getSwseq() > 0 ? ((Moteinfo)element).getSwseq() + "" : ""  ;
//			}
//		});

//		tvc = new TableViewerColumn(tv, SWT.NONE);
//		tvc.getColumn().setWidth(300) ;
//		tvc.getColumn().setAlignment(SWT.CENTER);
//		tvc.getColumn().setText("경고내용");
//		tvc.setLabelProvider(new myColProvider() {
//			@Override
//			public String getText(Object element) {
//				if (element == null)  return "" ;
//				Moteinfo m = (Moteinfo)element ;
//				StringBuffer strb = new StringBuffer();
//				if (  m.getBatt() > 0 && m.getBatt() < AppMain.MOTECNF.getBatt() - 1000 ) 
//					strb.append("배터리 방전" ); 
//				else if (m.getBatt() > 0 && m.getBatt() < AppMain.MOTECNF.getBatt() )
//					strb.append( "배터리 교체요") ;
//				if (m.getStatus() == 1) strb.append( " *경고온도") ;
//				else if (m.getStatus() == 2) strb.append( " *위험온도") ;
//				
//				return strb.toString() ;
//			}
//		});
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(tbl);
	}


//	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
		Cursor cursor = AppMain.cur_comp.getCursor() ;
		AppMain.cur_comp.setCursor(AppMain.busyc);
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		time_c = AppMain.appmain.getLasTime(1) ;

		List<Moteinfo> moteinfo2 = em.createNativeQuery("SELECT m.pkey, m.mmgb, m.seq, m.cntgb, m.bno, m.stand, m.loc,m.tb, m.descript ,m.rtd1, m.batt, m.act,m.temp_d,temp_w ,chocknm from vMoteinfo m, LasTime l"
				+ " where m.mmgb = '2' and m.tm = l.lastm  UNION "
				+ "SELECT m.pkey , m.mmgb, m.seq, 0, m.bno, m.stand, m.loc,m.tb,descript, cast(0 as float), m.batt, m.act,temp_d,temp_w, '' from motestatus m WHERE mmgb = '2' and m.gubun = 'R' AND m.spare = 'N' "
				+ " order by seq "
				, Moteinfo.class)
				.getResultList() ;

		tv2.setInput(moteinfo2);
		tv2.refresh();

		List<Moteinfo> moteinfo = em.createNativeQuery("SELECT m.pkey,  m.mmgb, m.seq, m.cntgb, m.bno, m.stand, m.loc,m.tb, m.descript ,m.rtd1, m.batt, m.act,temp_d,temp_w,chocknm from vMoteinfo m, LasTime l"
				+ " where m.mmgb = '1' and m.tm = l.lastm  UNION  "
				+ "SELECT m.pkey + 888,  m.mmgb, m.seq,m.cntgb,m.bno, m.stand, m.loc,m.tb, m.descript ,m.rtd2, m.batt, m.act,temp_d,temp_w,chocknm from vMoteinfo m, LasTime l"
				+ " where m.mmgb = '1' and m.tm = l.lastm and m.cntgb = 1 UNION  "
				+ "SELECT m.pkey + 999,  m.mmgb, m.seq,m.cntgb,m.bno, m.stand, m.loc,m.tb, m.descript ,m.rtd3, m.batt, m.act,temp_d,temp_w,chocknm from vMoteinfo m, LasTime l"
				+ " where m.mmgb = '1' and m.tm = l.lastm and m.cntgb = 1 and m.seq % 2 = 1 UNION "
				+ "SELECT m.pkey , m.mmgb, m.seq,0, m.bno, m.stand, m.loc,m.tb,descript, cast(0 as float), m.batt, m.act,temp_d,temp_w,'' from motestatus m WHERE mmgb = '1' and m.gubun = 'R' AND m.spare = 'N' "
				+ " order by seq , pkey"
				, Moteinfo.class)
				.getResultList() ;

		tv.setInput(moteinfo);
		tv.refresh();

		AppMain.cur_comp.setCursor(cursor);
		
	}

	private class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object input) {
			//return new Object[0];
			return ((List<Moteinfo>)input).toArray();
		}
		@Override
		public void dispose() {
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class myColProvider extends ColumnLabelProvider {
		@Override
		public Color getForeground(Object e) {
			Color col = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE) ;
			if (e == null) return col ;
			if ( ((Moteinfo)e).getStatus() > 0 ) col = AppMain.colout ;
			
			else if ( ((Moteinfo)e).getBatt() > 0 && ((Moteinfo)e).getBatt() < AppMain.MOTECNF.getBatt() 
					   && ((Moteinfo)e).getAct() == 2 ) col = AppMain.collow ;
			return col ;
		}
	}

}