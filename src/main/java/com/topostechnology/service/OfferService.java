package com.topostechnology.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferService {

    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);
    @Autowired
    private JdbcTemplate jDBCTemplate;

    @Transactional
    public void update() {
        logger.info("Updating table");
        int result = jDBCTemplate.update("update table set field = field  where id= 1");
        logger.info("Updating result: " + result);
    }
}