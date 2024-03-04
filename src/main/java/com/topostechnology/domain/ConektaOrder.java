package com.topostechnology.domain;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "conekta_order")
@Getter
@Setter
public class ConektaOrder extends CoreCatalogEntity {

	private static final long serialVersionUID = 8353607814691639442L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "conekta_order_id", nullable = false, length = 300)
	private String conektaOrderId;
	
	public Integer amount;
	
	@Column(name = "conekta_customer_id", nullable = true, length = 30)
	private String conektaCustomerId;
	
	@Column(name = "name", nullable = false, length = 30)
    private String name;
	
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    
	@Column(name = "phone", nullable = false, length = 14)
	private String phone;
	
	@Column(name = "conekta_created_at", nullable = false)
	public Integer conektaCreatedAt;
	
	@Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;
	
	@Column(name = "conekta_plan_id", nullable = true)
	private String conektaPlanId;
	
	@Column(name = "altan_offer_id", nullable = false)
	private String altanOfferId;
	
	@Column(name="altan_order_id", nullable = true, length = 50)
	private String altanOrderId;
    
    @Column(name = "purchase_altan_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseAltanAt;
    
    
    @Column(name="internal_status", nullable = true)
    private String internalStatus;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConektaOrder)) return false;
        ConektaOrder user = (ConektaOrder) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
