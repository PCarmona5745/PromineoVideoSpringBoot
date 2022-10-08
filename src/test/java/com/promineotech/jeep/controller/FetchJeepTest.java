package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.controller.support.FetchJeepTestSuport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;


class FetchJeepTest extends FetchJeepTestSuport {



  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
  @ActiveProfiles("test") // creates a new profile for testing so it looks for application-test.yml
  @Sql(
      scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
          "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
      config = @SqlConfig(encoding = "utf-8"))
  class TestsThatDoNotPolluteTheApplicationContext extends FetchJeepTestSuport {

    @Test
    void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
      // Given: a valid model, trim and URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Sport";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);

      // When: a connection is made to the URI
      ResponseEntity<List<Jeep>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null,
          new ParameterizedTypeReference<>() {});

      // Then: a success (OK - 200) status code is returned
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      // And: the actual list returned is the same as the expected list
      List<Jeep> actual = response.getBody();

      List<Jeep> expected = buildExpected();

      actual.forEach(jeep -> jeep.setModelPK(null));

      assertThat(actual).isEqualTo(expected);

    }

    @Test
    void testThatAnErrorMessageIsReturnedWhenAnUnknownTrimIsSupplied() {
      // Given: a valid model, trim and URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Unknown Value";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);

      // When: a connection is made to the URI
      ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          null, new ParameterizedTypeReference<>() {});

      // Then: a not found (404) status code is returned
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

      // And: an error message is returned

      Map<String, Object> error = response.getBody();

      assertErrorMessageValid(error, HttpStatus.NOT_FOUND);

    }

    @ParameterizedTest
    @MethodSource("com.promineotech.jeep.controller.FetchJeepTest#parametersForInvalidInput")
    void testThatAnErrorMessageIsReturnedWhenAnInvalidValueIsSupplied(String model, String trim,
        String reason) {
      // Given: a valid model, trim and URI

      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);

      // When: a connection is made to the URI
      ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          null, new ParameterizedTypeReference<>() {});

      // Then: a not found (404) status code is returned
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

      // And: an error message is returned

      Map<String, Object> error = response.getBody();

      assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);

    }
  }

  // returns a stream of arguments to pass into the test method
  static Stream<Arguments> parametersForInvalidInput() {
    //@formatter:off
    return Stream.of(
        arguments("WRANGLER", "@@#%$^@#", "Trim contains non-alpha-numeric characters"),
        arguments("WRANGLER", "C".repeat(Constants.TRIM_MAX_LENGTH+1), "Trim length too long"),
        arguments("INVALID", "Sport", "Model is not enum value")
        );
    //@formatter:on
  }



  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
  @ActiveProfiles("test") // creates a new profile for testing so it looks for application-test.yml
  @Sql(
      scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
          "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
      config = @SqlConfig(encoding = "utf-8"))
  class TestsThatPolluteTheApplicationContext extends FetchJeepTestSuport {
    
    //mock object
    @MockBean
    private JeepSalesService jeepSalesService;
    
    @Test
    void testThatAnUnplannedErrorResultsInA500Status() {
      // Given:
      JeepModel model = JeepModel.WRANGLER;
      String trim = "invalid";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);

      
      //mock makes the jeep sales service throw an exception when a model/trim is fetched
      doThrow(new RuntimeException("Ouch!")).when(jeepSalesService).fetchJeeps(model,trim);
      
      // when:
      ResponseEntity<Map<String,Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
      

      // Then:
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      
      //and:
      Map<String, Object> error = response.getBody();
      
      assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }



  }



}
