package com.topostechnology.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class CdrRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
	
	public List<Object[]> getCdrInfoByMsisdnByDate(LocalDateTime initDateTime, LocalDateTime endDateTime , String msisdn ) {
		Query query = entityManager.createNativeQuery(" "
				+ "(SELECT cd.CDR_ID, cd.CUST_LOCAL_START_DATE, cd.ACTUAL_USAGE, cd.USAGE_MEASURE_ID "  
				+ "FROM cdr_data AS cd "
				+ "WHERE cd.PRI_IDENTITY = ?1 AND  (cd.CUST_LOCAL_START_DATE BETWEEN ?2 AND ?3)) "
				+ "UNION "
				+ "(SELECT cs.CDR_ID, cs.CUST_LOCAL_START_DATE, cs.ACTUAL_USAGE, cs.USAGE_MEASURE_ID "   
				+ "FROM cdr_sms AS cs "
				+ "WHERE cs.PRI_IDENTITY = ?1 AND  (cs.CUST_LOCAL_START_DATE BETWEEN ?2 AND ?3)) "
				+ "UNION "
				+ " (SELECT cv.CDR_ID, cv.CUST_LOCAL_START_DATE, cv.ACTUAL_USAGE, cv.USAGE_MEASURE_ID "
				+ "FROM cdr_voice AS cv "
				+ "WHERE cv.PRI_IDENTITY = ?1 AND  (cv.CUST_LOCAL_START_DATE BETWEEN ?2 AND ?3)) ");

		query.setParameter(1, msisdn);
		query.setParameter(2, initDateTime);
		query.setParameter(3, endDateTime);
		
		List<Object[]> rows = query.getResultList();
		return rows;
	}

	public List<Object[]> getCdrInfoByMsisdnByDate0(LocalDateTime initDateTime, LocalDateTime endDateTime , String msisdn, String cdrTableName ) {
		Query query = entityManager.createNativeQuery(" "
				+ "SELECT CDR_ID, "
				+ "CUST_LOCAL_START_DATE, "
				+ "ACTUAL_USAGE, "
				+ "USAGE_MEASURE_ID "
				+ "FROM " + cdrTableName 
				+" WHERE PRI_IDENTITY = ?1 AND "
				+ " CUST_LOCAL_START_DATE BETWEEN ?2 AND ?3 "
				+ "ORDER BY CUST_LOCAL_START_DATE");
		query.setParameter(1, msisdn);
		query.setParameter(2, initDateTime);
		query.setParameter(3, endDateTime);
		
		List<Object[]> rows = query.getResultList();
		return rows;
	}
	
	public List<Object[]> getCdrInfoByDate(LocalDateTime initDateTime, LocalDateTime endDateTime, String cdrTableName ) {
		Query query = entityManager.createNativeQuery(" "
				+ "SELECT CDR_ID, "
				+ "CUST_LOCAL_START_DATE, "
				+ "PRI_IDENTITY, "
				+ "MainOfferingID, "
				+ "FREE_UNIT_ID_1, "
				+ "ACTUAL_USAGE, "
				+ "USAGE_MEASURE_ID "
				+ "FROM " + cdrTableName 
				+" WHERE "
				+ " CUST_LOCAL_START_DATE BETWEEN ?1 AND ?2 "
				+ "ORDER BY CDR_ID");
		query.setParameter(1, initDateTime);
		query.setParameter(2, endDateTime);
		
		List<Object[]> rows = query.getResultList();
		return rows;
	}
	
	public List<Object[]> getOfferInfoByMonth(LocalDateTime initDateTime, LocalDateTime endDateTime, String cdrTableName ) {
		Query query = entityManager.createNativeQuery(" "
				+"SELECT MainOfferingID, " + 
				" PRI_IDENTITY " + 
				" FROM "+ cdrTableName +
				" WHERE CUST_LOCAL_START_DATE BETWEEN ?2 AND ?3"  +
				" ORDER BY MainOfferingID");
		query.setParameter(2, initDateTime);
		query.setParameter(3, endDateTime);
		List<Object[]> rows = query.getResultList();
		return rows;
	}
	
	
	public Object countMsisd(String msisdn, String cdrTableName) {
		// TODO ACTUALIZAR QUERY
		Query query = entityManager.createNativeQuery(" "
				+  "SELECT count(cdr_id) " 
				+ " FROM "+ cdrTableName 
				+ " where PRI_IDENTITY = ?1");
		query.setParameter(1, msisdn);
		return  query.getSingleResult();
	}
	
}

