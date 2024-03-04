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
@Table(name = "config_paremeter")
@Getter
@Setter
public final class ConfigParameter extends CoreCatalogEntity {

	private static final long serialVersionUID = 3153557060654602415L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "value", nullable = false, length = 200)
	private String value;
	
	@Column(name = "description", length = 100)
	private String description;
}
