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
@Table(name = "planting_sim")
@Getter
@Setter
public class PlantingSim extends CoreCatalogEntity {

	private static final long serialVersionUID = 5274850430647381683L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "cellphone_number", nullable = false, length = 10)
	private String cellphoneNumber;
	
	@Column(name = "iccid", nullable = false, length = 25)
	private String iccid;
	
	@Column(name="status", nullable = true, length = 10)
	private String status;
	
	@Column(name="orderId", nullable = true, length = 50)
	private String activationOrderId;
}
