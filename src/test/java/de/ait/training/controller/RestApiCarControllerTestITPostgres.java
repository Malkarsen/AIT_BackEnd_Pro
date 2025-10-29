package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiCarControllerTestITPostgres {
    @Autowired // чтобы при запуске обратился к Spring Context и достал бин (джава файл)
    TestRestTemplate restTemplate; // объект для отправки запросов на сервер

    @Autowired
    CarRepository repository;

    @Test
    void getAllCarsSuccess() {
        /*
        http-запрос состоит из тела, заголовков, а также имеет тип (метод)
        Тело - содержит информацию, которую мы отправляем на сервер
        (например, объект автомобиля для сохранения в БД)
        Заголовки - содержат служебную информацию о запросе
        (например, информацию об авторизации, куки и др.)
        Тип запроса (метод) - GET, POST, PUT, DELETE

        Создаем объект заголовков запроса
        Хотя нам пока нечего вкладывать в заголовки, их лучше все равно создать,
        хотя бы пустые, потому что некоторые веб-сервера могут вернуть ошибку,
        если в запросе совсем не будет заголовков
         */
        HttpHeaders headers = new HttpHeaders();

        // Создаем объект http-запроса
        // Т.к нам ничего не нужно вкладывать в тело запроса,
        // параметризуем запрос типом void
        HttpEntity<Void> request = new HttpEntity<>(headers); // пустой заголовок

        /*
        Здесь мы отправляем на наше тестовое приложение реальный http-запрос
        и получаем реальный http-ответ. Это и делает метод exchange
        Четыре аргумента метода:
        1) Эндпоинт, на который отправляется запрос
        2) Тип (метод) запроса
        3) Объект запроса (с вложенными в него заголовками и телом)
        4) Тип данных, который мы ожидаем получить с сервера
         */
        // Проблема Iterable<Car>.class в качестве четвертого аргумента не работает,
        // это нарушение синтаксиса.
        // Решение 1: использовать массив Car[]
        ResponseEntity<Car[]> response = restTemplate.exchange(
                "/api/cars", HttpMethod.GET, request, Car[].class
                );

        // Решение 2: преобразовать полученный массив в лист
        List<Car> cars = Arrays.asList(response.getBody());

        // Решение 3: использование класса ParametrizedTypeReference
        // В этом случае никакие преобразования не нужны, сразу получаем список
        ResponseEntity<List<Car[]>> response1 = restTemplate.exchange(
                "/api/cars", HttpMethod.GET, request, new ParameterizedTypeReference<List<Car[]>>(){}
        );

        // Здесь мы проверяем, действительно ли от сервера пришел ответ с правильным статусом
        // ВАЖНО! В метод нужно передавать сначала ожидаемое значение,
        // потом действительное. НЕ НАОБОРОТ!
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Unexpected http status");

        // Получим тело ответа из самого объекта ответа
        Car[] body = response.getBody();

        // Проверяем, а есть ли вообще тело в ответе от сервера
        assertNotNull(body, "Response body should not be null");

        for(Car car : body) {
            assertNotNull(car.getId(), "Car id should not be null");
            assertNotNull(car.getColor(), "Car color should not be null");
            assertNotNull(car.getModel(), "Car model should not be null");
            // Допускаем, что цена может равняться нулю для случая, когда автомобиль
            // ещё не оценен и не выставлен на продажу
            assertTrue(car.getPrice() >= 0, "Car price should be greater than 0");
        }
    }

    @Test
    void postNewCarSuccess() {
        HttpHeaders headers = new HttpHeaders();

        // Поскольку мы тестируем сохранение автомобиля в базу данных, то нам нужно
        // создать тестовый объект, который мы и будем отправлять на сервер
        Car testCar = new Car("Test color", "Test model", 77777.77);

        // В этом случе мы отправляем автомобиль в теле запроса, поэтому
        // сам запрос параметризуем типом Car и вкладываем объект автомобиля
        // в объект запроса
        HttpEntity<Car> request = new HttpEntity<>(testCar, headers);

        ResponseEntity<Car> response = restTemplate.exchange(
                "/api/cars", HttpMethod.POST, request, Car.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Unexpected http status");

        Car savedCar = response.getBody();
        assertNotNull(savedCar, "Saved car should not be null");
        assertNotNull(savedCar.getId(), "Saved car id should not be null");
        assertEquals(testCar.getColor(), savedCar.getColor(), "Saved car color is incorrect");
        assertEquals(savedCar.getModel(), testCar.getModel(), "Saved car model is incorrect");
        assertEquals(savedCar.getPrice(), testCar.getPrice(), "Saved car price is incorrect");
    }

    @Test
    void putCarSuccess() {
        HttpHeaders header = new HttpHeaders();

        Car testCar = new Car("Test color", "Test model", 1000.0);
        long id = 5L;

        HttpEntity<Car> request = new HttpEntity<>(testCar, header);

        // Задание URL
        // Вариант 1: String.format
//        String url = String.format("/api/cars/%d", id);
//        ResponseEntity<Car> response = restTemplate.exchange(
//                url, HttpMethod.PUT, request, Car.class);

        // Вариант 2: Spring Boot переменные
        ResponseEntity<Car> response = restTemplate.exchange(
                "/api/cars/{id}", HttpMethod.PUT, request, Car.class, id
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Unexpected http status");

        Car modifiedCar = response.getBody();
        assertNotNull(modifiedCar, "Modified car car should not be null");
        assertEquals(id, modifiedCar.getId(), "Modified car id is incorrect");
        assertEquals(testCar.getColor(), modifiedCar.getColor(), "Modified car color is incorrect");
        assertEquals(testCar.getModel(), modifiedCar.getModel(), "Modified car model is incorrect");
        assertEquals(testCar.getPrice(), modifiedCar.getPrice(), "Modified car price is incorrect");
    }

}