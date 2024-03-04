package com.topostechnology.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "altan_action_request")
@Getter
@Setter
public class AltanActionRequest extends CoreCatalogEntity {
	
	private static final long serialVersionUID = -8077551685501630224L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "cellphone_number")
	private String cellphoneNumber;
	
	@Column(name = "json_request", length = 300)
	private String jsonRequest;
	
	@Column(name = "json_response", length = 300)
	private String jsonResponse;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "origin_request")
	private String originRequest;
	
	@Column(name = "url")
	private String url;

	

}
