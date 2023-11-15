package br.com.erudio.mapper.custom;

import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.model.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {

    public PersonVOV2 convertEntityToVo(Person person) {
        PersonVOV2 vo = new PersonVOV2();
        vo.setId(person.getId());
        vo.setAddress(person.getAddress());
        vo.setGender(person.getGender());
        vo.setFirstName(person.getFirstName());
        vo.setLastName(person.getLastName());
        vo.setBirthDate(new Date());
        return vo;
    }

    public Person convertVoToEntity(PersonVOV2 personVo) {
        Person person = new Person();
        person.setId(personVo.getId());
        person.setAddress(personVo.getAddress());
        person.setGender(personVo.getGender());
        person.setFirstName(personVo.getFirstName());
        person.setLastName(personVo.getLastName());
        return person;
    }

}
