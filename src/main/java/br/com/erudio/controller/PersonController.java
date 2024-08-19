package br.com.erudio.controller;

import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.service.PersonService;
import br.com.erudio.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for managing people")
public class PersonController {

    @Autowired
    PersonService service;

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML})
    @Operation(summary = "Find a person", description = "Find a person", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public PersonVO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML})
    @Operation(summary = "Find all people", description = "Find all people", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class)))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "13") Integer size,
                                                                    @RequestParam(value = "direction", defaultValue = "asc") String direction
                                                  ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping(produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML},
            consumes = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML})
    @Operation(summary = "Adds a new person", description = "Adds a new person", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public PersonVO create(@RequestBody PersonVO person) {
        return service.create(person);
    }

    @PutMapping(produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML})
    @Operation(summary = "Updates a person", description = "Updates a person", tags = {"People"}, responses = {
            @ApiResponse(description = "Update", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public PersonVO update(@RequestBody PersonVO person) {
        return service.update(person);
    }

    @PatchMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML})
    @Operation(summary = "Disable a specific person by your ID", description = "Disable a specific person by your ID", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public PersonVO disablePerson(@PathVariable(value = "id") Long id) {
        return service.disablePerson(id);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a person", description = "Delete a person", tags = {"People"}, responses = {
            @ApiResponse(description = "No content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
    })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
