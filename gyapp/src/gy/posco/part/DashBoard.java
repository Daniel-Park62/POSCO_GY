package gy.posco.part;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

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
	Image img_l1 = AppMain.getMyimage("rollnm.png");

    Label lblApActive;
    Label lblApInactive;
    Label lblTagActive;
    Label lblTagInactive;
    Label lblAlertActive;
    Label lblAlertInactive , lblAlertWarn;
    
//    DateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");
    Font font1 = SWTResourceManager.getFont("HY견고딕", 24 , SWT.BOLD ) ;
    Font font2 = SWTResourceManager.getFont("HY견고딕", 14, SWT.NORMAL);
    Font font12 = SWTResourceManager.getFont("맑은 고딕", 11, SWT.NORMAL ) ;
    Font font12B = SWTResourceManager.getFont("맑은 고딕", 12, SWT.BOLD ) ;
    Font fontT = SWTResourceManager.getFont("맑은 고딕", 18, SWT.BOLD  ) ;
    Thread dsThread ;

    Standcl[] standcl = new Standcl[5];
	List<Motestatus > motelist ;

	private Date time_c = null;
    EntityManager em = AppMain.emf.createEntityManager();

    private void label_cre1(Composite comp, int x, int y, String s) {
		Label lblT = new Label(comp, SWT.NONE);
		lblT.setAlignment(SWT.RIGHT);
		lblT.setFont(SWTResourceManager.getFont("맑은 고딕", 11, SWT.BOLD ));
		lblT.setBounds(x, y, 50, 20);
		lblT.setText(s);
		lblT.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
    }
	public DashBoard(Composite parent, int style) {

//		super(parent, style) ;
		
		Composite composite = new Composite(parent, SWT.NONE);
		
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
		composite_l1.setLayoutData(new GridData(1600, 100));
		
		composite_l1.setBackgroundImage(img_1);
		Label lblT = new Label(composite_l1,SWT.NONE) ;
		lblT.setText("스탠드별 상태");
		lblT.setFont(SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD ));
		lblT.setBounds(80, 5, 200, 40);

		int iy = 25 ;
		lblApActive = new Label(composite_l1, SWT.NONE);
		lblApActive.setAlignment(SWT.RIGHT);
		lblApActive.setFont(font2);
		lblApActive.setBounds(335, iy, 40, 20);
		lblApActive.setText("99");
		label_cre1(composite_l1, 380,iy, "정상") ;
		
		lblApInactive = new Label(composite_l1, SWT.NONE);
		lblApInactive.setAlignment(SWT.RIGHT);
		lblApInactive.setFont(font2);
		lblApInactive.setBounds(335, iy+25 , 40, 20);
		lblApInactive.setText("99");
		label_cre1(composite_l1, 380,iy+23, "비활성") ;

		lblTagActive = new Label(composite_l1, SWT.NONE);
		lblTagActive.setAlignment(SWT.RIGHT);
		lblTagActive.setFont(font2);
		lblTagActive.setBounds(540, iy, 40, 20);
		lblTagActive.setText("99");
		label_cre1(composite_l1, 585,iy, "정상") ;
		
		lblTagInactive = new Label(composite_l1, SWT.NONE);
		lblTagInactive.setAlignment(SWT.RIGHT);
		lblTagInactive.setFont(font2);
		lblTagInactive.setBounds(540, iy+25, 40, 20);
		lblTagInactive.setText(" 0");
		label_cre1(composite_l1, 585,iy+23, "비정상") ;

		lblAlertWarn = new Label(composite_l1, SWT.NONE);
		lblAlertWarn.setAlignment(SWT.LEFT);
		lblAlertWarn.setFont(font12B);
		lblAlertWarn.setBounds(750, iy-5, 150, 20);
		lblAlertWarn.setText(" 0      ");
		lblAlertWarn.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblAlertWarn.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
//		lblAlertActive.setCursor(AppMain.handc);

		lblAlertInactive = new Label(composite_l1, SWT.NONE);
		lblAlertInactive.setAlignment(SWT.LEFT);
		lblAlertInactive.setFont(font12B);
		lblAlertInactive.setBounds(750, iy+25, 150, 20);
		lblAlertInactive.setText(" 0      ");
		lblAlertInactive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblAlertInactive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
//		lblAlertInactive.setCursor(AppMain.handc);

		lblAlertActive = new Label(composite_l1, SWT.NONE);
		lblAlertActive.setAlignment(SWT.LEFT);
		lblAlertActive.setFont(font12B);
		lblAlertActive.setBounds(960, iy+10, 160, 20);
		lblAlertActive.setText("        ");
		lblAlertActive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblAlertActive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));

		Label btnlow = new Label(composite_l1, 0) ;
		
		btnlow.setBounds(1256, iy, 50,60);
		btnlow.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
//		btnlow.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));

		Label btnob = new Label(composite_l1, 0) ;
		btnob.setBounds(1350, iy, 50,60);
//		btnob.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));
		Label bsetup = new Label(composite_l1, 0) ;
		bsetup.setBounds(1430, iy, 50,60);
//		Label bswitch = new Label(composite_l1, 0) ;
//		bswitch.setBounds(1430, iy+20, 50,60);
		
		btnlow.setCursor(AppMain.handc);
		btnob.setCursor(AppMain.handc);
		bsetup.setCursor(AppMain.handc);
//		bswitch.setCursor(AppMain.handc);

		btnlow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 0 ) ;
				valert.open() ;
			}
		});

		btnob.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 1 ) ;
				valert.open() ;
			}
			
		});
//		bswitch.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
//				ViewAlert valert = new ViewAlert(parent.getShell(), 2 ) ;
//				valert.open() ;
//			}
//			
//		});

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

		Composite composite2 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(10, 10).equalWidth(false).spacing(0, 0).numColumns(2).applyTo(composite2);
		GridDataFactory.fillDefaults().applyTo(composite2) ;

		Composite composite21 = new Composite(composite2, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 165).numColumns(1).equalWidth(false).applyTo(composite21);

		Label lnm = new Label(composite21,SWT.NONE) ;
		lnm.setImage(img_l1);
		lnm.pack();
		
		Composite composite_2 = new Composite(composite2, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 5).equalWidth(true).spacing(35, 20).numColumns(5).applyTo(composite_2);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(composite_2) ;
//		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		
		for (int i=1;i<6;i++) {
			Composite composite_3 = new Composite(composite_2, SWT.SHADOW_IN);
			GridLayoutFactory.fillDefaults().applyTo(composite_3);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(composite_3) ;
			standcl[i-1] = new Standcl(composite_3, i) ;
		}
		
		refreshSensorList();
		AppMain.appmain.callf = new IcallFunc() {
			
			@Override
			public void callFunc() {
				refreshSensorList();
			}

			@Override
			public void finalFunc() {
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
	
	private class Standcl {
		Image img_r = AppMain.getMyimage("Roller_W.png");

		private int stand ;
		private CLabel[][] cltemp = new CLabel[2][6] ;
		CLabel[] clt = new CLabel[2] ;
		CLabel[] clb = new CLabel[2] ;
		CLabel[] clunc = new CLabel[5] ;
		CLabel[] clrw = new CLabel[2] ;

		CLabel ctinfo, cninfo ;
		String[] lname = {"TB","TI","TW","BW","BI","BB"} ;
		Composite comp_s3 ;
		public Standcl(Composite comp, int stand) {
			this.stand = stand ;
			CLabel clbl = new CLabel(comp, SWT.CENTER) ;
			clbl.setText("        STAND  " + stand);
			clbl.setFont(fontT);
			GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(clbl);
			clbl = new CLabel(comp, SWT.CENTER) ;
			clbl.setText("     W/S(℃)                  D/S(℃)");
			clbl.setFont(font12);
			GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(clbl);
			Composite comp_s = new Composite(comp, SWT.NONE) ;
			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(85, 25).margins(18, 35).applyTo(comp_s);
			GridDataFactory.fillDefaults().hint(250, 500).align(SWT.FILL, SWT.FILL).applyTo(comp_s);
			comp_s.setBackgroundImage(img_r);
			clt[0] = new CLabel(comp_s, SWT.CENTER );
			clt[1] = new CLabel(comp_s, SWT.CENTER );
			clt[0].setFont(font12B);
			clt[1].setFont(font12B);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(65, 30).applyTo(clt[0]);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(65, 30).applyTo(clt[1]);
			clt[0].setBackground(SWTResourceManager.getColor( 255,248,206 )) ;
			clt[0].setText("CBTW01");
			clt[1].setText("CBTD01");
			clt[1].setBackground(clt[0].getBackground()) ;
			for(int i=0 ; i<6; i++)
				for (int j=0;j<2; j++) {
					cltemp[j][i] = new CLabel(comp_s, SWT.BORDER | SWT.CENTER );
					cltemp[j][i].setText(" 0.0");
					cltemp[j][i].setFont(font2);
					cltemp[j][i].setBackground(AppMain.colstr) ;
					GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(60, 30).applyTo(cltemp[j][i]);
				}
			clb[0] = new CLabel(comp_s, SWT.CENTER );
			clb[1] = new CLabel(comp_s, SWT.CENTER );
			clb[0].setFont(font12B);
			clb[1].setFont(font12B);
			clb[0].setBackground(clt[0].getBackground()) ;
			clb[1].setBackground(clt[0].getBackground()) ;
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(65, 30).applyTo(clb[0]);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(65, 30).applyTo(clb[1]);
			clb[0].setText("CBBW01");
			clb[1].setText("CBBD01");
			Composite comp_s2 = new Composite(comp, SWT.NONE) ;
			GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(true).spacing(20, 5).margins(35, -1).applyTo(comp_s2);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(comp_s2);
			for(int i=0;i<5;i++) {
				clunc[i] = new CLabel(comp_s2, SWT.BORDER | SWT.CENTER );
				clunc[i].setText(" 0.0");
				clunc[i].setFont(font12);
				clunc[i].setBackground(AppMain.colstr) ;
				GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(45, -1).applyTo(clunc[i]);
			}
			comp_s2.requestLayout();
			comp_s3 = new Composite(comp, SWT.NONE) ;
			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(30, -1).margins(35, 10).applyTo(comp_s3);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(comp_s3);

//			if (stand == 1) {
//				Label lbl = new Label(comp_s3, SWT.NONE) ;
//				lbl.setText("중계기");
//				lbl.setFont(font12);
//			}
			for(int i=0;i<2;i++) {
				clrw[i] = new CLabel(comp_s3,  SWT.CENTER );
				clrw[i].setText("");
				clrw[i].setFont(SWTResourceManager.getFont("Calibri", 8, SWT.NORMAL));
				clrw[i].setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT) ) ;
				clrw[i].setBackground(AppMain.img_inact ) ;
				clrw[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(25, 25).applyTo(clrw[i]);
			}

			comp_s3.layout();
			
			ctinfo = new CLabel(comp, SWT.NONE) ;
			cninfo = new CLabel(comp, SWT.NONE) ;
			
			ctinfo.setText("");
			cninfo.setText("");
			ctinfo.setFont(font12);
			cninfo.setFont(font12);
			ctinfo.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED)) ;
			cninfo.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN)) ;
			GridLayoutFactory.fillDefaults().margins(40,-1).applyTo(ctinfo);
			GridLayoutFactory.fillDefaults().margins(40,0).applyTo(cninfo);
//			queryData() ;
			
		}
		public int queryData() {

			List<String> stemp = em.createNativeQuery("SELECT warn_desc FROM twarnhist WHERE gbn = 1 and stand = ?1  LIMIT 1")
					.setParameter(1, this.stand)
					.getResultList() ;
				ctinfo.setText(stemp.size() == 0 ? "" : stemp.get(0) );

			List<String> scomm = em.createNativeQuery("SELECT warn_desc FROM twarnhist WHERE gbn = 2 and stand = ?1  LIMIT 1")
						.setParameter(1, this.stand)
						.getResultList() ;
				cninfo.setText(scomm.size() == 0 ? "" : scomm.get(0) );
			clt[0].setText("-");
			clt[1].setText("-");
			clb[0].setText("-");
			clb[1].setText("-");
			for(int i=0 ; i<6; i++)
				for (int j=0;j<2; j++) {
					cltemp[j][i].setText(" 0.0");
					cltemp[j][i].setBackground(AppMain.colstr) ;
				}
			for(int i=0;i<5;i++) {
				clunc[i].setText(" 0.0");
				clunc[i].setBackground(AppMain.colstr) ;
			}

			moteinfo.stream().filter(h -> h.getStand() == stand ).forEach( mote -> {
				int i,j ;

				i = Integer.parseInt( mote.getMmgb()) - 1;
				if (mote.getCntgb() == 0 ) {
					j = Arrays.asList(lname).indexOf(mote.getTb() + mote.getLoc()) ;

					if (i > 1 || j > 5 || i < 0 || j < 0) return ;
					cltemp[i][j].setText(String.format("%.1f", mote.getRtd1()));
					label_col_set(cltemp[i][j], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd1() == 0 ? 1 
							: mote.getStatus() == 0 && mote.getBatt() < AppMain.MOTECNF.getBatt() ? -2 : mote.getStatus());
					if (j == 0 ) clt[i].setText( mote.getChocknm() ); 
					if (j == 5 ) clb[i].setText( mote.getChocknm() );  
				} else {
					if (mote.getSeq() % 2 == 1) {
						clunc[0].setText(String.format("%.1f", mote.getRtd1()));
						clunc[1].setText(String.format("%.1f", mote.getRtd2()));
						clunc[2].setText(String.format("%.1f", mote.getRtd3()));
						label_col_set(clunc[0], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd1() == 0 ? 1 : mote.getStatus());
						label_col_set(clunc[1], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd2() == 0 ? 1 :mote.getStatus2());
						label_col_set(clunc[2], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd3() == 0 ? 1 :mote.getStatus3());
						
					} else {
						clunc[3].setText(String.format("%.1f", mote.getRtd1()));
						clunc[4].setText(String.format("%.1f", mote.getRtd2()));
						label_col_set(clunc[3], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd1() == 0 ? 1 : mote.getStatus());
						label_col_set(clunc[4], mote.getAct() == 0 ? -1 : mote.getAct() == 2 && mote.getRtd2() == 0 ? 1 : mote.getStatus2());
					}
				}
		});

//			List<Motestatus> motes 
//			= (List<Motestatus>) em.createQuery("select m from Motestatus m where m.stand = ?1  and m.gubun = 'R' and m.spare = 'N' "
//					, Motestatus.class)
//				.setParameter(1, stand)
//				.getResultList() ;
			
			motest.stream().filter(m -> m.getStand() == this.stand).forEach( ms -> {
				int i = Integer.parseInt(ms.getMmgb()) - 1;
				if (ms.getStatus() == 2){
					clrw[i].setBackground(AppMain.img_danger);
					clrw[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else if (ms.getStatus() == 1) {
					clrw[i].setBackground(AppMain.img_warn);
				} else if (ms.getBatt() > 0 && ms.getBatt() < AppMain.MOTECNF.getBatt()) {
					clrw[i].setBackground(AppMain.img_lowb);
				} else if (ms.getAct() == 0 ) {
					clrw[i].setBackground(AppMain.img_inact);
				} else {
					clrw[i].setBackground(AppMain.img_act);
				}
				clrw[i].setText(ms.getSeq()+"");
			});
			return 1;
//			comp_s3.requestLayout();
		}
		
		void label_col_set(CLabel cbl, int sts){
			Color cl = sts == 2 ? AppMain.colout
					: sts == 1 ? AppMain.colwarn  
					: sts == -1 ? AppMain.colinact 
					: sts == -2 ? AppMain.collow 
					: AppMain.colact ;
			Color fcl = sts == 2 ? SWTResourceManager.getColor(SWT.COLOR_YELLOW)
					: SWTResourceManager.getColor(SWT.COLOR_BLACK);
			
			cbl.setBackground(cl);
			cbl.setForeground(fcl);
			
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
    List<Moteinfo> moteinfo ;
    List<Motestatus> motest ;
//	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
	    
//		Cursor cursor = AppMain.cur_comp.getCursor() ;
//		AppMain.cur_comp.setCursor(AppMain.busyc);
		time_c = AppMain.appmain.time_c ;
		
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		
		moteinfo 
		= (List<Moteinfo>) em.createQuery("select m from Moteinfo m where m.tm = ?1 and m.stand between 1 and 5 ", Moteinfo.class)
			.setParameter(1, time_c) 
			.getResultList() ;

		motest 
		= (List<Motestatus>) em.createQuery("select m from Motestatus m where m.stand between 1 and 5 and m.gubun = 'R' and m.spare = 'N' "
				, Motestatus.class)
			.getResultList() ;

		
		for(int i=0;i<5;i++) {
			standcl[i].queryData();
		}

		motelist = em.createQuery("select m from Motestatus m where m.spare = 'N'", Motestatus.class).getResultList() ;

		int activeCnt = 0;
		int inactiveCnt = 0;

		int activeSsCnt = 0;
		int failCnt = 0;
		int moteLBCnt = 0;
		int oBCnt = 0;

		int wCnt = 0 ;

		for(Motestatus m : motelist) {
			activeCnt += m.getAct() == 2 ? 1:0;
			inactiveCnt += m.getAct() != 2 ? 1:0;
			activeSsCnt += m.getAct() == 2 && m.getGubun().equals("S") ? 1:0;
			moteLBCnt += m.getCntgb() == 0 && m.getBatt() > 0 && m.getBatt() < AppMain.MOTECNF.getBatt() ? 1:0;
		}
		for(Moteinfo m: moteinfo ) {
			oBCnt += m.getStatus() > 1 ? 1 :0;
			oBCnt += m.getCntgb() == 1 && m.getStatus2() > 1 ? 1 :0;
			oBCnt += m.getCntgb() == 1 && m.getMmgb().equals("1") && m.getStatus3() > 1 ? 1 :0;
			wCnt += m.getStatus() == 1 ? 1 :0;
			wCnt += m.getCntgb() == 1 && m.getStatus2() == 1 ? 1 :0;
			wCnt += m.getCntgb() == 1 && m.getSeq() % 2 == 1 && m.getStatus3() == 1 ? 1 :0;
			failCnt +=  m.getAct() == 2 && m.getRtd1() == 0 ? 1 : 0; 
			failCnt +=  m.getCntgb() == 1 && m.getAct() == 2 && m.getRtd2() == 0 ? 1 : 0; 
			failCnt +=  m.getCntgb() == 1 && m.getSeq() % 2 == 1 && m.getAct() == 2 && m.getRtd3() == 0 ? 1 : 0; 
		}
		
		lblApActive.setText(activeCnt+    "");
		lblApInactive.setText(inactiveCnt+"");

		lblTagActive.setText(activeSsCnt+"");
		lblTagInactive.setText(failCnt+"");
		lblAlertActive.setText(String.format  ("%.1fV 미만: %2d건", AppMain.MOTECNF.getBatt() /1000.0, moteLBCnt));
		lblAlertWarn.setText(String.format("주의 : %2d건", wCnt) );
		lblAlertInactive.setText(String.format("경고 : %2d건", oBCnt) );
		lblAlertActive.requestLayout();
		lblAlertInactive.requestLayout();

//		dashb1.queryData();
//		dashb2.queryData();

	}
	

}