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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Autoconfigure REST docs, with Annotation; similar to autoconfiguring the MockMvc-Object:
@AutoConfigureRestDocs
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

        mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID().toString())
                // Controller will ignore this; only an exercise for Spring REST docs - API documentation:
                .param("isCold", "yes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/beer",
                        pathParameters(
                                parameterWithName("beerId").description("UUID of desired beer to get.")
                        ),
                        requestParameters(
                                parameterWithName("isCold").description("Is beer cold query parameter")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of Beer"),
                                fieldWithPath("version").description("Version number"),
                                fieldWithPath("createdDate").description("Date when entry was created"),
                                fieldWithPath("lastModifiedDate").description("Date when entry was updated the last time"),
                                fieldWithPath("beerName").description("Name of the beer"),
                                fieldWithPath("beerStyle").description("Style of the beer"),
                                fieldWithPath("upc").description("UPC of the beer"),
                                fieldWithPath("price").description("price per you_name_it"),
                                fieldWithPath("quantityOnHand").description("Quantity on hand")
                        )));
    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDTO BeerDTO = getValidBeerDTO();
        String BeerDTOJson = objectMapper.writeValueAsString(BeerDTO);

        mockMvc.perform(post("/api/v1/beer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOJson))
                .andExpect(status().isCreated())
                .andDo(document("v1/beer",
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("version").ignored(),
                                fieldWithPath("createdDate").ignored(),
                                fieldWithPath("lastModifiedDate").ignored(),
                                fieldWithPath("beerName").description("The name of the beer"),
                                fieldWithPath("beerStyle").description("The style of the beer"),
                                fieldWithPath("upc").description("Beer UPC").attributes(),
                                fieldWithPath("price").description("The price of the beer"),
                                fieldWithPath("quantityOnHand").ignored()
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

}
