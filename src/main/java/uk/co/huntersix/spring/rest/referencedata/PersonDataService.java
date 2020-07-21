package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.exception.EntityAlreadyExistException;
import uk.co.huntersix.spring.rest.exception.EntityNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    public static final List<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
            new Person("Mary", "Smith"),
            new Person("Brian", "Archer"),
            new Person("Collin", "Brown")
    ));

    public Person findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .findFirst().orElseThrow(EntityNotFoundException::new)
                ;
    }

    public List<Person> findPerson(String lastName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public Person addPerson(Person p) {
        PERSON_DATA.stream()
                .filter(x -> x.getFirstName().equalsIgnoreCase(p.getFirstName())
                        && x.getLastName().equalsIgnoreCase(p.getLastName()))
                .findAny().ifPresent(s -> {
            throw new EntityAlreadyExistException();
        });

        PERSON_DATA.add(p);
        return p;
    }
}
