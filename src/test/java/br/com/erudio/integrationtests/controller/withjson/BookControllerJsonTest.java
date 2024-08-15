package br.com.erudio.integrationtests.controller.withjson;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.testscontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO bookVO;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.findAndRegisterModules();

        bookVO = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin1234");

        var accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .body(bookVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO createdBook = objectMapper.readValue(content, BookVO.class);
        bookVO = createdBook;

        assertNotNull(createdBook);
        assertNotNull(createdBook.getKey());
        assertNotNull(createdBook.getLaunchDate());
        assertEquals("George R.R Martin", createdBook.getAuthor());
        assertEquals("Dança dos Dragões", createdBook.getTitle());
        assertEquals(new BigDecimal("50"), createdBook.getPrice());
    }

    @Test
    @Order(2)
    public void testFindById() throws JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getLaunchDate());
        assertEquals("George R.R Martin", persistedBook.getAuthor());
        assertEquals("Dança dos Dragões", persistedBook.getTitle());
        assertEquals(new BigDecimal("50.00"), persistedBook.getPrice());
    }

    @Test
    @Order(3)
    public void testFindByIdWithoutToken() {
        var specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer ")
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();


        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

    }

    @Test
    @Order(4)
    public void testUpdate() throws JsonProcessingException {
        bookVO.setPrice(new BigDecimal("25"));

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .body(bookVO)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO createdBook = objectMapper.readValue(content, BookVO.class);
        bookVO = createdBook;

        assertNotNull(createdBook);
        assertNotNull(createdBook.getKey());
        assertNotNull(createdBook.getLaunchDate());
        assertEquals("George R.R Martin", createdBook.getAuthor());
        assertEquals("Dança dos Dragões", createdBook.getTitle());
        assertEquals(new BigDecimal("25"), createdBook.getPrice());
    }

    @Test
    @Order(5)
    public void testDelete() {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .pathParam("id", bookVO.getKey())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() throws JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_IURY)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<BookVO> books = objectMapper.readerForListOf(BookVO.class).readValue(content);

        assertNotNull(books);
        assertEquals(15, books.size());
    }

    private void mockBook() {
        bookVO.setAuthor("George R.R Martin");
        bookVO.setLaunchDate(LocalDateTime.now());
        bookVO.setPrice(new BigDecimal("50"));
        bookVO.setTitle("Dança dos Dragões");
    }
}
