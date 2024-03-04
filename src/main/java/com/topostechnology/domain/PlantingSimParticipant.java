package com.topostechnology.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "planting_sim_participant")
@Getter
@Setter
public class PlantingSimParticipant extends CoreCatalogEntity {


	private static final long serialVersionUID = 9177405749027218498L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "fullName", nullable = false, length = 50)
    private String fullName;
    
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    
	@NotNull
	@Column(name = "cellphone_number", nullable = false, length = 10)
	private String cellphoneNumber;
	
    @Column(name="imei", nullable = true, length = 15)
    private String imei;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "principal_participant_id")
    private PlantingSimParticipant principal;
	
    @OneToMany(mappedBy = "principal", fetch = FetchType.EAGER)
    private List<PlantingSimParticipant> recommendations;
    
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    @Column(name = "sim", nullable = true, length = 10)
    private String sim;
    
    @Column(name = "status", nullable= false, length = 20)
    private String status;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlantingSimParticipant)) return false;
        PlantingSimParticipant user = (PlantingSimParticipant) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
