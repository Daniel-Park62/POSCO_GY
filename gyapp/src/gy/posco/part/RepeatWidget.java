package gy.posco.part;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("serial")
public class RepeatWidget extends Composite {

	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//	ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
//	URL url2 = FileLocator.find(bundle, new Path("images/moteicon_active.png"), null);
//	ImageDescriptor icon_active = ImageDescriptor.createFromURL(url2);
//	Image img_active = resourceManager.createImage(icon_active);
//	URL urlin = FileLocator.find(bundle, new Path("images/moteicon_inactive.png"), null);
//	ImageDescriptor icon_inactive = ImageDescriptor.createFromURL(urlin);
//	Image img_inactive = resourceManager.createImage(icon_inactive);
//	URL urllow = FileLocator.find(bundle, new Path("images/moteicon_lowbattery.png"), null);
//	ImageDescriptor icon_low = ImageDescriptor.createFromURL(urllow);
//	Image img_low = resourceManager.createImage(icon_low);
	Image img_active = new Image(Display.getCurrent(),"images/moteicon_active.png");
	Image img_inactive = new Image(Display.getCurrent(),"images/moteicon_inactive.png");
	Image img_low = new Image(Display.getCurrent(),"images/moteicon_lowbattery.png");
	
	String sloc ;
	String id ;
	int idx ;
	Label lbl = new Label(this, SWT.CENTER);
	
	public RepeatWidget(Composite parent, String id, int x, int y) {
		super(parent, SWT.NONE);
		this.setSize(66, 46);
		this.setBackgroundImage(img_active);
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		this.id  = id ;
		this.setLocate(x, y);
		lbl.setFont(new Font(null, "���� ���", 14, SWT.NORMAL));
		lbl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lbl.setText(id);
		lbl.setBounds(10, 10, 40, 30);
		lbl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
	}
	
	public void setLocate(int x, int y ) {
		this.setLocation(x , y );
	}
	public void setImage(int x) {
		if (x == 2) {
			this.setBackgroundImage(img_active);
		} else if (x == 1) {
			this.setBackgroundImage(img_inactive);
		} else if (x == 0) {
			this.setBackgroundImage(img_low);
		}
	}

	public void setId(String id) {
		this.id = id;
		lbl.setText(id);
	}


	public int getIdx() {
		return idx;
	}

}
