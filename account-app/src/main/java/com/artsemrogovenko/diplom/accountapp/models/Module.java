package com.artsemrogovenko.diplom.accountapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

@Data
@Entity
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //

    @Column(columnDefinition = "VARCHAR(100)")
    private String factoryNumber;       // заводской номер

    @Column(columnDefinition = "VARCHAR(100)")
    private String model;       // модификация
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;     // имя

    @Column(columnDefinition = "INT", nullable = true)
    private Integer quantity;    // какое количество
    @Column(columnDefinition = "VARCHAR(20)")
    private String unit;     // единица измерения

    @Column(columnDefinition = "VARCHAR(255)")
    private String description;  // тут можно указать например цвет
    private String circuitFile;  // схема сборки

    //один модуль может содержать несколько компонентов
    @ManyToMany(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinTable(name = "module_components", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "component_id"))
    private Set<Component> components = new HashSet<>();    // список компонентов

    @JsonIgnore
    @ManyToMany(mappedBy = "modules")
    private List<Task> tasks = new ArrayList<>();


    public Module(Long id) {
        this.id = id;
    }

    public void addComponent(Component newComponent) {
        components.add(newComponent);
    }


    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", factoryNumber='" + factoryNumber + '\'' +
                ", model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                ", circuitFile='" + circuitFile + '\'' +
                ", components=" + components +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(factoryNumber, module.factoryNumber) && Objects.equals(model, module.model) && Objects.equals(name, module.name) && Objects.equals(quantity, module.quantity) && Objects.equals(unit, module.unit) && Objects.equals(description, module.description) && Objects.equals(circuitFile, module.circuitFile) && Objects.equals(components, module.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryNumber, model, name, quantity, unit, description, circuitFile, components);
    }

}
