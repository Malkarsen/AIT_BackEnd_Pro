package de.ait.training.controller;

import de.ait.training.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestApiCarControllerIT { // IT - интеграционный тест
    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    //-----------------------------------------------------------------------
    /**
     * GET /api/cars
     */
    @Test
    @DisplayName("Get cars, 4 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCars_shouldReturnAllCars_whenDatabaseIsNotEmpty() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                "/api/cars"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).hasSize(4);
        assertThat(cars.getFirst().getModel()).isEqualTo("BMW x5");
    }

    @Test
    @DisplayName("Get cars, 0 cars found, status Ok")
    @Sql(scripts = "classpath:sql/clear.sql")
    void testGetCars_shouldReturnEmptyList_whenNoCarsInDatabase() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * GET /api/cars/color/{color}
     */
    @Test
    @DisplayName("Get cars by color red, 1 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByColor_shouldReturnCarsByColor_whenColorExists() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/color/red"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).hasSize(1);
        assertThat(cars.getFirst().getModel()).isEqualTo("Ferrari");
    }

    @Test
    @DisplayName("Get cars by color ReD, 1 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByColor_shouldReturnCarsByColorIgnoringCase_whenColorExists() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/color/ReD"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).hasSize(1);
        assertThat(cars.getFirst().getModel()).isEqualTo("Ferrari");
    }

    @Test
    @DisplayName("Get cars by color purple, 0 cars found, status NotFound")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByColor_shouldReturnEmptyList_whenColorDoesNotExist() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/color/purple"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(cars).isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * GET /api/cars/price/between/{min}/{max}
     */
    @Test
    @DisplayName("Price between 10000 and 30000, 3 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceBetween10000And30000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                "/api/cars/price/between/10000/30000"),
                Car[].class);

//        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars).hasSize(3);
        assertThat(cars.getFirst().getModel()).isEqualTo("BMW x5");
    }

    @Test
    @DisplayName("Price between 30000 and 10000, 0 cars found, status BadRequest")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceBetween30000And10000Fail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                        "/api/cars/price/between/30000/10000"),
                Car[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars).isEmpty();
    }

    @Test
    @DisplayName("Price between 100 and 500, 0 cars found, status NotFound")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByPriceBetween_shouldReturnNotFound_whenPriceBetween100And500() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                "/api/cars/price/between/100/500"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(cars).isEmpty();
    }

    @Test
    @DisplayName("Price between 15000 and 18000, 2 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByPriceBetween_shouldReturnCarsIncludesBoundaryPrices_whenPriceBetween15000And18000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                "/api/cars/price/between/15000/18000"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).hasSize(2);
        assertThat(cars.getFirst().getModel()).isEqualTo("Audi A4");
        assertThat(cars.get(1).getModel()).isEqualTo("MB A220");
    }

    //-----------------------------------------------------------------------
    /**
     * GET /api/cars/price/under/{max}
     */
    @Test
    @DisplayName("Price less than 16000, 1 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceUnder16000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url(
                "/api/cars/price/under/16000"),
                Car[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars).hasSize(1);
        assertThat(cars.getFirst().getModel()).isEqualTo("Audi A4");
    }

    @Test
    @DisplayName("Price less than 1000, 0 cars found, status NotFound")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByPriceUnder_shouldReturnsNotFound_whenPriceUnder1000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/under/1000"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(cars).isEmpty();
    }

    //-----------------------------------------------------------------------
    /**
     * GET /api/cars/price/between/over/{min}
     */
    @Test
    @DisplayName("Price greater than 25000, 2 cars found, status Ok")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByPriceOver_shouldReturnsCars_whenPriceOver25000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/over/25000"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cars).hasSize(2);
        assertThat(cars.getFirst().getModel()).isEqualTo("BMW x5");
    }

    @Test
    @DisplayName("Price greater than 1 000 000, 0 cars found, status NotFound")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testGetCarsByPriceOver_shouldReturnsNotFound_whenPriceOver1000000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/over/1000000"),
                Car[].class);
        assertNotNull(response.getBody());
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(cars).isEmpty();
    }
}
