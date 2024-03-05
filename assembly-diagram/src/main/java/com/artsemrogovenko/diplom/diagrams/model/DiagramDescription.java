package com.artsemrogovenko.diplom.diagrams.model;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class DiagramDescription {
    private String moduleName;
    private String modification;
    private String versionAssembly;

}
