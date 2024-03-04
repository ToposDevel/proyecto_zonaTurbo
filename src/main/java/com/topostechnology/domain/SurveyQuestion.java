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
@Table(name = "survey_question")
@Getter
@Setter
public class SurveyQuestion extends CoreCatalogEntity {

	private static final long serialVersionUID = 2322361299322749950L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "question", nullable = false, length = 150)
	private String question;

	@ManyToOne
	@JoinColumn(name = "survey_type_id", nullable = false)
	private SurveyType surveyTypeId;

}
