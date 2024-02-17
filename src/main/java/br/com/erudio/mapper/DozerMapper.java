package br.com.erudio.mapper;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.model.Book;
import br.com.erudio.model.Person;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public class DozerMapper {

    private static ModelMapper mapper = new ModelMapper();

    static {
        mapper.createTypeMap(Person.class, PersonVO.class)
                .addMapping(Person::getId, PersonVO::setKey);
        mapper.createTypeMap(PersonVO.class, Person.class)
                .addMapping(PersonVO::getKey, Person::setId);
        mapper.createTypeMap(Book.class, BookVO.class)
                .addMapping(Book::getId, BookVO::setKey);
        mapper.createTypeMap(BookVO.class, Book.class)
                .addMapping(BookVO::getKey, Book::setId);
    }

    public static <O, D> D parseObject(O origin, Class<D> destination) {
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> originList, Class<D> destination) {
        List<D> destinationObjects = new ArrayList<>();
        for (O origin: originList) {
            destinationObjects.add(mapper.map(origin, destination));

        }

        return destinationObjects;
    }
}
