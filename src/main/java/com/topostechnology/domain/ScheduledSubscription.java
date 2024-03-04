package com.topostechnology.domain;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheduled_subscription")
@Getter
@Setter
public class ScheduledSubscription extends CoreCatalogEntity {

	private static final long serialVersionUID = 8353607814691639442L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "conekta_customer_id", nullable = false)
    private ConektaCustomer conektaCustomer;
	
	@ManyToOne
    @JoinColumn(name = "conekta_subscription_id", nullable = true)
    private ConektaSubscription conektaSubscription;
	
	@Column(name = "phone", nullable = false, length = 14)
	private String phone;
	
	@Column(name = "payment_provider", nullable = false, length = 50)
	private String paymentProvider;
	
    @Column(name = "scheduled_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledAt;
    
    @Column(name = "subscription_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionAt;
    
	@Column(name = "conekta_plan_id", nullable = false)
	private String conektaPlanId;
	
	@Column(name = "altan_offer_id", nullable = false)
	private String altanOfferId;
    
	@Column(name = "card_id", nullable = false)
	private String cardId;
	
	@Column(name="internal_status", nullable = true)
    private String internalStatus;
	
    @Column(name="internal_operation", nullable = true)
    private String internalOperation;
    
    @Column(name="belongs_to_hbb")
    private boolean belongsToHbb;
    
    @Column(name="coordinates")
    private String coordinates;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledSubscription)) return false;
        ScheduledSubscription user = (ScheduledSubscription) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
