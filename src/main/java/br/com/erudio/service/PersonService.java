package br.com.erudio.service;

import br.com.erudio.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(String id) {
        logger.info("Finding one person.");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Iury");
        person.setLastName("Santos");
        person.setAddress("R. Tarrafas - Fortaleza CE");
        person.setGender("Male");

        return person;
    }

    public List<Person> findAll() {
        logger.info("Finding all people.");

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i<8; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }

        return persons;
    }

    public Person create(Person person) {
        logger.info("Create a person");

        return person;
    }

    public Person update(Person person) {
        logger.info("Update a person");

        return person;
    }

    public void delete(String id) {
        logger.info("Delete a person");
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Person " + i);
        person.setLastName("Santos" + i);
        person.setAddress("R. Tarrafas - Fortaleza CE");
        person.setGender("Male");

        return person;
    }

}
