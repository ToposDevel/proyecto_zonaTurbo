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
@Table(name="survey_answer")
@Getter
@Setter
public class SurveyAnswer  extends CoreCatalogEntity {
	
	private static final long serialVersionUID = 2322361299322749950L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="answer", nullable = true, length = 150)
	private String answer;
	
	@ManyToOne
	@JoinColumn(name = "survey_question_id", nullable = false)
	private SurveyQuestion SurveyQuestion;
	
	@ManyToOne
	@JoinColumn(name = "sim_participant_id", nullable = false)
	private PlantingSimParticipant simParticipant;
}
