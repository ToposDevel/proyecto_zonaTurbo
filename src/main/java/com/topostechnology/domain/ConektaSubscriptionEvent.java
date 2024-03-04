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
@Table(name = "conekta_subscription_event")
@Getter
@Setter
public class ConektaSubscriptionEvent extends CoreCatalogEntity {

	private static final long serialVersionUID = -3032596162471882160L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @ManyToOne
    @JoinColumn(name = "conekta_subscription_id")
    private ConektaSubscription conektaSubscription;
    
    @Column(name="event_type")
    private String eventType;
    
	@Column(name = "conekta_created_at", nullable = false)
	private Integer conektaCreatedAt;
	
	@Column(name="conekta_plan_id")
	private String conektaPlanId;
	
	@Column(name="altan_offer_id")
	private String altanOfferId;
	
	@Column(name="subscription_start")
	private Integer subscriptionStart;
	
	@Column(name="canceled_at")
	private Integer canceledAt;
	
	@Column(name="paused_at")
	private Integer pausedAt;
	
	@Column(name="billing_cycle_start")
	private Integer billingCycleStart;
	
	@Column(name="billing_cycle_end")
	private Integer billingCycleEnd;
	
	@Column(name="payment_amount")
	public Integer paymentAmount;
	
	
//	@Column(name="customer_id")
//	private String customerId;
	
	

}
