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

@Entity
@Table(name = "conekta_order_event")
@Getter
@Setter
public class ConektaOrderEvent extends CoreCatalogEntity {

	private static final long serialVersionUID = -6022973951860558450L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="conekta_charge_id", nullable = true)
    private String conektaChargeId;

    @ManyToOne
    @JoinColumn(name = "conekta_order_id")
    private ConektaOrder conektaOrder;
    
    @Column(name="event_type", nullable = false)
    private String eventType;
    
	@Column(name = "conekta_created_at", nullable = false)
	private Integer conektaCreatedAt;
	
	@Column(name="amount",  nullable = true)
	public Integer amount;
	
	@Column(name="amount_refunded",  nullable = true)
	public Integer amountRefunded;

    @Column(name="currency", nullable = true)
    private String currency;
    
    @Column(name="payment_status", nullable = true)
    private String paymentStatus;
    
    @Column(name="payment_method", nullable = true)
    private String paymentMethod;
    
    
}
