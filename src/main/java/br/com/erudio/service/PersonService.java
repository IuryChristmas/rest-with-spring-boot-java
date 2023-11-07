package br.com.erudio.service;

import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonService {

    @Autowired
    PersonRepository repository;

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(Long id) {
        logger.info("Finding one person.");

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    public List<Person> findAll() {
        logger.info("Finding all people.");

        return repository.findAll();
    }

    public Person create(Person person) {
        logger.info("Create a person");

        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Update a person");
        var personFind = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        personFind.setFirstName(person.getFirstName());
        personFind.setLastName(person.getLastName());
        personFind.setAddress(person.getAddress());
        personFind.setGender(person.getGender());

        return repository.save(personFind);
    }

    public void delete(Long id) {
        logger.info("Delete a person");
        var person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(person);
    }

}
