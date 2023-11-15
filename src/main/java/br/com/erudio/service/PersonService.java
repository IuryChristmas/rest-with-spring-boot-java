package br.com.erudio.service;

import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.mapper.custom.PersonMapper;
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

    @Autowired
    PersonMapper personMapper;

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    public PersonVO findById(Long id) {
        logger.info("Finding one person.");

        var entity =  repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public List<PersonVO> findAll() {
        logger.info("Finding all people.");

        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    public PersonVO create(PersonVO person) {
        logger.info("Create a person");

        var entity = DozerMapper.parseObject(person, Person.class);
        return DozerMapper.parseObject(repository.save(entity), PersonVO.class);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Update a person");
        var personFind = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        personFind.setFirstName(person.getFirstName());
        personFind.setLastName(person.getLastName());
        personFind.setAddress(person.getAddress());
        personFind.setGender(person.getGender());

        return DozerMapper.parseObject(repository.save(personFind), PersonVO.class);
    }

    public void delete(Long id) {
        logger.info("Delete a person");
        var person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(person);
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        logger.info("Create a person v2");

        var entity = personMapper.convertVoToEntity(person);
        return personMapper.convertEntityToVo(repository.save(entity));
    }
}
