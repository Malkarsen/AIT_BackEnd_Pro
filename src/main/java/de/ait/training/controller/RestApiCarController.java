package de.ait.training.controller;

import de.ait.training.model.Car;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Cars", description = "Operation on cars")
@Slf4j
@RequestMapping("/api/cars")
@RestController
public class RestApiCarController {
    Car carOne = new Car(1, "Black", "BMW x5", 25000);
    Car carTwo = new Car(2, "Green", "Audi A4", 15000);
    Car carThree = new Car(3, "White", "BMW A220", 18000);
    Car carFour = new Car(4, "Red", "Ferrari", 250000);

    List<Car> cars = new ArrayList<>();

    public RestApiCarController() {
        cars.add(carOne);
        cars.add(carTwo);
        cars.add(carThree);
        cars.add(carFour);
    }

    /**
     * GET /api/cars
     *
     * @return список всех автомобилей
     */
    // GET --> api/cars
    @Operation(
            summary = "Get cars",
            description = "Returns a list of all cars",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful")
            }
    )
    @GetMapping
    Iterable<Car> getCars() {
        return cars;
    }

    /**
     * GET /api/cars/color/{color}
     *
     * @return список всех автомобилей заданного цвета
     */
    @Operation(
            summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/color/{color}")
    ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> filteredCars = cars.stream()
                .filter(car -> car.getColor().equalsIgnoreCase(color))
                .toList();
        if (filteredCars.isEmpty()) {
            log.warn("Code 404 - No cars found for color {}", color);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Code 200 - Cars found for color {}", color);
            return new ResponseEntity<>(filteredCars, HttpStatus.OK);
        }
    }

    /**
     * Создает новый автомобиль и добавляет его в лист
     *
     * @param car данные для новой машины
     * @return созданный автомобиль
     */
    @Operation(
            summary = "Create car",
            description = "Create a new car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created")
            }
    )
    @PostMapping
    Car postCar(@RequestBody Car car) {
        if (car.getId() <= 0) {
            log.error("Code 400 - Car ID must be greater than 0");
            return new Car(9999, "000", "000", 9999);
        }
        cars.add(car);
        log.info("Code 200 - Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если ID не найден, то создаем новый
     *
     * @param id  ID машины, которую нужно изменить
     * @param car новые данные машины
     * @return созданный или найденный автомобиль
     */
    @Operation(
            summary = "Change car",
            description = "Change car data by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful"),
                    @ApiResponse(responseCode = "201", description = "Created")
            }
    )
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        int carIndex = -1;
        for (Car carInList : cars) {
            if (carInList.getId() == id) {
                carIndex = cars.indexOf(carInList);
                cars.set(carIndex, car);
                log.info("Car ID: {} has been updated", carInList.getId());
            }
        }
        return (carIndex == -1)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * Удаляем автомобиль по ID
     *
     * @param id id машины, которую нужно удалить
     */
    @Operation(
            summary = "Delete car",
            description = "Delete car by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful")
            }
    )
    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete Car with ID {}", id);
        cars.removeIf(car -> car.getId() == id);
    }
}
