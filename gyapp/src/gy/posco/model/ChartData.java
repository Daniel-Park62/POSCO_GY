package gy.posco.model;

import java.sql.Timestamp;

public class ChartData {
	private Timestamp tm;
	private int sensorNo ;  // sensorno
	private double temp;
	
	public ChartData(Timestamp tm, int sensorNo, double temp) {
		super();
		this.tm = tm;
		this.sensorNo = sensorNo;
		this.temp = temp;
	}
	
	public Timestamp getTm() {
		return tm;
	}
	public int getSensorNo() {
		return sensorNo;
	}
	public double getTemp() {
		return temp;
	}

}
