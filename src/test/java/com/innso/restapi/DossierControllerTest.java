package com.innso.restapi;

import com.innso.restapi.model.Dossier;
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
class DossierControllerTest extends RestApiApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private Dossier dossier;
	private static final HttpHeaders headers = new HttpHeaders();
	private static final String expectedDossier = "{\"dossierID\":1,\"reference\":null,\"nomClient\":\"test nom\",\"dateOuverture\":\"2020-02-24\"}";

	@BeforeEach
	void init() {
		dossier = Dossier.builder().nomClient("test nom").dateOuverture(LocalDate.parse("2020-02-24")).build();
	}

	@Test
	void newDossier() throws Exception {
		ResponseEntity<String> result = createNewDossier();
		assertEquals(expectedDossier, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}

	@Test
	void retrieveDossier() throws Exception {
		String uri = "http://localhost:" + port + "/dossiers/1";
		ResponseEntity<String> result = createNewDossier();

		assertEquals(expectedDossier, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		ResponseEntity<Dossier> getResult = restTemplate.getForEntity(uri, Dossier.class);
		assertDossier(getResult.getBody());
	}

	@Test
	void retrieveALLDossiers() throws Exception {
		String uri = "http://localhost:" + port + "/dossiers";
		ResponseEntity<String> result = createNewDossier();

		assertEquals(expectedDossier, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		ResponseEntity<Dossier[]> getResult = restTemplate.getForEntity(uri, Dossier[].class);
		assertEquals(1, getResult.getBody().length);
		assertDossier(getResult.getBody()[0]);
	}

	@Test
	void modifyDossier() throws Exception {
		String uri = "http://localhost:" + port + "/dossiers/1";
		//Create a new Dossier
		ResponseEntity<String> result = createNewDossier();

		assertEquals(expectedDossier, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		//Fetch the created dossier
		ResponseEntity<Dossier> getResult = restTemplate.getForEntity(uri, Dossier.class);
		Dossier latestCretedDossier = getResult.getBody();
		//modify the created dossier by adding a new reference
		latestCretedDossier.setReference("REF-1");
		HttpEntity<Dossier> request = new HttpEntity<>(latestCretedDossier, headers);
		restTemplate.put(uri, request, Dossier.class);
		ResponseEntity<Dossier> getResultAfterModification = restTemplate.getForEntity(uri, Dossier.class);
		//check the modification
		assertEquals("REF-1", getResultAfterModification.getBody().getReference());
	}

	@Test
	void deleteDossier() throws Exception {
		String uri = "http://localhost:" + port + "/dossiers/1";
		//Create a new Dossier
		ResponseEntity<String> result = createNewDossier();

		assertEquals(expectedDossier, result.getBody());
		assertEquals(HttpStatus.CREATED, result.getStatusCode());

		//delete dossier 1
		restTemplate.delete(uri);
		ResponseEntity<String> getResult = restTemplate.getForEntity(uri, String.class);
		//check that object is null
		assertEquals(HttpStatus.NOT_FOUND, getResult.getStatusCode());
	}

	/**
	 * create a new Dossier
	 *
	 * @return
	 */
	private ResponseEntity<String> createNewDossier() {
		String uri = "http://localhost:" + port + "/dossiers";
		HttpEntity<Dossier> request = new HttpEntity<>(dossier, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
		return result;
	}

	private void assertDossier(Dossier dossier) {
		assertEquals("test nom", dossier.getNomClient());
		assertEquals(LocalDate.parse("2020-02-24"), dossier.getDateOuverture());
	}
}
