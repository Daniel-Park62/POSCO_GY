package gy.posco.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

/**
 * Entity implementation class for Entity: TdataAnaly
 *
 */
@SqlResultSetMapping(name = "TdataAnalyMap", classes = @ConstructorResult(columns = { @ColumnResult(name = "seq"),
		@ColumnResult(name = "stm", type = Date.class), @ColumnResult(name = "etm", type = Date.class),
		@ColumnResult(name = "dftm", type = Time.class), @ColumnResult(name = "y1temp"), @ColumnResult(name = "y2temp"),
		@ColumnResult(name = "stemp"), @ColumnResult(name = "etemp"), @ColumnResult(name = "atemp")

}, targetClass = TdataAnaly.class))

public class TdataAnaly implements Serializable {

	@Id
	private int seq;
	private Date stm;
	private Date etm;
	private Time dftm;
	private double y1temp;
	private double y2temp;
	private double stemp;
	private double etemp;
	private double atemp;
	private static final long serialVersionUID = 1L;

	public TdataAnaly(int seq, Date stm, Date etm, Time dftm, double y1temp, double y2temp, double stemp, double etemp,	double atemp) {
		this.seq   =  seq    ;
		this.stm   =  stm    ; 
		this.etm   =  etm    ; 
		this.dftm   = dftm   ; 
		this.y1temp = y1temp ;
		this.y2temp = y2temp ;
		this.stemp =  stemp  ;
		this.etemp =  etemp  ;
		this.atemp =  atemp  ;
	}

	public int getSeq() {
		return this.seq;
	}

	public Date getStm() {
		return this.stm ;
	}

	public Date getEtm() {
		return this.etm;
	}

	public void setEtm(Date etm) {
		this.etm = etm;
	}

	public Time getDftm() {
		return this.dftm;
	}

	public double getY1temp() {
		return this.y1temp;
	}

	public double getY2temp() {
		return this.y2temp;
	}

	public double getStemp() {
		return this.stemp;
	}

	public double getEtemp() {
		return this.etemp;
	}

	public double getAtemp() {
		return this.atemp;
	}
@Override
public String toString() {
	return "TdataAnaly [seq=" + seq + ", stm=" + stm + ", etm=" + etm + ", dftm=" + dftm + ", y1temp=" + y1temp
			+ ", y2temp=" + y2temp + ", stemp=" + stemp + ", etemp=" + etemp + ", atemp=" + atemp + "]";
}
}
