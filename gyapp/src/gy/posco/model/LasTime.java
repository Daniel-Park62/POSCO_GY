package gy.posco.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.ReadOnly;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Entity implementation class for Entity: LasTime
 *
 */
@ReadOnly
@Entity
@NamedQuery(name="LasTime.findAll", query="SELECT l FROM LasTime l")
public class LasTime  {

	@Id
	private int pkey;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastm;

	private LasTime() {	}   
	public int getPkey() {
		return this.pkey;
	}

	public void setPkey(int pkey) {
		this.pkey = pkey;
	}
	
	public Date getLastm() {
		return this.lastm;
	}

	public void setLastm(Timestamp lastm) {
		this.lastm = lastm;
	}
   
	public String getStrLastm() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastm) ;
	}

}
