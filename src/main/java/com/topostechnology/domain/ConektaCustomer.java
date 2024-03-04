package com.topostechnology.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "conekta_subscription_customer")
@Getter
@Setter
public class ConektaCustomer extends CoreCatalogEntity {

	private static final long serialVersionUID = 8353607814691639442L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "conekta_customer_id", nullable = false, length = 30)
	private String conektaCustomerId;
	
	@Column(name = "name", nullable = false, length = 30)
    private String name;
	
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    
	@Column(name = "phone", nullable = false, length = 14)
	private String phone;
	
	@Column(name = "conekta_created_at", nullable = false)
	public Integer conektaCreatedAt;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConektaCustomer)) return false;
        ConektaCustomer user = (ConektaCustomer) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
