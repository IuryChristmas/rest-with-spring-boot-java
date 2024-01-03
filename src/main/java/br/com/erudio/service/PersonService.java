package br.com.erudio.service;

import br.com.erudio.controller.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    @Autowired
    PersonRepository repository;

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    public PersonVO findById(Long id) {
        logger.info("Finding one person.");

        var entity =  repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public List<PersonVO> findAll() {
        logger.info("Finding all people.");

        var persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
        persons.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return persons;
    }

    public PersonVO create(PersonVO person) {
        logger.info("Create a person");

        var entity = DozerMapper.parseObject(person, Person.class);

        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        logger.info("Update a person");
        var personFind = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        personFind.setFirstName(person.getFirstName());
        personFind.setLastName(person.getLastName());
        personFind.setAddress(person.getAddress());
        personFind.setGender(person.getGender());

        PersonVO vo = DozerMapper.parseObject(repository.save(personFind), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Delete a person");
        var person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(person);
    }

}
