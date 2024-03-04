package com.topostechnology.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "conekta_subscription")
@Getter
@Setter
public class ConektaSubscription extends CoreCatalogEntity {

	private static final long serialVersionUID = -3032596162471882160L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@JoinColumn(name = "conekta_subscription_id", nullable = false)
	private String conektaSubscriptionId;
	
    @ManyToOne
    @JoinColumn(name = "conekta_customer_id", nullable = false)
    private ConektaCustomer conektaCustomer;
    
	@Column(name = "cellphone_number")
	private String cellphoneNumber;
	
    @Column(name = "conekta_status", nullable = false)
    private String conektaStatus;
    
	@Column(name = "conekta_created_at", nullable = false)
	private Integer conektaCreatedAt;
    
	@Column(name = "conekta_plan_id", nullable = false)
	private String conektaPlanId;
	
	@Column(name = "altan_offer_id", nullable = true)
	private String altanOfferId;
    
	@Column(name = "card_id", nullable = false)
	private String cardId;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER,  mappedBy = "conektaSubscription")
	private  List<ConektaSubscriptionEvent> conektaSubscriptionDetails;
	
	
	@Column(name="altan_order_id", nullable = true, length = 50)
	private String altanOrderId;
	
    @Column(name = "activated_altan_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date activatedAltanAt;
    
    
    @Column(name="internal_status", nullable = true)
    private String internalStatus;
    
    @Column(name="internal_operation", nullable = true)
    private String internalOperation;
    
    @Column(name="belongs_to_hbb")
    private boolean belongsToHbb;
    
    @Column(name="coordinates")
    private String coordinates;
    
    @Column(name="super_offer_type")
    private String superOfferType;
    
}
