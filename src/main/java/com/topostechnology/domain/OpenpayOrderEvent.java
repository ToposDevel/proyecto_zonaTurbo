package com.topostechnology.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "openpay_order_event")
@Getter
@Setter
public class OpenpayOrderEvent extends CoreCatalogEntity {

	private static final long serialVersionUID = -2417102013225568920L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="transaction_id", nullable = true)
    private String transactionId;
	
	@Column(name="event_type")
    private String eventType;
	
	@Column(name="status")
    private String status;

	@Column(name="event_date")
	private String eventDate;
	
	@Column(name="operation_date")
	private Date operationDate;
	
	@Column(name="payment_method")
	private String paymentMethod;
	
	@Column(name="error_message")
	private String errorMessage;
	
	@Column(name="amount")
	private BigDecimal amount;
	
	@Column(name="order_id")
	private String orderId;
	

	
	
	
}
