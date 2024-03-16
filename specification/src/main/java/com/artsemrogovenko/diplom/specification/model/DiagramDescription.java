package com.artsemrogovenko.diplom.specification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
public class DiagramDescription {
    private String moduleName;
    private String modification;
    private String versionAssembly;

}
