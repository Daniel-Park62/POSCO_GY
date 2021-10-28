package gy.posco.part;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import gy.posco.model.Motestatus;

public class NewMoteDlg extends Dialog {

	private Motestatus mote = new Motestatus();
	private boolean modflag = true;
	private Text txtSeq ;
	private Text txtBno ;
	private Text txtDesc ;
	private CDateTime cdate ;
	private Button buttonS , buttonR , btnNo, btnYes, btnWS, btnDS, btnIMR, btnWR, btnBUR, btnTOP, btnBOTTOM;
	private Spinner spstand ;
	private DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");	
	final Font font = SWTResourceManager.getFont("Calibri", 14, SWT.NORMAL);
	final Font fonth = SWTResourceManager.getFont("맑은 고딕", 13, SWT.NORMAL);

	protected NewMoteDlg(Shell parentShell) {
		super(parentShell);
		
		// TODO Auto-generated constructor stub
	}
	protected NewMoteDlg(Shell parentShell, boolean flag) {
		super(parentShell);
		this.modflag = flag;
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("나눔고딕코딩", 12, SWT.NORMAL));
		// TODO Auto-generated method stub
		Composite container = (Composite)super.createDialogArea(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.marginRight = 5;
        layout.marginLeft = 10;
        
        container.setLayout(layout);

        Label label = new Label(container, SWT.NONE);
        label.setText("마스터모트:");
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setFont(fonth);

        Group  wdGroup = new Group (container, SWT.NONE);
        wdGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
        wdGroup.setFont(new Font(null, "", 1, SWT.NORMAL));
 
        btnWS = new Button(wdGroup, SWT.RADIO);
        btnWS.setText("W/S");        
        btnWS.setFont(font);
        btnDS = new Button(wdGroup, SWT.RADIO);
        btnDS.setText("D/S");
        btnDS.setFont(font);

        Label lblSeq = new Label(container, SWT.NONE  );
        lblSeq.setFont(font);
        lblSeq.setText("Seq:");
        lblSeq.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        txtSeq = new Text(container, SWT.BORDER);
        txtSeq.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSeq.setFont(font);

        txtSeq.addModifyListener(e -> {
            Text textWidget = (Text) e.getSource();
            mote.setSeq(Short.parseShort(textWidget.getText()));
        });
        txtSeq.setEnabled(modflag);

        Label lblSno = new Label(container, SWT.NONE  );
        lblSno.setFont(font);
        lblSno.setText("Chock No:");
        lblSno.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        txtBno = new Text(container, SWT.BORDER);
        txtBno.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,  1, 1));
        txtBno.setFont(font);

        Label lblDesc = new Label(container, SWT.NONE);
        lblDesc.setText("Description:");
        lblDesc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDesc.setFont(font);

        txtDesc = new Text(container, SWT.BORDER);
        txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtDesc.setFont(font);
        txtDesc.addModifyListener(e -> {
        	Text textWidget = (Text) e.getSource();
        	mote.setDescript(textWidget.getText());
        });

        label = new Label(container, SWT.NONE);
        label.setText("Mote 구분:");
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setFont(fonth);
        // Group
        Group  genderGroup = new Group (container, SWT.NONE);
        genderGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
        genderGroup.setFont(new Font(null, "", 1, SWT.NORMAL));
 
        // Radio - mss
        buttonS = new Button(genderGroup, SWT.RADIO);
        buttonS.setText("Sensor");        
        buttonS.setFont(font);
        // Radio - mrs
        buttonR = new Button(genderGroup, SWT.RADIO);
        buttonR.setText("Repeater");
        buttonR.setFont(font);
        
        Label label_1 = new Label(container, SWT.NONE);
        label_1.setFont(fonth);
        label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_1.setText("예비품여부:");
        
        Group group = new Group(container, SWT.NONE);
        group.setLayout(new RowLayout(SWT.HORIZONTAL));
        group.setFont(SWTResourceManager.getFont( "", 1, SWT.NORMAL));
        
        btnNo = new Button(group, SWT.RADIO);
        btnNo.setFont(font);
        btnNo.setText("사용");
        
        btnYes = new Button(group, SWT.RADIO);
        btnYes.setFont(font);
        btnYes.setText("예비품");
        
        Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_2.setText("배터리교체일:");
        label_2.setFont(fonth);
/*        
        txtBattdt = new DateText(container, SWT.BORDER);
        txtBattdt.setFont(font);
        txtBattdt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtBattdt.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseDoubleClick(MouseEvent e) {
            	CalDialog cd = new CalDialog(parent.getShell(), e.x , e.y  );
                String s = (String)cd.open();
                if (s != null) {
                	txtBattdt.setText(s ) ;
                }
	    		super.mouseDoubleClick(e);
	    	}
		});
*/        
        cdate = new CDateTime(container,  CDT.BORDER |  CDT.DROP_DOWN | CDT.DATE_LONG );
        cdate.setSelection(new Date());
        cdate.setPattern("yyyy/MM/dd");
        cdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        cdate.setFont(font);
//        cdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        lblSno = new Label(container, SWT.NONE  );
        lblSno.setFont(font);
        lblSno.setText("Stand No:");
        lblSno.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spstand = new Spinner(container,SWT.BORDER ) ;
		spstand.setFont(font);
		spstand.setSelection(1);
		spstand.setMaximum(5);
		spstand.computeSize(200, -1);

        Label label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_3.setText("설치위치:");
        label_3.setFont(fonth);
        // Group
        Group  gloc = new Group (container, SWT.NONE);
        gloc.setLayout(new RowLayout(SWT.HORIZONTAL));
        gloc.setFont(new Font(null, "", 1, SWT.NORMAL));
 
        btnIMR = new Button(gloc, SWT.RADIO);
        btnIMR.setText("IMR");        
        btnIMR.setFont(font);
        btnWR = new Button(gloc, SWT.RADIO);
        btnWR.setText("WR");        
        btnWR.setFont(font);
        btnBUR = new Button(gloc, SWT.RADIO);
        btnBUR.setText("BUR");        
        btnBUR.setFont(font);
       
        label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_3.setText("");
        label_3.setFont(fonth);
        // Group
        Group  gtb = new Group (container, SWT.NONE);
        gtb.setLayout(new RowLayout(SWT.HORIZONTAL));
        gtb.setFont(new Font(null, "", 1, SWT.NORMAL));
 
        btnTOP = new Button(gtb, SWT.RADIO);
        btnTOP.setText("TOP");        
        btnTOP.setFont(font);
        btnBOTTOM = new Button(gtb, SWT.RADIO);
        btnBOTTOM.setText("BOTTOM");        
        btnBOTTOM.setFont(font);
        
        setValue();

		return container ;
	}
	
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "저장", true).setFont(fonth);
        createButton(parent, IDialogConstants.CANCEL_ID, "취소", false).setFont(fonth);

//        parent.getShell().pack();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    @Override
    protected void okPressed() {
    	if (modflag) {
    		mote.setMmgb(btnWS.getSelection() ? "1" :"2");
    		mote.setSeq(Short.parseShort(txtSeq.getText()));
    	}
    	
    	mote.setBno(Short.parseShort(txtBno.getText()));
    	mote.setDescript(txtDesc.getText());
    	mote.setGubun(buttonS.getSelection() ? "S" : "R");
    	mote.setSpare(btnYes.getSelection() ? "Y" : "N");
   		mote.setBattDt(cdate.getSelection());
    	mote.setStand((short) spstand.getSelection() );
    	mote.setLoc(btnIMR.getSelection() ? "I" : btnWR.getSelection() ? "W" : "B") ;
    	mote.setTb(btnTOP.getSelection() ? "T" : "B");
    	AppMain.sendReload() ;
        super.okPressed();
    }
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Mote 등록");
    }
    
	public Motestatus getMote() {
		return mote;
	}

	public void setMote(Motestatus mote) {
		this.mote = mote;
	}
	
	private void setValue() {
		btnWS.setSelection(mote.getMmgb().equals("1"));
		btnDS.setSelection(mote.getMmgb().equals("2"));
        txtSeq.setText(String.valueOf(mote.getSeq()));
        txtBno.setText(String.valueOf(mote.getBno()));

        txtDesc.setText(mote.getDescript());
        buttonS.setSelection(mote.getGubun().equals("S"));
        buttonR.setSelection(mote.getGubun().equals("R"));
        btnNo.setSelection(mote.getSpare().equals("N"));
        btnYes.setSelection(mote.getSpare().equals("Y"));

        cdate.setSelection(mote.getBattDt());
	    spstand.setSelection(mote.getStand());
	    btnIMR.setSelection(mote.getLoc().equals("I"));
	    btnBUR.setSelection(mote.getLoc().equals("B"));
	    btnWR.setSelection(mote.getLoc().equals("W"));
	    btnTOP.setSelection(mote.getTb().equals("T"));
	    btnBOTTOM.setSelection(mote.getTb().equals("B"));
        
	}
}
