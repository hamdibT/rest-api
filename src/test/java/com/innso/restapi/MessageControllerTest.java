package com.innso.restapi;

import com.innso.restapi.model.Dossier;
import com.innso.restapi.model.Message;
import com.innso.restapi.utils.Canal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MessageControllerTest extends RestApiApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private Message message;
	private static final HttpHeaders headers = new HttpHeaders();
	private static final String expectedMessage = "{\"id\":1,\"dateCreation\":\"2020-02-24\",\"nomAuteur\":\"test nom\",\"message\":\"message de test\",\"canal\":\"FACEBOOK\",\"dossier\":null}";

	@BeforeEach
	void init() {
		message = Message.builder().nomAuteur("test nom").dateCreation(LocalDate.parse("2020-02-24")).message("message de test").canal(Canal.FACEBOOK).build();
	}

	@Test
	void newMessage() throws Exception {
		ResponseEntity<String> result = createNewMessage();
		assertEquals(expectedMessage, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}

	@Test
	void newMessageWithDossier() throws Exception {
		Dossier dossier = Dossier.builder().nomClient("test nom").dateOuverture(LocalDate.parse("2020-02-24")).build();
		String uriDossier = "http://localhost:" + port + "/dossiers";
		HttpEntity<Dossier> request = new HttpEntity<>(dossier, headers);
		ResponseEntity<Dossier> dossierResponseEntity = restTemplate.postForEntity(uriDossier, request, Dossier.class);
		assertEquals(HttpStatus.CREATED, dossierResponseEntity.getStatusCode());

		//Assign dossier to message
		message.setDossier(dossierResponseEntity.getBody());
		ResponseEntity<String> result = createNewMessage();
		assertEquals(HttpStatus.CREATED, result.getStatusCode());

		String uri = "http://localhost:" + port + "/messages/2";
		ResponseEntity<Message> getResult = restTemplate.getForEntity(uri, Message.class);
		final Message createdMessage = getResult.getBody();
		assertMessage(createdMessage);
		assertEquals(1, createdMessage.getDossier().getDossierID());
	}

	@Test
	void retrieveMessage() throws Exception {
		String uri = "http://localhost:" + port + "/messages/1";
		ResponseEntity<String> result = createNewMessage();

		assertEquals(expectedMessage, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		ResponseEntity<Message> getResult = restTemplate.getForEntity(uri, Message.class);
		assertMessage(getResult.getBody());
	}

	@Test
	void retrieveALLMessages() throws Exception {
		String uri = "http://localhost:" + port + "/messages";
		ResponseEntity<String> result = createNewMessage();
		assertEquals(expectedMessage, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		ResponseEntity<Message[]> getResult = restTemplate.getForEntity(uri, Message[].class);
		assertEquals(1, getResult.getBody().length);
		assertMessage(getResult.getBody()[0]);
	}

	@Test
	void modifyMessage() throws Exception {
		String uri = "http://localhost:" + port + "/messages/1";
		//Create a new Message
		ResponseEntity<String> result = createNewMessage();
		assertEquals(expectedMessage, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		//Fetch the created message
		ResponseEntity<Message> getResult = restTemplate.getForEntity(uri, Message.class);
		Message latestCretedMessage = getResult.getBody();
		//modify the created message by changing the CANAL
		latestCretedMessage.setCanal(Canal.TWITTER);
		HttpEntity<Message> request = new HttpEntity<>(latestCretedMessage, headers);
		restTemplate.put(uri, request, Message.class);
		ResponseEntity<Message> getResultAfterModification = restTemplate.getForEntity(uri, Message.class);
		//check the modification
		assertEquals(Canal.TWITTER, getResultAfterModification.getBody().getCanal());
	}

	/**
	 * attach existing Dossier to message
	 *
	 * @throws Exception
	 */
	@Test
	void attachMessageToDossier() throws Exception {
		//Create a new Dossier
		Dossier dossier = Dossier.builder().nomClient("test nom").dateOuverture(LocalDate.parse("2020-02-24")).build();
		String uriDossier = "http://localhost:" + port + "/dossiers";
		HttpEntity<Dossier> request = new HttpEntity<>(dossier, headers);
		ResponseEntity<Dossier> dossierResponseEntity = restTemplate.postForEntity(uriDossier, request, Dossier.class);
		assertEquals(HttpStatus.CREATED, dossierResponseEntity.getStatusCode());

		String uri = "http://localhost:" + port + "/messages/2";
		//Create a new Message
		ResponseEntity<String> result = createNewMessage();
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		//Fetch the created message
		ResponseEntity<Message> getResult = restTemplate.getForEntity(uri, Message.class);
		Message latestCretedMessage = getResult.getBody();

		latestCretedMessage.setDossier(dossierResponseEntity.getBody());
		//modify the created message by attaching it to dossier
		HttpEntity<Message> modificationRequest = new HttpEntity<>(latestCretedMessage, headers);
		restTemplate.put(uri, modificationRequest, Message.class);
		ResponseEntity<Message> getResultAfterModification = restTemplate.getForEntity(uri, Message.class);
		//check that the dossier is linked to the message
		assertEquals(1, getResultAfterModification.getBody().getDossier().getDossierID());
	}

	/**
	 * delete an existing message
	 *
	 * @throws Exception
	 */
	@Test
	void deleteMessage() throws Exception {
		String uri = "http://localhost:" + port + "/messages/1";
		//Create a new Message
		ResponseEntity<String> result = createNewMessage();

		assertEquals(expectedMessage, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());

		//delete message 1
		restTemplate.delete(uri);
		ResponseEntity<String> getResult = restTemplate.getForEntity(uri, String.class);
		//check that object is null
		assertEquals(HttpStatus.NOT_FOUND, getResult.getStatusCode());
	}

	/**
	 * create a new Message
	 *
	 * @return
	 */
	private ResponseEntity<String> createNewMessage() {
		String uri = "http://localhost:" + port + "/messages";
		HttpEntity<Message> request = new HttpEntity<>(message, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
		return result;
	}

	private void assertMessage(Message message) {
		assertEquals("test nom", message.getNomAuteur());
		assertEquals(LocalDate.parse("2020-02-24"), message.getDateCreation());
	}
}
