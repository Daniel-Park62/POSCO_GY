package gy.posco.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ibm.icu.text.SimpleDateFormat;

import gy.posco.part.AppMain;


/**
 * The persistent class for the motestatus database table.
 * 
 */
@Entity
@NamedQuery(name = "Motestatus.sensorList", query = "select a from Motestatus a where a.gubun = 'S' and a.spare = 'N' ")
@NamedQuery(name="Motestatus.findAll", query="SELECT m FROM Motestatus m")
@NamedQuery(name="Motestatus.findOne", query="SELECT m FROM Motestatus m where m.mmgb = :mmgb and m.stand = :stand and m.loc = :loc and m.tb = :tb")
@NamedQuery(name="Motestatus.findOne2", query="SELECT m FROM Motestatus m where m.mmgb = :mmgb and m.seq = :seq")
public class Motestatus  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private short pkey;

	private String mmgb = "1";
	
	private short seq ;
	private short bno ;
	private short cntgb ;
	private short swseq ;
	
	private short stand;
	private String loc = "I";
	private String tb  = "T";
	private String locnm ;
	
	@Column(updatable=false, insertable=false)
	private short act;

	@Column(updatable=false, insertable=false)
	private short status;

	@Column(updatable=false, insertable=false)
	private int batt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="batt_dt")
	private Date battDt;

	private String descript;

	private String gubun = "S"; // 센서/리피터구분 S, R;

	@Column(updatable=false, insertable=false)
	private String mac = "";

	private String spare = "N"; // 예비품 Y,N;

	@Column(name="temp_d")
	private float tempD;

	@Column(name="temp_w")
	private float tempW;
	private float xrang;

	public Motestatus() {
	}

	public short getPkey() {
		return pkey;
	}

	public String getMmgb() {
		return mmgb;
	}

	public String getMmgbNm() {
		return mmgb.equals("2") ? "D/S" : "W/S";
	}

	public void setMmgb(String mmgb) {
		this.mmgb = mmgb;
	}

	public short getSeq() {
		return this.seq;
	}

	public void setSeq(short seq) {
		this.seq = seq;
	}

	public short getSwseq() {
		return swseq;
	}

	public void setSwseq(short swseq) {
		this.swseq = swseq;
	}

	public short getAct() {
		return this.act;
	}

	public short getBno() {
		return bno;
	}

	public void setBno(short bno) {
		this.bno = bno;
	}

	public short getCntgb() {
		return cntgb;
	}

	public void setCntgb(short cntgb) {
		this.cntgb = cntgb;
	}

	public void setAct(short act) {
		this.act = act;
	}

	public short getStatus() {
		return status;
	}

	public int getBatt() {
		return this.batt;
	}

	public void setBatt(int batt) {
		this.batt = batt;
	}

	public Date getBattDt() {
		return this.battDt ;
	}
	
	public String getStrBattDt() {
		return this.battDt == null ? "" : new SimpleDateFormat("yyyy/MM/dd").format(battDt) ;
	}

	public void setBattDt(Date battDt) {
		this.battDt = battDt;
	}

	public String getDescript() {
		return this.descript == null ? "" : this.descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getGubun() {
		return this.gubun;
	}

	public void setGubun(String gubun) {
		this.gubun = gubun;
	}

	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSpare() {
		return this.spare;
	}

	public void setSpare(String spare) {
		this.spare = spare;
	}

	public short getStand() {
		return this.stand;
	}

	public void setStand(short stand) {
		this.stand = stand;
	}

	public String getLoc() {
		return loc;
	}

	public String getLocnm() {
		return locnm == null ? "" : locnm;
	}

	public void setLocnm(String locnm) {
		this.locnm = locnm;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getTb() {
		return this.tb;
	}

	public void setTb(String tb) {
		this.tb = tb;
	}

	public float getTempD() {
		return tempD;
	}

	public void setTempD(float tempD) {
		this.tempD = tempD;
	}

	public float getTempW() {
		return tempW;
	}

	public void setTempW(float tempW) {
		this.tempW = tempW;
	}

	public float getXrang() {
		return xrang;
	}

	public void setXrang(float xrang) {
		this.xrang = xrang;
	}

	public void setStatus(short status) {
		this.status = status;
	}
	
	public String getLocNmlong() {
		if (locnm.length() > 0 && (cntgb == 1 || gubun.equals("R"))) {
			return locnm ;
		}
		if (cntgb == 1)   return "비접촉 #" + stand  ;
		StringBuilder sb = new StringBuilder() ;
		sb.append("#" + stand + " ");
		sb.append(AppMain.getLocName(loc) + " " + tb) ;
		
		return sb.toString() ;
		
	}

}