package com.topostechnology.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="virtual_number")
@Getter
@Setter
public class VirtualNumber  extends CoreCatalogEntity {

	private static final long serialVersionUID = -564164749458280560L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "number", nullable = false, unique = true, length = 10)
	private String number;

    @Column(name="status")
    private String status;

	@Column(name = "turbo_office_user_id")
	private String turboOfficeUserId;
    
    /*
    @Column(name="turbo_office_user_id")
    private TurboOfficeUser turboOfficeUser;
    */
    
	@Column(name = "associated_number_temp", length = 10)
	private String associatedNumberTemp;
	
	@Column(name = "frozen_at")
	private Date frozenAt;
	
	
    
}
