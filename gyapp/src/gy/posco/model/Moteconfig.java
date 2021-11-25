package gy.posco.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 * The persistent class for the moteconfig database table.
 * 
 */
@Entity
@NamedQuery(name="Moteconfig.findAll", query="SELECT m FROM Moteconfig m")
public class Moteconfig  {

	private static Moteconfig instance = new Moteconfig();
	public static Moteconfig getInstance() {
		return instance;
	}

	@Id
	private int pkey;

	private int batt;

	private int measure;

	private String syscode;

	private String updauto;

	private Moteconfig() {
	}

	public int getBatt() {
		return this.batt;
	}

	public void setBatt(int batt) {
		this.batt = batt;
	}

	public int getMeasure() {
		return this.measure;
	}

	public void setMeasure(int measure) {
		this.measure = measure;
	}

	public int getPkey() {
		return this.pkey;
	}

	public void setPkey(int pkey) {
		this.pkey = pkey;
	}

	public String getSyscode() {
		return this.syscode;
	}

	public void setSyscode(String syscode) {
		this.syscode = syscode;
	}

	public String getUpdauto() {
		return updauto;
	}

	public void setUpdauto(String updauto) {
		this.updauto = updauto;
	}

}