package com.eigenbaumarkt.restdocsexample.bootstrap;

import com.eigenbaumarkt.restdocsexample.domain.Beer;
import com.eigenbaumarkt.restdocsexample.repositories.BeerRepository;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;

public class BeerLoader implements CommandLineRunner {

    private final BeerRepository beerRepository;

    public BeerLoader(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public void run(String... args) throws Exception {

    }

    private void loadBeerObjects() {

        if(beerRepository.count() == 0) {

            beerRepository.save(Beer.builder()
                    .beerName("Mönchsambacher Lager")
                    .beerStyle("Lagerbier")
                    .quantityToBrew(200)
                    .minOnHand(12)
                    .upc(1900024312001L)
                    .price(new BigDecimal("12.95"))
                    .build());

            beerRepository.save(Beer.builder()
                    .beerName("Mönchsambacher Weizen")
                    .beerStyle("Hefeweißbier")
                    .quantityToBrew(350)
                    .minOnHand(24)
                    .upc(1900024312002L)
                    .price(new BigDecimal("14.95"))
                    .build());
        }


    }

}
