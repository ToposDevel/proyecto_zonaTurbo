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
@Table(name = "phone")
@Getter
@Setter
public class Phone extends CoreCatalogEntity {
	
	private static final long serialVersionUID = -8077551685501630224L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "cellphone_number")
	private String cellphoneNumber;
	
	@Column(name="MAILBOX")
	private boolean mailbox;
	
	@Column(name="CLASS_TYPE")
	private Short classType; 
	
	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
    @Column(name="imei")
    private String imei;

}
