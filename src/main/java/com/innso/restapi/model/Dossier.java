package com.innso.restapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Dossier{

	@Id
	@GeneratedValue
	private Long dossierID;
	@ApiModelProperty(value = "Référence du produit", example = "ref-1")
	@Column(unique = true)
	private String reference;
	@ApiModelProperty(value = "Nom du client", example = "nom client", required = true)
	private String nomClient;
	@ApiModelProperty(value = "Date d'ouverture du dossier", example = "2020-01-01", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOuverture;
}
