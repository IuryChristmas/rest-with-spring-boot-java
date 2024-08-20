package br.com.erudio.integrationtests.controller.withyml;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyml.mapper.YmlMapper;
import br.com.erudio.integrationtests.testscontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static PersonVO personVO;
	private static YmlMapper objectMapper;

	@BeforeAll
	public static void setup() {
		objectMapper = new YmlMapper();
		personVO = new PersonVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin1234");

		var accessToken = given()
				.config(RestAssuredConfig.config()
				.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
					.statusCode(200)
						.extract()
						.body()
							.as(TokenVO.class, objectMapper)
						.getAccessToken();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonProcessingException {
		mockPerson();

		PersonVO createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.body(personVO, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		personVO = createdPerson;

		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
		assertTrue(createdPerson.getId() > 0);
		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Stallman", createdPerson.getLastName());
		assertEquals("New York City, New York, US", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
	}

	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() {
		mockPerson();

		var content = given()
				.config(RestAssuredConfig.config()
				.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
				.body(personVO, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();

		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonProcessingException {
		mockPerson();

		PersonVO persistedPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.pathParam("id", personVO.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		personVO = persistedPerson;

		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindAll() throws JsonProcessingException {
		var content = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PagedModelPerson.class, objectMapper);

		var people = content.getContent();

		assertNotNull(people);
		assertNotNull(people.get(0).getId());
		assertNotNull(people.get(0).getFirstName());
		assertNotNull(people.get(0).getLastName());
		assertNotNull(people.get(0).getAddress());
		assertNotNull(people.get(0).getGender());
		assertFalse(people.get(0).getEnabled());
		assertTrue(people.get(0).getId() > 0);
		assertEquals("Alvie", people.get(0).getFirstName());
		assertEquals("Veldens", people.get(0).getLastName());
		assertEquals("90 Morningstar Drive", people.get(0).getAddress());
		assertEquals("Male", people.get(0).getGender());

		assertNotNull(people.get(1).getId());
		assertNotNull(people.get(1).getFirstName());
		assertNotNull(people.get(1).getLastName());
		assertNotNull(people.get(1).getAddress());
		assertNotNull(people.get(1).getGender());
		assertEquals("Alvie", people.get(1).getFirstName());
		assertEquals("Stellman", people.get(1).getLastName());
		assertEquals("2460 Dawn Alley", people.get(1).getAddress());
		assertEquals("Male", people.get(1).getGender());
	}

	@Test
	@Order(5)
	public void testUpdate() throws JsonProcessingException {
		mockPerson();
		personVO.setLastName("Stall");

		PersonVO createdPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.body(personVO, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		personVO = createdPerson;

		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
		assertTrue(createdPerson.getId() > 0);
		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Stall", createdPerson.getLastName());
		assertEquals("New York City, New York, US", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
	}

	@Test
	@Order(6)
	public void testDisablePersonyId() throws JsonProcessingException {
		PersonVO persistedPerson = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.pathParam("id", personVO.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		personVO = persistedPerson;

		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

		assertTrue(persistedPerson.getId() > 0);
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stall", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(7)
	public void testDelete() {
		mockPerson();

		given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
				.pathParam("id", personVO.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	private void mockPerson() {
		personVO.setFirstName("Richard");
		personVO.setLastName("Stallman");
		personVO.setAddress("New York City, New York, US");
		personVO.setGender("Male");
		personVO.setEnabled(Boolean.TRUE);
	}

}
