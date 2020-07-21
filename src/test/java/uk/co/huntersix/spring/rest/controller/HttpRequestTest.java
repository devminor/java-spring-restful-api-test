package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.Arrays;
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
    public void shouldReturnPersonDetails() throws Exception {
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
    public void shouldReturnMultipleResultWhileSearchBySurname()  {
        Person[] personList = this.restTemplate.getForObject("http://localhost:" + port + "/person/smith", Person[].class);
        assertNotNull(personList);
        assertThat(personList.length > 0);
        assertFalse(Arrays.stream(personList).filter(t -> !t.getLastName().equalsIgnoreCase("smith")).findAny().isPresent());
    }

    @Test
    public void shouldReturnEmptyArrayIfNotFoundAnyWhileSearchBySurname()  {
        Person[] personList = this.restTemplate.getForObject("http://localhost:" + port + "/person/xyz", Person[].class);
        assertNotNull(personList);
        assertThat(personList.length == 0);
    }
}