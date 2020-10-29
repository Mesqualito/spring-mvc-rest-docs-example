package com.eigenbaumarkt.restdocsexample.web.controller;


import com.eigenbaumarkt.restdocsexample.domain.Beer;
import com.eigenbaumarkt.restdocsexample.repositories.BeerRepository;
import com.eigenbaumarkt.restdocsexample.web.model.BeerDTO;
import com.eigenbaumarkt.restdocsexample.web.model.BeerStyleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
// bring in the mappers:
@ComponentScan(basePackages = "com.eigenbaumarkt.restdocsexample.web.mappers")
class BeerControllerTest {

    // bring only up the web layer, not the whole Spring framework
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // mocking the repository, not bringing up the database layer from the Spring framework
    @MockBean
    BeerRepository beerRepository;

    @Test
    void getBeerById() throws Exception {
        given(beerRepository.findById(any())).willReturn(Optional.of(Beer.builder().build()));

        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDTO BeerDTO =  getValidBeerDTO();
        String BeerDTOJson = objectMapper.writeValueAsString(BeerDTO);

        mockMvc.perform(post("/api/v1/beer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBeerById() throws Exception {
        BeerDTO BeerDTO =  getValidBeerDTO();
        String BeerDTOJson = objectMapper.writeValueAsString(BeerDTO);

        mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOJson))
                .andExpect(status().isNoContent());
    }

    BeerDTO getValidBeerDTO(){
        return BeerDTO.builder()
                .beerName("Mönchsambacher Weizen")
                .beerStyle(BeerStyleEnum.WEIZEN_HELL)
                .price(new BigDecimal("9.99"))
                .upc(123123123123L)
                .build();

    }

}
