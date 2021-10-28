package gy.posco.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import gy.posco.part.AppMain;

public class FindMoteInfo {

	
	private ArrayList<Moteinfo> tempList ;
	private ArrayList<Motehist> tempList2 ;
	private ArrayList<ChartData> chList ;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");


    public ArrayList<Motehist> getMoteHists(String fmdt, String todt, int gb, String qval) {
    	Timestamp ts_fmdt = Timestamp.valueOf(fmdt) ;
    	Timestamp ts_todt = Timestamp.valueOf(todt) ;

    	return getMoteHists(ts_fmdt, ts_todt, gb, qval)  ;
    }
    
    public ArrayList<Moteinfo> getMoteInfos(Timestamp fmdt, Timestamp todt, int seq) {
		tempList = new ArrayList<Moteinfo>();
		EntityManager em = AppMain.emf.createEntityManager();
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		String sseqif ;
		if (seq == 0) {
			sseqif = "";
		} else {
			sseqif = "and t.seq = " + seq ;
		}
		
        TypedQuery<Moteinfo> qMotes = em.createQuery("select t from Moteinfo t " 
        		+ "where t.tm between :fmdt and :todt " + sseqif + " order by t.tm ,t.seq ", Moteinfo.class);

        qMotes.setParameter("fmdt", fmdt);
        qMotes.setParameter("todt", todt);
        qMotes.getResultList().stream().forEach( t -> tempList.add(t));
        
		em.close();

		return tempList ;

    }

    public ArrayList<Motehist> getMoteHists(Timestamp fmdt, Timestamp todt, int gb, String qval) {
		tempList2 = new ArrayList<Motehist>();
		EntityManager em = AppMain.emf.createEntityManager();
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		String sseqif ;
		switch (gb) {
		case 1:
			sseqif = "and t.seq = " + qval ;
			break;
		case 2:
			sseqif = "and t.bno = " + qval ;
			break;
		case 3:
			sseqif = "and t.stand = '" + qval +"'" ;
			break;
		default:
			sseqif = "" ;
			break;
		}
		
        TypedQuery<Motehist> qMotes = em.createQuery("select t from Motehist t " 
        		+ "where t.tm between :fmdt and :todt " + sseqif + " order by t.tm desc"
        		, Motehist.class);

        qMotes.setParameter("fmdt", fmdt);
        qMotes.setParameter("todt", todt);
        qMotes.setHint(QueryHints.READ_ONLY, HintValues.TRUE);
        qMotes.getResultList().stream().forEach( t -> tempList2.add(t));
        
		em.close();

		return tempList2 ;

    }

    public ArrayList<ChartData> getChartData(Timestamp fmdt, Timestamp todt, int seq) {
		EntityManager em = AppMain.emf.createEntityManager();
		em.clear();
//		em.getEntityManagerFactory().getCache().evictAll();
		
		String sql = " CALL SP_CHARTDATA('" + fmdt + "', '" + todt + "'," + seq + ")" ; 
 
		Query nativeQuery = em.createNativeQuery(sql);
	    List<Object[]> resultList = nativeQuery.getResultList();
	    chList = resultList.stream().map(r -> new ChartData( 
	    		Timestamp.valueOf(r[0].toString()) , ((Long)r[1]).intValue(),  (Double)r[2] ))
	    		.collect(Collectors.toCollection(ArrayList::new)) ;
        
		em.close();

		return chList ;

    }

    public ArrayList<ChartData> getChartData_hist(Timestamp fmdt, Timestamp todt, int seq) {
		EntityManager em = AppMain.emf.createEntityManager();
		em.clear();
//		em.getEntityManagerFactory().getCache().evictAll();
		
		String sql = " CALL SP_CHARTDATA_HIST('" + fmdt + "', '" + todt + "'," + seq + ")" ; 
 
		Query nativeQuery = em.createNativeQuery(sql);
	    List<Object[]> resultList = nativeQuery.getResultList();
	    chList = resultList.stream().map(r -> new ChartData( 
	    		Timestamp.valueOf(r[0].toString()) , ((Long)r[1]).intValue(),  (Double)r[2] ))
	    		.collect(Collectors.toCollection(ArrayList::new)) ;
        
		em.close();

		return chList ;

    }

}