package com.artsemrogovenko.diplom.taskmanager.model;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Component implements ComponentData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //
    @JsonIgnore
    @ManyToMany //один компонент может относится ко многим модулям
    private Set<Module> modules = new HashSet<>();
    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;       // заводской номер
    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // имя
    @Column(columnDefinition = "INT", nullable = false)
    private Integer quantity;    // какое количество компонента в модуле
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String unit;     // единица измерения
    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет


    public boolean fieldsIsNull() {
        return id == null && factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null ;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", factoryNumber='" + factoryNumber + '\'' +
                ", model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(getFactoryNumber(), component.getFactoryNumber()) && Objects.equals(getModel(), component.getModel()) && Objects.equals(getName(), component.getName()) && Objects.equals(getQuantity(), component.getQuantity()) && Objects.equals(getUnit(), component.getUnit()) && Objects.equals(getDescription(), component.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFactoryNumber(), getModel(), getName(), getQuantity(), getUnit(), getDescription());
    }
}