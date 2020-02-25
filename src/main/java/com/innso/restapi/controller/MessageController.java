package com.innso.restapi.controller;

import com.innso.restapi.model.Message;
import com.innso.restapi.repository.DossierRepository;
import com.innso.restapi.repository.MessageRepository;
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
import java.util.Objects;

@RestController
public class MessageController {

	private final MessageRepository repository;
	private final DossierRepository dossierRepository;

	MessageController(MessageRepository repository, DossierRepository dossierRepository) {
		this.repository = repository;
		this.dossierRepository = dossierRepository;
	}

	@ApiOperation(value = "lister tous les messages", nickname = "listeMessage", notes = "liste tous les messages")
	@GetMapping("/messages")
	List<Message> all() {
		return (List<Message>) repository.findAll();
	}

	@ApiOperation(value = "Ajouter un nouveau message", nickname = "ajouterMessage", notes = "Ajouter un nouveau message")
	@PostMapping("/messages")
	@ResponseStatus(HttpStatus.CREATED)
	Message newMessage(@RequestBody Message newMessage) {
		if (Objects.nonNull(newMessage.getDossier()) && Objects.nonNull(newMessage.getDossier().getDossierID())) {
			return dossierRepository.findById(newMessage.getDossier().getDossierID())
				.map(dossier -> {
					newMessage.setDossier(dossier);
					return repository.save(newMessage);
				}).orElseGet(() -> {
					throw new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Dossier associé non trouvé");
				});
		}
		return repository.save(newMessage);
	}

	@ApiOperation(value = "récuperer un message via son id", nickname = "récupererMessage", notes = "récuperer un Message via son id")
	@GetMapping("/messages/{id}")
	Message get(@PathVariable Long id) {
		return repository.findById(id)
			.orElseGet(() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Message non trouvé");
			});
	}

	@ApiOperation(value = "mettre à jour un Message.", nickname = "mettreAJourMessage", notes = "mettre à jour un Message.")
	@PutMapping("/messages/{id}")
	Message replaceMessage(@RequestBody Message newMessage, @PathVariable Long id) {
		return repository.findById(id)
			.map(message -> {
				final Message.MessageBuilder messageBuilder = Message.builder().id(message.getId()).canal(newMessage.getCanal()).dateCreation(newMessage.getDateCreation()).nomAuteur(newMessage.getNomAuteur()).message((newMessage.getMessage()));

				if (Objects.nonNull(newMessage.getDossier()) && Objects.nonNull(newMessage.getDossier().getDossierID())) {
					return dossierRepository.findById(newMessage.getDossier().getDossierID())
						.map(dossier -> {
							messageBuilder.dossier(dossier);
							return repository.save(messageBuilder.build());
						}).orElseGet(() -> {
							throw new ResponseStatusException(
								HttpStatus.NOT_FOUND, "Dossier associé non trouvé");
						});
				}
				return repository.save(messageBuilder.build());
			})
			.orElseGet(() -> {
				throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Message non trouvé");
			});
	}

	@ApiOperation(value = "supprimer un Message.", nickname = "supprimerMessage", notes = "supprimer un Message.")
	@DeleteMapping("/messages/{id}")
	void deleteMessage(@PathVariable Long id) {
		repository.deleteById(id);
	}
}



