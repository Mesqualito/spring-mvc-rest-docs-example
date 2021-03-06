package com.eigenbaumarkt.restdocsexample.web.controller;


import com.eigenbaumarkt.restdocsexample.domain.Beer;
import com.eigenbaumarkt.restdocsexample.repositories.BeerRepository;
import com.eigenbaumarkt.restdocsexample.web.model.BeerDTO;
import com.eigenbaumarkt.restdocsexample.web.model.BeerStyleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
// very important:
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Autoconfigure REST docs, with Annotation; similar to autoconfiguring the MockMvc-Object:
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.eigenbaumarkt.com", uriPort = 80)
// bring up the Spring Boot Web layer for testing:
@WebMvcTest(BeerController.class)
// extend the WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
// package to look for the mappers:
@ComponentScan(basePackages = "com.eigenbaumarkt.restdocsexample.web.mappers")
class BeerControllerTest {

    // wire in a MockMvc-Object autoconfigured by Spring boot
    @Autowired
    MockMvc mockMvc;

    // autoconfigure and bring in an ObjectMapper-Object for the tests:
    @Autowired
    ObjectMapper objectMapper;

    // only mocking a repository object, that means use a "crash test dummy" instead of using a real repository
    // including bringing up the whole database layer from the Spring framework and a database
    @MockBean
    BeerRepository beerRepository;

    @Test
    void getBeerById() throws Exception {
        given(beerRepository.findById(any())).willReturn(Optional.of(Beer.builder().build()));

        ConstrainedFields fields = new ConstrainedFields(BeerDTO.class);

        mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID().toString())
                // Controller will ignore this; only an exercise for Spring REST docs - API documentation:
                .param("isCold", "yes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                // .andExpect(jsonPath("$.beerName", is("Beer1")))
                .andDo(document("v1/beer-get",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of desired beer to get.")
                        ),
                        requestParameters(
                                parameterWithName("isCold").description("Is beer cold query parameter")
                        ),
                        responseFields(
                                fields.withPath("id").description("Id of Beer").type(UUID.class),
                                fields.withPath("version").description("Version number").type(Integer.class),
                                fields.withPath("createdDate").description("Date when entry was created").type(OffsetDateTime.class),
                                fields.withPath("lastModifiedDate").description("Date when entry was updated the last time").type(OffsetDateTime.class),
                                // TODO: Reflection not working here ?
                                fields.withPath("beerName").description("Name of the beer"),
                                fields.withPath("beerStyle").description("Style of the beer"),
                                fields.withPath("upc").description("UPC of the beer"),
                                fields.withPath("price").description("price per you_name_it"),
                                fields.withPath("quantityOnHand").description("Quantity on hand")
                        )));
    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDTO BeerDTO = getValidBeerDTO();
        String BeerDTOJson = objectMapper.writeValueAsString(BeerDTO);

        ConstrainedFields fields = new ConstrainedFields(BeerDTO.class);

        mockMvc.perform(post("/api/v1/beer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOJson))
                .andExpect(status().isCreated())
                .andDo(document("v1/beer-post",
                        requestFields(
                                fields.withPath("id").ignored(),
                                fields.withPath("version").ignored(),
                                fields.withPath("createdDate").ignored(),
                                fields.withPath("lastModifiedDate").ignored(),
                                fields.withPath("beerName").description("The name of the beer"),
                                fields.withPath("beerStyle").description("The style of the beer"),
                                fields.withPath("upc").description("Beer UPC").attributes(),
                                fields.withPath("price").description("The price of the beer"),
                                fields.withPath("quantityOnHand").ignored()
                        )));
    }

    @Test
    void updateBeerById() throws Exception {
        BeerDTO BeerDTO = getValidBeerDTO();
        String BeerDTOJson = objectMapper.writeValueAsString(BeerDTO);

        mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOJson))
                .andExpect(status().isNoContent());
    }

    BeerDTO getValidBeerDTO() {
        return BeerDTO.builder()
                .beerName("Mönchsambacher Weizen")
                .beerStyle(BeerStyleEnum.WEIZEN_HELL)
                .price(new BigDecimal("9.99"))
                .upc(123123123123L)
                .build();

    }

    // needed for documenting Constraints in the DTO with Spring REST docs
    // with Java reflection from the bean validation for the DTO's fields and their constraints
    private static class ConstrainedFields {
        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }


}
