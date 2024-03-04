package com.topostechnology.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class CoreCatalogEntity extends TrackableEntity {
	private static final long serialVersionUID = 6492630132389838148L;
	private boolean active;
}
