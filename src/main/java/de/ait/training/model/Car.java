package de.ait.training.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Getter
//@Setter
//@ToString
@AllArgsConstructor
//@NoArgsConstructor
@Data
public class Car {
    private long id;
    private String color;
    private String model;
    private int price;
}
