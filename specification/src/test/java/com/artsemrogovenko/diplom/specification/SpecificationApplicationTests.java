package com.artsemrogovenko.diplom.specification;

import com.artsemrogovenko.diplom.specification.api.AssemblyApi;
import com.artsemrogovenko.diplom.specification.api.StorageApi;
import com.artsemrogovenko.diplom.specification.dto.ComponentRequest;
import com.artsemrogovenko.diplom.specification.dto.ComponentResponse;
import com.artsemrogovenko.diplom.specification.dto.ModuleRequest;
import com.artsemrogovenko.diplom.specification.model.DiagramDescription;
import com.artsemrogovenko.diplom.specification.model.Init;
import com.artsemrogovenko.diplom.specification.model.Module;
import com.artsemrogovenko.diplom.specification.model.MyCollection;
import com.artsemrogovenko.diplom.specification.repositories.ComponentRepository;
import com.artsemrogovenko.diplom.specification.repositories.ModuleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureDataMongo
class SpecificationApplicationTests {
    @MockBean
    private Init init;

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private ComponentRepository componentRepository;

    @MockBean
    private StorageApi storageApi;
    @MockBean
    private AssemblyApi assemblyApi;
    @Test
    @Transactional
    void shouldCreateModule() throws Exception {
        Mockito.when(assemblyApi.requestSheme(Mockito.any(DiagramDescription.class)))
                .thenReturn(new ResponseEntity<>("no circuit", HttpStatus.OK));

        String moduleRequestString = objectMapper.writeValueAsString(getModuleRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/module")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleRequestString))
                .andExpect(status().isCreated());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/component")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        List<ComponentResponse> response = objectMapper.readValue(content, new TypeReference<List<ComponentResponse>>() {
        });

        List<Module> modules= moduleRepository.findAll();

        Assertions.assertEquals(1, moduleRepository.findAll().size());
        Assertions.assertEquals(2, componentRepository.findAll().size());
        Assertions.assertEquals("no circuit",modules.get(0).getCircuitFile());
    }

    private ModuleRequest getModuleRequest() {
        ComponentRequest component1 = ComponentRequest.builder().factoryNumber("79436").name("НШВИ 0.75-8").quantity(4).unit("шт").build();
        ComponentRequest component2 = ComponentRequest.builder().factoryNumber("00-00012187").name("МКЭШнг(А)-LS 7х0.75 500В").quantity(5).unit("м").build();

        ModuleRequest moduleRequest = new ModuleRequest("factory23123", "2022.01.01", "кабель двигателя", 1, "шт", "в метал.рукаве", new MyCollection(Set.of(component1, component2)), null);
        return moduleRequest;

    }
}
