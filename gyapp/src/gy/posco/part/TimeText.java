package gy.posco.part;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TimeText extends Text {

	@Override
	protected void checkSubclass() {
	    //  allow subclass

	}
	
	public TimeText(Composite parent, int style) {
		super(parent, style) ;
		// TODO Auto-generated constructor stub
		setTextLimit(10);
		setText("HH:MM:SS");
		final Calendar calendar = Calendar.getInstance();
		addListener(SWT.FocusIn, new Listener() {
		      @Override
			public void handleEvent(Event e) {
		          setSelection(0,8);
		        }
		});
		addListener(SWT.Verify , new Listener() {
			boolean ignore;

			@Override
			public void handleEvent(Event e) {
		        if (ignore)
		            return;
		          e.doit = false;
		          StringBuffer buffer = new StringBuffer(e.text);
		          char[] chars = new char[buffer.length()];
		          buffer.getChars(0, chars.length, chars, 0);
		          if (e.character == '\b') {
		            for (int i = e.start; i < e.end; i++) {
		              switch (i) {
		              case 0: /* [H]H */
		              case 1: /* H[H] */ {
		                buffer.append('H');
		                break;
		              }
		              case 3: /* [M]M */
		              case 4: /* M[M] */{
		                buffer.append('M');
		                break;
		              }
		              case 6: /* [S]S */
		              case 7: /* S[S] */{
		                buffer.append('S');
		                break;
		              }
		              case 2: /* HH[:]MM */
		              case 5: /* MM[:]SS */{
		                buffer.append(':');
		                break;
		              }
		              default:
		                return;
		              }
		            }
		            
		            setSelection(e.start, e.start + buffer.length());
		            ignore = true;
		            insert(buffer.toString());
		            ignore = false;
		            setSelection(e.start, e.start);
		            return;
		          }

		          int start = e.start;
		          if (start > 7)
		            return;
		          int index = 0;
		          for (int i = 0; i < chars.length; i++) {
		            if (start + index == 2 || start + index == 5) {
		              if (chars[i] == ':') {
		                index++;
		                continue;
		              }
		              buffer.insert(index++, ':');
		            }
		            if (chars[i] < '0' || '9' < chars[i])
		              return;
		            if (start + index == 0 && '2' < chars[i])
		              return; /* [H]H */
		            if (start + index == 3 && '5' < chars[i])
		              return; /* [M]M */
		            if (start + index == 6 && '5' < chars[i])
			              return; /* [S]S */
		            index++;
		          }
		          String newText = buffer.toString();
		          int length = newText.length();
		          StringBuffer date = new StringBuffer(getText());
		          date.replace(e.start, e.start + length, newText);

		          String hh = date.substring(0, 2);
		          if (hh.indexOf('H') == -1) {
		            int hour = Integer.parseInt(hh);
		            if (0 > hour || hour > 24)
			              return;
		            calendar.set(Calendar.HOUR,hour) ;
		          }
		          String mm = date.substring(3, 5);
		          if (mm.indexOf('M') == -1) {
		            int min = Integer.parseInt(mm) ;
		            if (0 > min || min > 59)
		              return;
		            calendar.set(Calendar.MINUTE, min);
		          }
		          String ss = date.substring(6, 8);
		          if (ss.indexOf('S') == -1) {
		            int sec = Integer.parseInt(ss);
		            if (0 > sec || sec > 59)
		              return;
		            calendar.set(Calendar.SECOND, sec);
		          } 
		          setSelection(e.start, e.start + length);
		          ignore = true;
		          insert(newText);
		          ignore = false;
		   				
			}
		});
		

	}

}
