package gy.posco.part;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import gy.posco.model.Moteinfo;
import gy.posco.model.Motestatus;

public class DashBoard {


	Image img_1 = AppMain.getMyimage("dashboard_1.png");
	Image img_c = AppMain.getMyimage("category.png");
	Image img_c1 = AppMain.getMyimage("categoryicon_1.png");

    Label lblApActive;
    Label lblApInactive;
    Label lblTagActive;
    Label lblTagInactive;
    Label lblAlertActive;
    Label lblAlertInactive;
    
//    DateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");
    Font font1 = SWTResourceManager.getFont("HY견고딕", 24 , SWT.BOLD ) ;
    Font font2 = SWTResourceManager.getFont("HY견고딕", 14, SWT.NORMAL);
    Font font12 = SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD ) ;
    Font fontT = SWTResourceManager.getFont("Tahoma", 20, SWT.NORMAL  ) ;
    Thread dsThread ;
    Dashb dashb1, dashb2 ;
	List<Motestatus > motelist ;

	int activeCnt = 0;
	int inactiveCnt = 0;

	int activeSsCnt = 0;
	int failCnt = 0;
	int moteLBCnt = 0;
	int oBCnt = 0;

	private Date time_c = null;
    EntityManager em = AppMain.emf.createEntityManager();

	public DashBoard(Composite parent, int style) {

//		super(parent, style) ;
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
		
		Composite composite_l = new Composite(composite, SWT.NONE);
		composite_l.setLayout(new GridLayout(3,false) );
		Composite composite_l1 = new Composite(composite_l, SWT.NONE);
		composite_l1.setLayoutData(new GridData(1200, 100));
		
		composite_l1.setBackgroundImage(img_1);
		Label lblT = new Label(composite_l1,SWT.NONE) ;
		lblT.setText("스탠드별 상태");
		lblT.setFont(fontT);
		lblT.setBounds(80, 5, 200, 40);

		int iy = 25 ;
		lblApActive = new Label(composite_l1, SWT.NONE);
		lblApActive.setAlignment(SWT.RIGHT);
		lblApActive.setFont(font2);
		lblApActive.setBounds(340, iy, 40, 20);
		lblApActive.setText("99");
		
		lblApInactive = new Label(composite_l1, SWT.NONE);
		lblApInactive.setAlignment(SWT.RIGHT);
		lblApInactive.setFont(font2);
		lblApInactive.setBounds(340, iy+25 , 40, 20);
		lblApInactive.setText("99");
		
		lblTagActive = new Label(composite_l1, SWT.NONE);
		lblTagActive.setAlignment(SWT.RIGHT);
		lblTagActive.setFont(font2);
		lblTagActive.setBounds(550, iy, 40, 20);
		lblTagActive.setText("99");
		
		lblTagInactive = new Label(composite_l1, SWT.NONE);
		lblTagInactive.setAlignment(SWT.RIGHT);
		lblTagInactive.setFont(font2);
		lblTagInactive.setBounds(550, iy+25, 40, 20);
		lblTagInactive.setText(" 0");
		
		lblAlertActive = new Label(composite_l1, SWT.NONE);
		lblAlertActive.setAlignment(SWT.LEFT);
		lblAlertActive.setFont(font2);
		lblAlertActive.setBounds(750, iy, 100, 20);
		lblAlertActive.setText(" 0      ");
		lblAlertActive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblAlertActive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
//		lblAlertActive.setCursor(AppMain.handc);

		lblAlertInactive = new Label(composite_l1, SWT.NONE);
		lblAlertInactive.setAlignment(SWT.LEFT);
		lblAlertInactive.setFont(font2);
		lblAlertInactive.setBounds(750, iy+25, 100, 20);
		lblAlertInactive.setText(" 0      ");
		lblAlertInactive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblAlertInactive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
//		lblAlertInactive.setCursor(AppMain.handc);
		
		Label btnlow = new Label(composite_l1, 0) ;
		
		btnlow.setBounds(880, iy+20, 50,60);
		btnlow.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
//		btnlow.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));

		Label btnob = new Label(composite_l1, 0) ;
		btnob.setBounds(960, iy+20, 50,60);
//		btnob.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));
		Label bsetup = new Label(composite_l1, 0) ;
		bsetup.setBounds(1040, iy+20, 50,60);
		Label bswitch = new Label(composite_l1, 0) ;
		bswitch.setBounds(1120, iy+20, 50,60);
		
		btnlow.setCursor(AppMain.handc);
		btnob.setCursor(AppMain.handc);
		bsetup.setCursor(AppMain.handc);
		bswitch.setCursor(AppMain.handc);

		btnlow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 1 ) ;
				valert.open() ;
			}
		});

		btnob.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 2 ) ;
				valert.open() ;
			}
			
		});
		bsetup.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				RegStand2 newmote = new RegStand2(parent.getShell() ) ;

				if (newmote.open() == Window.OK) {
					AppMain.appmain.reloaddata();
					refreshSensorList();
				}
			}
			
		});

		Composite composite_2 = new Composite(composite, SWT.BORDER);
		GridLayoutFactory.fillDefaults().margins(15, -1).spacing(20, 20).applyTo(composite_2);
		composite_2.setLayoutData(new GridData(GridData.FILL_BOTH));
		dashb1 = new Dashb(composite_2,"1") ;
		dashb2 = new Dashb(composite_2,"2") ;
		
		refreshSensorList();
		AppMain.appmain.callf = new IcallFunc() {
			
			@Override
			public void callFunc() {
				refreshSensorList();
			}

			@Override
			public void finalFunc() {
				// TODO Auto-generated method stub
//				dsThread.interrupt();
			}
		};
		
//        Runnable runnable = new Runnable() {
//
//            @Override
//            public void run() {
//                System.out.println("*** jj ***" + clarr.size());
//            }
//        };
//        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//        service.scheduleAtFixedRate(runnable, 1, 2, TimeUnit.SECONDS);

//		dsThread = new MyThread(Display.getCurrent() );
//		dsThread.start();

	}
	
	private class Dashb {
		String[] sname = {"BUR ","IMR ","WR ","비접촉 "} ;
		CLabel[] cltit = new CLabel[5];
		CLabel[] clvtit = new CLabel[4];
		CLabel[][] clt = new CLabel[5][10] ;
		CLabel[][] clb = new CLabel[5][10] ;
		String sv_mmgb = "1" ;
		public void queryData() {

			List<Moteinfo> moteinfo 
			= (List<Moteinfo>) em.createQuery("select m from Moteinfo m where m.mmgb = ?1 and m.tm = ?2 ", Moteinfo.class)
				.setParameter(1, sv_mmgb)
				.setParameter(2, time_c) 
				.getResultList() ;
			List<Moteinfo> motemax 
			= (List<Moteinfo>) em.createNativeQuery("select m.pkey, m.mmgb,m.seq, m.cntgb, m.stand, m.loc, m.tb, m.act"
					+ ", temp_d, temp_w, max(m.rtd1) rtd1, max(m.rtd2) rtd2, max(m.rtd3) rtd3, max(m.temp) temp "
					+ " from vMoteinfo m where "
					+ "m.mmgb = ?1 and m.tm between (?2 - interval 1 hour) and ?3  group by mmgb, seq ", Moteinfo.class)
				.setParameter(1, sv_mmgb)
				.setParameter(2, time_c) 
				.setParameter(3, time_c) 
				.getResultList() ;
			for(Moteinfo mote: moteinfo) {
				int i,j ;
				Color cl = mote.getStatus() == 2 ? SWTResourceManager.getColor(SWT.COLOR_RED)
							: SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
				if (mote.getCntgb() == 1) 
					i = 3;
				else
					switch (mote.getLoc()) {
					case "B":
						i = 0 ;
						break;
					case "I":
						i = 1 ;
						break;
					case "W":
						i = 2 ;
						break;
					default:
						i = 3;
						break;
					}
				j = (mote.getStand() - 1) * 2 ;

				if (mote.getCntgb() == 1) {
					clt[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clt[i][j].setForeground(cl) ;
					clb[i][j].setText(String.format("%.0f", mote.getRtd2()));
					if ( mote.getMmgb().equals("1")) clt[4][j].setText(String.format("%.0f", mote.getRtd3()));
				} else if (mote.getTb().equals("T") ) {
					clt[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clt[i][j].setForeground(cl) ;
				} else {
					clb[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clb[i][j].setForeground(cl) ;
				}

				if (mote.getAct() == 0) {
					clvtit[i].setImage(AppMain.appmain.img_inact);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_inact);
				} else if (mote.getStatus() == 1) {
					clvtit[i].setImage(AppMain.appmain.img_warn);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_warn);
				} else if (mote.getStatus() == 2){
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_danger);
					clvtit[i].setImage(AppMain.appmain.img_danger);
				} else if (mote.getBatt() < AppMain.MOTECNF.getBatt()) {
					clvtit[i].setImage(AppMain.appmain.img_lowb);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_lowb);
				} else {
					clvtit[i].setImage(AppMain.appmain.img_act);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_act);
					
				}
				
			}
			for(Moteinfo mote: motemax) {
				int i,j ;
				Color cl = mote.getStatus() == 2 ? SWTResourceManager.getColor(SWT.COLOR_RED)
							: SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
				if (mote.getCntgb() == 1) 
					i = 3;
				else
					switch (mote.getLoc()) {
					case "B":
						i = 0 ;
						break;
					case "I":
						i = 1 ;
						break;
					case "W":
						i = 2 ;
						break;
					default:
						i = 3;
						break;
					}
				j = (mote.getStand() - 1) * 2 + 1 ;

				if (mote.getCntgb() == 1) {
					clt[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clt[i][j].setForeground(cl) ;
					clb[i][j].setText(String.format("%.0f", mote.getRtd2()));
					if ( mote.getMmgb().equals("1")) clt[4][j].setText(String.format("%.0f", mote.getRtd3()));
				} else if (mote.getTb().equals("T") ) {
					clt[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clt[i][j].setForeground(cl) ;
				} else {
					clb[i][j].setText(String.format("%.0f", mote.getRtd1()));
					clb[i][j].setForeground(cl) ;
				}

				if (mote.getAct() == 0) {
					clvtit[i].setImage(AppMain.appmain.img_inact);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_inact);
				} else if (mote.getStatus() == 1) {
					clvtit[i].setImage(AppMain.appmain.img_warn);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_warn);
				} else if (mote.getStatus() == 2){
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_danger);
					clvtit[i].setImage(AppMain.appmain.img_danger);
				} else if (mote.getBatt() < AppMain.MOTECNF.getBatt()) {
					clvtit[i].setImage(AppMain.appmain.img_lowb);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_lowb);
				} else {
					clvtit[i].setImage(AppMain.appmain.img_act);
					cltit[mote.getStand() -1].setImage(AppMain.appmain.img_act);
					
				}
				
			}
		}
		public Dashb(Composite comp, String mmgb) {
			Composite comp_s = new Composite(comp, SWT.NONE) ;
			GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).spacing(5, 8).margins(25, -1).applyTo(comp_s);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(comp_s);
			sv_mmgb = mmgb ;
			clvtit[0] = new CLabel(comp_s, SWT.BOTTOM);
			clvtit[0].setFont(font1);
			clvtit[0].setTopMargin(15);
			if (mmgb.equals("1")) {
				clvtit[0].setText("W/S");
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).span(1, 2).applyTo(clvtit[0]) ;
				for (int i=0;i<5;i++) {
					cltit[i] = new CLabel(comp_s, SWT.RIGHT_TO_LEFT|SWT.CENTER  );
					cltit[i].setText(String.format("#%d", i+1)) ;
					cltit[i].setImage(AppMain.appmain.img_act ) ;
					cltit[i].setFont(fontT);
					cltit[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE)) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).applyTo(cltit[i]) ;
				}
				Label lbl ;
				for (int i=0;i<5;i++) {
					lbl = new Label(comp_s, SWT.LEFT  );
					lbl.setText("     실시간        Max/hour ") ;
					lbl.setFont(font12);
					lbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY)) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BOTTOM).hint(250,-1).applyTo(lbl) ;
				}
			} else {
				clvtit[0].setText("D/S");
				GridDataFactory.fillDefaults().span(6, 1).align(SWT.FILL, SWT.BOTTOM).applyTo(clvtit[0]) ;
			}
			
			for (int i=0; i< 4 ; i++) {
				clvtit[i] = new CLabel(comp_s, SWT.RIGHT_TO_LEFT  ) ;
				clvtit[i].setFont(fontT);
				clvtit[i].setImage(AppMain.appmain.img_act ) ;
				clvtit[i].setText(sname[i]) ;
				clvtit[i].setBackground(SWTResourceManager.getColor(227, 235, 209) ) ;
				clvtit[i].setRightMargin(30);
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(180, -1).applyTo(clvtit[i]) ;
	
				
				for (int j=0; j<5; j++) {
					Composite compl = new Composite(comp_s, SWT.NONE);
					GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(compl);
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(250, -1).applyTo(compl) ;
					int k = j*2  ;
					clt[i][k] = new CLabel(compl, SWT.NONE | SWT.CENTER) ;
					clt[i][k].setText("99");
					clt[i][k].setFont(font2);
					clt[i][k].setBackground(SWTResourceManager.getColor(220, 230, 242) ) ;
					clt[i][k].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE)) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clt[i][k]) ;
					clt[i][k+1] = new CLabel(compl, SWT.NONE| SWT.CENTER) ;
					clt[i][k+1].setText("99");
					clt[i][k+1].setFont(font2);
					clt[i][k+1].setBackground(SWTResourceManager.getColor(220, 230, 242) ) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clt[i][k+1]) ;

					clb[i][k] = new CLabel(compl, SWT.NONE| SWT.CENTER) ;
					clb[i][k].setText("99");
					clb[i][k].setFont(font2);
					clb[i][k].setBackground(SWTResourceManager.getColor(242, 242, 242) ) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clb[i][k]) ;
					clb[i][k+1] = new CLabel(compl, SWT.NONE| SWT.CENTER) ;
					clb[i][k+1].setText("99");
					clb[i][k+1].setFont(font2);
					clb[i][k+1].setBackground(SWTResourceManager.getColor(242, 242, 242) ) ;
					GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clb[i][k+1]) ;
					if (mmgb.equals("1") && i == 3) {
						clt[4][k] = new CLabel(compl, SWT.NONE | SWT.CENTER) ;
						clt[4][k].setText("99");
						clt[4][k].setFont(font2);
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clt[4][k]) ;
						clt[4][k].setBackground(SWTResourceManager.getColor(220, 230, 242) ) ;
						clt[4][k+1] = new CLabel(compl, SWT.NONE| SWT.CENTER) ;
						clt[4][k+1].setText("99");
						clt[4][k+1].setFont(font2);
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(100, 30).applyTo(clt[4][k+1]) ;
						clt[4][k+1].setBackground(SWTResourceManager.getColor(220, 230, 242) ) ;
					}
				}
			}
			
		}
		
	}

	private class MyThread extends Thread {
		private Display display = null;
		MyThread(Display display ){
			this.display = display ;
		}
		private boolean vb = false ;
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted() && !display.isDisposed()) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
		                try {
							Thread.currentThread();
							Thread.sleep(900);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                vb = !vb ;
		                
					}
				});
				

			}
		}

	}

    final DateFormat ymd = new SimpleDateFormat("yyyyMMddHHmmss");

//	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
	    
//		Cursor cursor = AppMain.cur_comp.getCursor() ;
//		AppMain.cur_comp.setCursor(AppMain.busyc);
		time_c = AppMain.appmain.time_c ;
		
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		motelist = em.createQuery("select m from Motestatus m where m.spare = 'N'", Motestatus.class).getResultList() ;

		activeCnt = 0;
		inactiveCnt = 0;
		activeSsCnt = 0;
		failCnt = 0;
		moteLBCnt = 0;
		oBCnt = 0;

		activeCnt = (int) motelist.stream().filter(m -> m.getAct() == 2).count() ;
		inactiveCnt = (int) motelist.stream().filter(m -> m.getAct() != 2).count() ;
		activeSsCnt = (int) motelist.stream().filter(m -> m.getAct() == 2 && m.getGubun().equals("S") ).count() ;
		failCnt = (int) motelist.stream().filter(m -> m.getAct() != 2 && m.getGubun().equals("S") ).count() ;
		moteLBCnt = (int) motelist.stream().filter( m -> m.getBatt() > 0 && m.getBatt() < AppMain.MOTECNF.getBatt() ).count() ;
		oBCnt = (int) motelist.stream().filter(m -> m.getStatus() > 0 ).count() ;
		
		lblApActive.setText(activeCnt+"");
		lblApInactive.setText(inactiveCnt+"");

		lblTagActive.setText(activeSsCnt+"");
		lblTagInactive.setText(failCnt+"");
		lblAlertActive.setText(String.format  ("%2d  배터리", moteLBCnt));
		lblAlertInactive.setText(String.format("%2d   온도", oBCnt) );
		lblAlertActive.requestLayout();
		lblAlertInactive.requestLayout();

		dashb1.queryData();
		dashb2.queryData();

	}
	

}