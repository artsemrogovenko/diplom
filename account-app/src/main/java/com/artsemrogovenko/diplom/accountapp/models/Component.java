package com.artsemrogovenko.diplom.accountapp.models;

import com.artsemrogovenko.diplom.accountapp.dto.ComponentData;
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
    @ManyToMany(mappedBy = "components") //один компонент может относиться ко многим модулям
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

    public Component(Long id) {
        this.id = id;
    }

    public boolean fieldsIsNull() {
        return id == null && factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null;
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
        return Objects.equals(factoryNumber, component.factoryNumber) && Objects.equals(model, component.model) && Objects.equals(name, component.name) && Objects.equals(quantity, component.quantity) && Objects.equals(unit, component.unit) && Objects.equals(description, component.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryNumber, model, name, quantity, unit, description);
    }

//    @Override
//    public int hashCode() {
//        final int PRIME = 59;
//        int result = 1;
////        result = result * PRIME + (id == null ? 0 : id.hashCode());
//        result = result * PRIME + (factoryNumber == null ? 0 : factoryNumber.hashCode());
//        result = result * PRIME + (model == null ? 0 : model.hashCode());
//        result = result * PRIME + (name == null ? 0 : name.hashCode());
//        result = result * PRIME + (quantity == null ? 0 : quantity.hashCode());
//        result = result * PRIME + (unit == null ? 0 : unit.hashCode());
//        result = result * PRIME + (description == null ? 0 : description.hashCode());
//        return result;
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Component component = (Component) o;
//        return
////                Objects.equals(id, component.id) &&
//                        Objects.equals(factoryNumber, component.factoryNumber) && Objects.equals(model, component.model) && Objects.equals(name, component.name) && Objects.equals(quantity, component.quantity) && Objects.equals(unit, component.unit) && Objects.equals(description, component.description);
//    }


}