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
     * @return возвращает список всех автомобилей
     */
    // GET --> api/cars
    @GetMapping
    Iterable<Car> getCars() {
        return cars;
    }

    /**
     * Создает новый автомобиль и добавляет его в лист
     *
     * @param car
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
            log.error("Car ID must be greater than 0");
            Car errorCar = new Car(9999, "000", "000", 9999);
            return errorCar;
        }
        cars.add(car);
        log.info("Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден, то создаем новый
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        int carIndex = -1;
        for (Car carInList : cars) {
            if (carInList.getId() == id) {
                carIndex = cars.indexOf(carInList);
                cars.set(carIndex, car);
                log.info("Car ID: " + carInList.getId() + " has been updated");
            }
        }
        return (carIndex == -1)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * Удаляем автомобиль по id
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete Car with ID {}", id);
        cars.removeIf(car -> car.getId() == id);
    }
}
