
package com.topostechnology.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="turbo_office_user")
@Getter
@Setter
public class TurboOfficeUser  extends CoreCatalogEntity {

	private static final long serialVersionUID = -564164749458280560L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "full_name", nullable = false, length = 50)
    private String fullName;
	
    @Column(name = "email", nullable = false, length = 50)
    private String email;
	
	@Column(name = "virtual_number", unique = true, length = 10)
	private String virtualNumber;
	

	
	@Column(name = "associated_number",  unique = true, length = 10)
	private String associatedNumber;
	
	@Column(name = "company")
	private String company;
	
    @Column(name="imei", length = 15)
    private String imei;
    
	@Column(name = "profile", nullable = false, length = 30)
	private String profile;
	
    @Column(name="status")
    private String status;
    
    @Column(name="acrobits_status")
    private String acrobitsStatus;
    
    @Column(name="call_fawarding")
    private int callFowarding;
    
    
    @Column(name = "mobile_install_number", unique = true, length = 10)
    private String mobileInstallNumber;

    
    
}
