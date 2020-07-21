package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnPersonDetails() {
        assertThat(
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/person/smith/mary",
                        String.class
                )
        ).contains("Mary");
    }

    @Test
    public void shouldReturnNotFoundWhenPersonNotFound() {
        Map entity = this.restTemplate.getForObject("http://localhost:" + port + "/person/kmd/trn", Map.class);
        assertNotNull(entity);
        assertEquals(404, entity.get("status"));
        assertEquals("Entity not found", entity.get("message"));
    }

    @Test
    public void shouldReturnMultipleResultWhileSearchBySurname() {
        Person[] personList = this.restTemplate.getForObject("http://localhost:" + port + "/person/smith", Person[].class);
        assertNotNull(personList);
        assertTrue(personList.length > 0);
        assertFalse(Arrays.stream(personList).anyMatch(t -> !t.getLastName().equalsIgnoreCase("smith")));
    }

    @Test
    public void shouldReturnEmptyArrayIfNotFoundAnyWhileSearchBySurname() {
        Person[] personList = this.restTemplate.getForObject("http://localhost:" + port + "/person/xyz", Person[].class);
        assertNotNull(personList);
        assertEquals(0, personList.length);
    }

    @Test
    public void shouldCreateNewPerson() {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", "Marvel");
        map.put("lastName", "Bryn");
        HttpEntity<Map> request = new HttpEntity<>(map);
        ResponseEntity<Person> response = restTemplate
                .exchange("http://localhost:" + port + "/person", HttpMethod.POST, request, Person.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Marvel", response.getBody().getFirstName());
        assertEquals("Bryn", response.getBody().getLastName());
    }

    @Test
    public void shouldReturnBadRequestWhenPersonAlreadyExist() {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", "Mary");
        map.put("lastName", "Smith");
        HttpEntity<Map> request = new HttpEntity<>(map);
        ResponseEntity<Map> response = restTemplate
                .exchange("http://localhost:" + port + "/person", HttpMethod.POST, request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Entity already exists", response.getBody().get("message"));
    }
}