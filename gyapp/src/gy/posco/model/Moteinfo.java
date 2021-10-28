package gy.posco.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.ReadOnly;

import gy.posco.part.AppMain;

/**
 * The persistent class for the moteinfo database table.
 * 
 */
@ReadOnly
@Entity
@Table(name = "vmoteinfo")
@NamedQuery(name="Moteinfo.findAll", query="SELECT m FROM Moteinfo m")
public class Moteinfo  {

	@Id
	private long pkey;
	private String mmgb;
	private short seq;
	private short bno;
	private short cntgb;
	private short swseq ;
	
	public short getSwseq() {
		return swseq;
	}

	private int batt;
	private short act;

	private float rtd1;
	private float rtd2;
	private float rtd3;
	private float temp;

	private short stand;
	private String loc;
	private String tb;

	@Column(name="temp_d")
	private float tempD;

	@Column(name="temp_w")
	private float tempW;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tm;


	public Moteinfo() {
	}

	public long getPkey() {
		return this.pkey;
	}

	public String getMmgb() {
		return mmgb;
	}

	public String getMmgbNm() {
		return mmgb.equals("2") ? "D/S" : "W/S";
	}

	public float getRtd1() {
		return rtd1;
	}

	public float getRtd2() {
		return rtd2;
	}

	public float getRtd3() {
		return rtd3;
	}

	public float getRtd() {
		return Math.max(Math.max(rtd1, rtd2),rtd3);
	}
	
	public String getLoc() {
		return loc;
	}

	public String getTb() {
		return tb;
	}

	public short getAct() {
		return this.act;
	}

	public int getBatt() {
		return batt;
	}

	public short getSeq() {
		return this.seq;
	}

	public short getBno() {
		return this.bno;
	}

	public short getCntgb() {
		return cntgb;
	}

	public short getStand() {
		return this.stand;
	}

	public float getTemp() {
		return this.temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public Date getTm() {
		return this.tm;
	}

	public void setTm(Timestamp tm) {
		this.tm = tm;
	}

	public float getTempD() {
		return tempD;
	}

	public float getTempW() {
		return tempW;
	}

	public int getStatus() {
		int sts = 0 ;
		if ( rtd1 > tempD ) 
			sts = 2 ;
		else if ( rtd1 > tempW )
			sts = 1 ;
		return sts ;
	}

	public String getLocNmlong() {
		if (cntgb == 1)  return "비접촉" ;
		StringBuilder sb = new StringBuilder() ;
		sb.append(stand + " Stand ");
		sb.append(AppMain.getLocName(loc) + " " + tb) ;
		
		return sb.toString() ;
		
	}

}