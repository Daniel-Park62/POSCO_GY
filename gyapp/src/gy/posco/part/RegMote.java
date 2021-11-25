package gy.posco.part;

import java.util.ArrayList;

import javax.annotation.PreDestroy;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import gy.posco.model.Moteconfig;
import gy.posco.model.Motestatus;

public class RegMote {

	private class ContentProvider implements IStructuredContentProvider {
		/**
		 * 
		 */
		@Override
		public Object[] getElements(Object input) {
			// return new Object[0];
			ArrayList<Motestatus> arrayList = (ArrayList<Motestatus>) input;
			return arrayList.toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

//	List<MoteInfo> selectedTagList = new ArrayList<MoteInfo>();
	Point selectedPoint = new Point(0, 0);

	EntityManager em = AppMain.emf.createEntityManager();

	Image slice_page = AppMain.getMyimage("regmote_t.png");

	Label lblApActive;
	Label lblApInactive;
	Label lblTagActive;
	Label lblTagInactive;
	Label lblAlertActive;
	Label lblAlertInactive;
	Label lblDate, lblTime;

	private Button btnMod ;
	private Table table;
	private TableViewer tvlist;
	ArrayList<Motestatus> tempList;
	public static final String[] PROPS = { "", "", "", "", "", "DESC", "TYPE", "SPARE", "BATTDT" };
	private static String[] statusNm = { "Inactive", "Sleep", "Active" };

	public RegMote(Composite parent, int style) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginBottom = 5;
		gl_composite.horizontalSpacing = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		final Font font2 = SWTResourceManager.getFont("Tahoma", 16, SWT.BOLD);
		final Font font3 = SWTResourceManager.getFont("맑은 고딕", 14, SWT.NORMAL);

		CLabel lbl = new CLabel(composite, SWT.NONE);
		lbl.setImage(slice_page);
		lbl.setText("무선장치 관리");
		lbl.setFont(SWTResourceManager.getFont("Tahoma", 22, SWT.BOLD));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(lbl);

		/*
		 * RegMote.addKeyListener(new keyAdapter() { public void KeyPressed(KeyEvent e)
		 * { if ( e.keyCode == SWT.F15 ) refreshSensorList(); } });
		 */
		Composite modbutton = new Composite(composite, SWT.NORMAL);
		GridLayout gl_layout = new GridLayout(4, false);
		gl_layout.marginLeft = 15;
		modbutton.setLayout(gl_layout);
		modbutton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		modbutton.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		{
			Button b = new Button(modbutton, SWT.ON_TOP);
			b.setFont(font2);
			b.setText(" 장치추가 ");
//			b.setSize(140, -1);
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					NewMoteDlg newmote = new NewMoteDlg(parent.getShell());
					if (newmote.open() == Window.OK) {
						if (newmote.getMote().getSeq() > 0) {
							em.getTransaction().begin();
							em.persist(newmote.getMote());
							em.getTransaction().commit();
							refreshSensorList();
						} else {
							MessageDialog.openError(parent.getShell(), "Mote 등록", "Seq 0 는 입력되지않습니다.");
						}

					}
				}
			});
		}

		btnMod = new Button(modbutton, SWT.ON_TOP);
		btnMod.setFont(font2);
		btnMod.setText(" 수정 ");

		btnMod.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = tvlist.getTable().getSelectionIndex();

				if (index != -1) {
					Motestatus mote = tempList.get(index);
					NewMoteDlg newmote = new NewMoteDlg(parent.getShell(), false);
					newmote.setMote(mote);
					if (newmote.open() == Window.OK) {
						em.getTransaction().begin();
//							if (!em.contains(mote)) {
						em.merge(mote);
//							}

						em.getTransaction().commit();
						MessageDialog.openInformation(parent.getShell(), "Mote 수정", "수정되었습니다.!");
						refreshSensorList();
					}

				}
			}
		});

		{
			Button b = new Button(modbutton, SWT.ON_TOP);
			b.setFont(font2);
			b.setText(" 삭제 ");
			b.setSize(140, 50);
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int index = tvlist.getTable().getSelectionIndex();

					if (index != -1) {
						em.getTransaction().begin();
						Motestatus mote = tempList.get(index);
						if (MessageDialog.openConfirm(parent.getShell(), "확인", mote.getDescript() + " : 삭제하시겠습니까?")) {
							if (!em.contains(mote)) {
								mote = em.merge(mote);
							}
							em.remove(mote);
							em.getTransaction().commit();
							MessageDialog.openInformation(parent.getShell(), "Mote 삭제", "삭제되었습니다.!");
							refreshSensorList();
						}

					}
				}
			});
		}

		{
			Button b = new Button(modbutton, SWT.ON_TOP);
			b.setFont(font2);
			b.setText("스탠드설정");
			b.setSize(140, 50);

			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					RegStand2 newmote = new RegStand2(parent.getShell());

					if (newmote.open() == Window.OK) {
						AppMain.appmain.reloaddata();
						refreshSensorList();
					}

				}
			});
		}

		Composite composite_3 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(15, 5).equalWidth(false).numColumns(2).applyTo(composite_3);
//		GridLayout gl_composite_3 = new GridLayout(2, false);
//		gl_composite_3.marginRight = 15;
//		gl_composite_3.marginLeft = 15;
//		gl_composite_3.marginBottom = 5 ;
//		composite_3.setLayout(gl_composite_3);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(composite_3);
		composite_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		tvlist = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION |SWT.MULTI);
		table = tvlist.getTable();
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(table);
//		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font3);
		table.setHeaderBackground(AppMain.coltblh);

		tvlist.setUseHashlookup(true);
		String[] cols1 = new String[] { "WD", "센서번호", "Chock No", "장치위치", "장치설명", "타입", "예비품", "배터리설치일", "상태정보", "Mac 주소" };
		int[] cwid = new int[] { 80, 90, 110, 200, 250, 60, 90, 150, 120, 230 };
		int[] cas1 = new int[] { SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,
				SWT.CENTER, SWT.CENTER, SWT.CENTER };

		TableViewerColumn tvcol = new TableViewerColumn(tvlist, SWT.NONE );
		for (int i = 0; i < cols1.length; i++) {
			tvcol = new TableViewerColumn(tvlist, cas1[i]);

			TableColumn tableColumn = tvcol.getColumn();
			tableColumn.setText(cols1[i]);
			tableColumn.setWidth(cwid[i]);
		}

		tvlist.setContentProvider(new ContentProvider());
		tvlist.setLabelProvider(new MoteLabelProvider());
		tvlist.setInput(tempList);

		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = (int) (event.gc.getFontMetrics().getHeight() * 1.5);
			}

		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (table.getSelectionIndex() >= 0) {
					btnMod.notifyListeners(SWT.Selection, null);
				}
				super.mouseDoubleClick(e);
			}
		});
		
		refreshSensorList();
//		table.layout();
		composite_3.layout();

		Moteconfig moteconfig = AppMain.MOTECNF;

		Composite comp_b = new Composite(composite, SWT.NONE);
		comp_b.setBackground(SWTResourceManager.getColor(250, 250, 250));
		GridData gd_Composite_2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_Composite_2.heightHint = 120;
		gd_Composite_2.minimumHeight = 120;
		comp_b.setLayoutData(gd_Composite_2);

		GridLayout gl_composite_2 = new GridLayout(1, true);
		gl_composite_2.marginTop = 15;
		gl_composite_2.marginWidth = 15;

		comp_b.setLayout(gl_composite_2);

		Group group_t = new Group(comp_b, SWT.NONE);
		group_t.setBackground(SWTResourceManager.getColor(250, 250, 250));

		group_t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		group_t.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_g = new GridLayout(12, false);
		gl_g.horizontalSpacing = 10;
		gl_g.marginTop = 10;
//		gl_composite_2.marginBottom = 5;

		group_t.setLayout(gl_g);
		group_t.setFont(SWTResourceManager.getFont("", 1, SWT.NORMAL));

		Label lblsyscode = new Label(group_t, SWT.NONE);
		lblsyscode.setText(" SYSTEM CODE ");
		lblsyscode.setFont(font3);
		lblsyscode.setAlignment(SWT.RIGHT);
		lblsyscode.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblsyscode.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		Label systext = new Label(group_t, SWT.BORDER);
		systext.setText(" " + moteconfig.getSyscode() + " ");
		systext.setFont(SWTResourceManager.getFont("Calibri", 14, SWT.BOLD));
		systext.setSize(80, -1) ;

		Label lblmeasure = new Label(group_t, SWT.NONE);
		lblmeasure.setText("   온도정보수집간격 ");
		lblmeasure.setFont(font3);
		lblmeasure.setAlignment(SWT.RIGHT);
		lblmeasure.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblmeasure.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		Spinner spinner = new Spinner(group_t, SWT.BORDER | SWT.CENTER);
		spinner.setMinimum(1);
		spinner.setMaximum(60);
		spinner.setSelection(moteconfig.getMeasure());
		spinner.setIncrement(1);
		spinner.setFont(font3);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		spinner.setSize(120, -1);

		Label lblbatt = new Label(group_t, SWT.NONE);
		lblbatt.setText("   배터리경고기준");
		lblbatt.setFont(font3);
		lblbatt.setAlignment(SWT.RIGHT);
		lblbatt.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblbatt.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		Spinner spbatt = new Spinner(group_t, SWT.BORDER | SWT.CENTER);
		spbatt.setMinimum(3000);
		spbatt.setMaximum(4000);
		spbatt.setSelection(moteconfig.getBatt());
		spbatt.setIncrement(100);
		spbatt.setFont(font3);
		spbatt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		spbatt.setSize(180, -1);

		lblbatt = new Label(group_t, SWT.NONE);
		lblbatt.setText("  Stand자동수정");
		lblbatt.setFont(font3);
		lblbatt.setAlignment(SWT.RIGHT);
		lblbatt.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblbatt.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		Button btnauto = new Button(group_t, SWT.CHECK) ;
		btnauto.setSelection(moteconfig.getUpdauto().equals("Y"));

		/*
		 * Label lblmeasure = new Label(group_t, SWT.NONE);
		 * lblmeasure.setText("Time Interval"); lblmeasure.setAlignment(SWT.RIGHT);
		 * lblmeasure.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		 * 
		 * Text measuretext = new Text(group_t, SWT.BORDER | SWT.CENTER );
		 * measuretext.setText(String.valueOf(Moteconfig.getMeasure()) );
		 * measuretext.setLayoutData(new GridData(50,10));
		 */
		Label lblpass = new Label(group_t, SWT.NONE);
		lblpass.setFont(font3);
		lblpass.setText("   비밀번호 ");
		lblpass.setAlignment(SWT.RIGHT);
		lblpass.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblpass.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));

		Text passwordField = new Text(group_t, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		passwordField.setEchoChar('*');
		passwordField.setFont(font3);
		GridData gd_pss = new GridData(SWT.FILL, GridData.CENTER, false, false, 1, 1);
		gd_pss.widthHint = 150;
		passwordField.setLayoutData(gd_pss);

		Composite btncontainer = new Composite(group_t, SWT.NONE);

		btncontainer.setLayout(new GridLayout(2, false));
		btncontainer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		btncontainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		{
			Button b = new Button(btncontainer, SWT.PUSH);

			b.setFont(font3);
			b.setEnabled(false);
			b.setText(" 저장 ");
			b.pack();
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					int lmeas = spinner.getSelection();
					int lbatt = spbatt.getSelection();

					moteconfig.setSyscode(systext.getText());
					moteconfig.setMeasure((short) lmeas);
					moteconfig.setBatt(lbatt);
					moteconfig.setUpdauto(btnauto.getSelection() ? "Y" :"N");
					em.getTransaction().begin();
					em.merge(moteconfig);
					em.getTransaction().commit();
					AppMain.sendMeasur("10") ; 
					MessageDialog.openInformation(parent.getShell(), "Save Infomation", "수정되었습니다.");

					passwordField.setText("");
				}
			});

			passwordField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent evt) {
					b.setEnabled((passwordField.getText().equals("Passw0rd!")));
				}
			});
		}
		{
			Button b = new Button(btncontainer, SWT.PUSH);
			b.setFont(font3);
			b.setText(" 취소 ");
			b.pack();
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					refreshSensorList();
					moteconfig.setSyscode(systext.getText());
					spinner.setSelection(moteconfig.getMeasure());
				}
			});
		}
		group_t.layout();

	}

	@PreDestroy
	public void preDestroy() {

	}

//	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
		EntityManager em = AppMain.emf.createEntityManager();
		tempList = new ArrayList<Motestatus>();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		TypedQuery<Motestatus> qMotes = em.createQuery("select t from Motestatus t order by t.seq ", Motestatus.class);

		qMotes.getResultList().stream().forEach(t -> tempList.add(t));

		em.close();

		tvlist.setInput(tempList);
		tvlist.refresh();

//		}

	}

	private static class MoteLabelProvider implements ITableLabelProvider {
		/**
		 * Returns the image
		 * 
		 * @param element     the element
		 * @param columnIndex the column index
		 * @return Image
		 */

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * Returns the column text
		 * 
		 * @param element     the element
		 * @param columnIndex the column index
		 * @return String
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			Motestatus mote = (Motestatus) element;
			if (mote == null)
				return "";
			switch (columnIndex) {
			case 1:
				return mote.getMmgbNm() ;
			case 2:
				return mote.getSeq() + "";
			case 3:
				return mote.getBno() + "";
			case 4:
				return mote.getLocNmlong();
			case 5:
				return mote.getDescript();
			case 6:
				return mote.getGubun();
			case 7:
				return mote.getSpare();
			case 8:
				return mote.getStrBattDt();
			case 9:
				return statusNm[mote.getAct()];
			case 10:
				return mote.getMac();
			}
			return "";
		}

		/**
		 * Adds a listener
		 * 
		 * @param listener the listener
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
			// Ignore it
		}

		/**
		 * Disposes any created resources
		 */
		@Override
		public void dispose() {
			// Nothing to dispose
		}

		/**
		 * Returns whether altering this property on this element will affect the label
		 * 
		 * @param element  the element
		 * @param property the property
		 * @return boolean
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		/**
		 * Removes a listener
		 * 
		 * @param listener the listener
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
			// Ignore
		}
	}

}