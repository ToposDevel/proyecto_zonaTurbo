package com.topostechnology.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "offer")
public class Offer extends CoreCatalogEntity{

	private static final long serialVersionUID = -3024286276861420649L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "offer_id")
    private Long offerId;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "COMMERCIAL_NAME")
    private String commercialName;

    @ManyToOne
    @JoinColumn(name = "OFFER_TYPE_ID")
    private OfferType offerType;
    
    @Column(name = "DATA")
    private Long data;
    
    @Column(name = "RS")
    private Integer rs;
    
    @Column(name = "VOICE")
    private Integer voice;
    
    @Column(name = "SMS")
    private Integer sms;
    
    @Column(name = "DAYS")
    private Integer days;
    
    @Column(name = "SPEED")
    private String speed;
    
        @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private OfferCategory category;
    
    @ManyToOne
    @JoinColumn(name = "SUBCATEGORY_ID")
    private OfferSubcategory subcategory;
    
    @Column(name= "PRORATE")
    private boolean prorate;
    
	@Column(name = "TARIFF")
	private Double tariff;
}


