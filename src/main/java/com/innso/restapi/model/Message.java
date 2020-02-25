package com.innso.restapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innso.restapi.utils.Canal;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Message {

	private @Id	@GeneratedValue	Long id;
	@ApiModelProperty(value = "Date de création du message", example = "2020-01-01", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateCreation;
	@ApiModelProperty(value = "Nom auteur", example = "nom auteur", required = true)
	private String nomAuteur;
	@ApiModelProperty(value = "message", example = "ceci est un message", required = true)
	@Lob private String message;
	@ApiModelProperty(value = "Type de Canal", example = "MAIL", required = true)
	private Canal canal;
	@ApiModelProperty(value = "Référence du dossier")
	@ManyToOne
	private  Dossier dossier;
}
