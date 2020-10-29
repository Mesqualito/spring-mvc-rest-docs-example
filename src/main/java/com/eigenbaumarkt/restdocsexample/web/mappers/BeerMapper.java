package com.eigenbaumarkt.restdocsexample.web.mappers;

import com.eigenbaumarkt.restdocsexample.domain.Beer;
import com.eigenbaumarkt.restdocsexample.web.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

    BeerDTO BeerToBeerDTO(Beer beer);
    Beer BeerDTOToBeer(BeerDTO beerDTO);

}
