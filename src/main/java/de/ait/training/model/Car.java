package de.ait.training.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // стратегия получения - авто-инкремент
    private Long id;

    @Column(nullable = false) // при попытке сохранить Null будет ошибка
    private String color;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private Double price;

    public Car(String color, String model, Double price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }
}
