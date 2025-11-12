package de.ait.training.model;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(nullable = false) // задать имя в бд <, name = "brand">
    private String model;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private int year;
    @Column(name = "engine_type")
    private String engineType;
    @Column(name = "image_url")
    private String imageUrl;

    public Car(String color, String model, double price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }
}
