package com.topostechnology.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -4811648107357178966L;

	public abstract void setId(Long id);

	public abstract Long getId();
}
