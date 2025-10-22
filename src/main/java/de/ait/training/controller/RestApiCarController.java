package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
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
    private CarRepository carRepository;

    public RestApiCarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * GET /api/cars
     * Получаем список всех автомобилей
     * (или пустой список, если ничего не найдено)
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
        return carRepository.findAll();
    }

    /**
     * GET /api/cars/color/{color}
     * Получаем список автомобилей с заданным цветом
     * (или пустой список, если ничего не найдено)
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
        List<Car> filteredCars = carRepository.findCarByColorIgnoreCase(color);

        if (filteredCars.isEmpty()) {
            log.warn("Code 404 - No cars found for color {}", color);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Code 200 - {} Cars found for color {}", filteredCars.size(), color);
            return new ResponseEntity<>(filteredCars, HttpStatus.OK);
        }
    }

    /**
     * GET /api/cars/price/between/{min}/{max}
     * Получаем список автомобилей, у которых цена входит в заданный диапазон
     * (или пустой список, если ничего не найдено)
     *
     * @param min минимальная цена
     * @param max максимальная цена
     * @return все автомобили, у которых price находится включительно между min и max
     */
    @Operation(
            summary = "Get cars by price between min and max",
            description = "Returns a list of cars whose price is inclusive between min and max",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
            }
    )
    @GetMapping("/price/between/{min}/{max}")
    ResponseEntity<List<Car>> getCarsByPriceBetween(@PathVariable Double min, @PathVariable Double max) {
        if (min > max) {
            log.error("Code 400 - The max ({}) value must be greater than the min value ({})",
                    max, min);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        List<Car> filteredCars = carRepository.findCarByPriceBetween(min, max);
        if (filteredCars.isEmpty()) {
            log.warn("Code 404 - No cars were found for the range from {} to {}",
                    min, max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            log.info("Code 200 - {} Cars were found for the range from {} to {}",
                    filteredCars.size(), min, max);
            return new ResponseEntity<>(filteredCars, HttpStatus.OK);
        }
    }

    /**
     * GET /api/cars/price/under/{max}
     * Получаем список автомобилей, у которых цена меньше, чем заданная, или равна ей
     * (или пустой список, если ничего не найдено)
     *
     * @param max максимальная цена
     * @return все автомобили, у которых price ≤ max
     */
    @Operation(
            summary = "Get cars by price less than or equal to max",
            description = "Returns a list of cars with price less than or equal to max",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/price/under/{max}")
    ResponseEntity<List<Car>> getCarsByPriceLessThanEqual(@PathVariable Double max) {
        List<Car> filteredCars = carRepository.findCarByPriceLessThanEqual(max);

        if (filteredCars.isEmpty()) {
            log.warn("Code 404 - No cars with a price less than or equal to {} were found",
                    max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            log.info("Code 200 - {} Cars with a price less than or equal to {} were found",
                    filteredCars.size(), max);
            return new ResponseEntity<>(filteredCars, HttpStatus.OK);
        }
    }

    /**
     * GET /api/cars/price/between/over/{min}
     * Получаем список автомобилей, у которых цена больше, чем заданная, или равна ей
     * (или пустой список, если ничего не найдено)
     *
     * @param min минимальная цена
     * @return все автомобили, у которых price ≥ min
     */
    @Operation(
            summary = "Get cars by price greater than or equal to min",
            description = "Returns a list of cars with price greater than or equal to min",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/price/over/{min}")
    ResponseEntity<List<Car>> getCarsByPriceGreaterThanEqual(@PathVariable Double min) {
        List<Car> filteredCars = carRepository.findCarByPriceGreaterThanEqual(min);

        if (filteredCars.isEmpty()) {
            log.warn("Code 404 - No cars with a price greater than or equal to {} were found",
                    min);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            log.info("Code 200 - {} Cars with a price greater than or equal to {} were found",
                    filteredCars.size(), min);
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
            return new Car("000", "000", 9999.0);
        }
        carRepository.save(car);
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
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        Car foundCar = carRepository.findById(id).orElse(null);

        if (foundCar == null) {
            log.info("Code 404 - Car not found for id {}", id);
        } else {
            log.info("Code 200 - Car found for id {}", id);
            carRepository.save(car);
        }

        return (foundCar == null)
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
        carRepository.deleteById(id);
    }
}
