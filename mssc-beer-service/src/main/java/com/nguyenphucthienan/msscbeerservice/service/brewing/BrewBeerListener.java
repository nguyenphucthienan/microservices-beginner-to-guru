package com.nguyenphucthienan.msscbeerservice.service.brewing;

import com.nguyenphucthienan.msscbeerservice.config.JmsConfig;
import com.nguyenphucthienan.msscbeerservice.domain.Beer;
import com.nguyenphucthienan.msscbeerservice.event.BrewBeerEvent;
import com.nguyenphucthienan.msscbeerservice.event.NewInventoryEvent;
import com.nguyenphucthienan.msscbeerservice.repository.BeerRepository;
import com.nguyenphucthienan.msscbeerservice.web.model.BeerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BrewBeerListener {

    private final BeerRepository beerRepository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    public void listen(BrewBeerEvent event) {
        BeerDTO beerDTO = event.getBeerDTO();
        Beer beer = beerRepository.getOne(beerDTO.getId());
        beerDTO.setQuantityOnHand(beer.getQuantityToBrew());

        log.debug("Brewed Beer " + beer.getMinOnHand() + " : Quantity On Hand: " + beerDTO.getQuantityOnHand());

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDTO);
        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
