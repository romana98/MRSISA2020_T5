package com.project.tim05.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="nurses")
@OnDelete(action = OnDeleteAction.CASCADE)
public class Nurse extends MedicalStaff {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clinic", referencedColumnName = "clinic_id", nullable = true)
	private Clinic clinic;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="nurse")
	private Set<Medicine> medicines = new HashSet<Medicine>();
   
	public Nurse() {
		super();
	}

	public Set<Medicine> getMedicines() {
		return medicines;
	}

	public void setMedicines(Set<Medicine> medicines) {
		this.medicines = medicines;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	
}