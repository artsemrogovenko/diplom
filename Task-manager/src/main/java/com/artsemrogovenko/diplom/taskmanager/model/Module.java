package com.artsemrogovenko.diplom.taskmanager.model;

import com.artsemrogovenko.diplom.taskmanager.dto.ModuleData;
import com.artsemrogovenko.diplom.taskmanager.dto.SavedModule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Entity
@NoArgsConstructor
public class Module implements SavedModule, ModuleData {
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

    //один модуль может содержать несколько компонентов
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "module_components", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "component_id"))
    private Set<Component> components = new HashSet<>();   // список компонентов
    private String circutFile;  // схема сборки
    @JsonIgnore
    @ManyToMany
    private List<Template> templates = new ArrayList<>();
    @JsonIgnore
    @ManyToMany
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (this.unit == null) {
            this.unit = "шт";
        }
        if (this.quantity == null) {
            this.quantity = 1;
        }
    }

    public void addComponent(Component c) {
        components.add(c);
    }

    public boolean fieldsIsNull() {
        return id == null && factoryNumber == null && model == null && name == null && quantity == null && unit == null && description == null && components.isEmpty() && circutFile == null;
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
                ", components=" + components +
                ", circutFile='" + circutFile + '\'' +
                '}';
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Module module = (Module) o;
//        return Objects.equals(getFactoryNumber(), module.getFactoryNumber()) && Objects.equals(getModel(), module.getModel()) && Objects.equals(getName(), module.getName()) && Objects.equals(getQuantity(), module.getQuantity()) && Objects.equals(getUnit(), module.getUnit()) && Objects.equals(getDescription(), module.getDescription()) && Objects.equals(getComponents(), module.getComponents()) && Objects.equals(getCircutFile(), module.getCircutFile());
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(getFactoryNumber(), getModel(), getName(), getQuantity(), getUnit(), getDescription(), getComponents(), getCircutFile());
//    }
}
