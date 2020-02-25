package com.innso.restapi.controller;

import com.innso.restapi.model.Dossier;
import com.innso.restapi.repository.DossierRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class DossierController {

	private final DossierRepository dossierRepository;

	DossierController(DossierRepository dossierRepository) {
		this.dossierRepository = dossierRepository;
	}

	@ApiOperation(value = "liste tous les dossiers", nickname = "listeDossier", notes = "liste tous les dossiers")
	@GetMapping("/dossiers")
	List<Dossier> all() {
		return (List<Dossier>) dossierRepository.findAll();
	}

	@ApiOperation(value = "ajouter un Nouveau Dossier.", nickname = "ajouterDossier", notes = "ajouter un nouveau dossier")
	@PostMapping("/dossiers")
	@ResponseStatus(HttpStatus.CREATED)
	Dossier newDossier(@RequestBody Dossier newDossier) {

		return dossierRepository.save(Dossier.builder().dateOuverture(newDossier.getDateOuverture()).nomClient(newDossier.getNomClient()).reference(newDossier.getReference()).build());
	}

	@ApiOperation(value = "récuperer un Dossier via son id", nickname = "récupererDossier", notes = "récuperer un Dossier via son id")
	@GetMapping("/dossiers/{id}")
	Dossier get(@PathVariable Long id) {

		return dossierRepository.findById(id)
			.orElseGet(() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Dossier non trouvé");
			});
	}

	@ApiOperation(value = "mettre à jour un Dossier.", nickname = "mettreAJourDossier", notes = "mettre à jour un Dossier.")
	@PutMapping("/dossiers/{id}")
	Dossier replaceDossier(@RequestBody Dossier newDossier, @PathVariable Long id) {

		return dossierRepository.findById(id)
			.map(dossier -> {
				dossier.setNomClient(newDossier.getNomClient());
				dossier.setDateOuverture(newDossier.getDateOuverture());
				dossier.setReference(newDossier.getReference());
				return dossierRepository.save(dossier);
			})
			.orElseGet(() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Dossier non trouvé");
			});
	}

	@ApiOperation(value = "supprimer un Dossier.", nickname = "supprimerDossier", notes = "supprimer un Dossier.")
	@DeleteMapping("/dossiers/{id}")
	void deleteDossier(@PathVariable Long id) {
		dossierRepository.deleteById(id);
	}
}



