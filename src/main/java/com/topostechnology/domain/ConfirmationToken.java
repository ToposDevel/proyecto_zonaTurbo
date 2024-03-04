package com.topostechnology.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "token_confirmation")
public class ConfirmationToken extends CoreCatalogEntity  {
	
	private static final long serialVersionUID = 7958269431223068245L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name="confirmation_token")
	private String confirmationToken;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
	
	public ConfirmationToken() {
	}
	
	public ConfirmationToken(User user) {
		this.user = user;
		this.setCreatedAt(new Date());
		this.setActive(true);
		confirmationToken = UUID.randomUUID().toString();
	}

}
