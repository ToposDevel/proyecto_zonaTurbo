package com.topostechnology.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "odoo_url_offert")
public class OddoUrlOffert extends CoreCatalogEntity{
	
	private static final long serialVersionUID = 7085958871060455569L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(name = "offer_id", nullable = false, unique = true)
    private Long offerId;
    
    @Column(name = "odoo_url", nullable = false)
    private String odooUrl;
    
    @Column(name = "producto", nullable = true)
    private String producto;
    
}
