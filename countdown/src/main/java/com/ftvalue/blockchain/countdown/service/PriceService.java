package com.ftvalue.blockchain.countdown.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * Created by mylonelyplanet on 2018/7/27.
 */
@Slf4j(topic = "Price")
@Service
public class PriceService {

    RestTemplate restTemplate;
    private final String priceUrl = "https://api.coinmarketcap.com/v1/ticker/";

    public BigDecimal getTokenPrice(String tokenName){

        if(!StringUtils.isEmpty(tokenName)){
            tokenName = "ethereum";
        }
        try {
            ResponseEntity   responseEntity = restTemplate.getForEntity(priceUrl+tokenName,String.class);
            String response = (String)responseEntity.getBody();
            log.info(response);
        }catch (RestClientException rce){

        }

        return BigDecimal.ZERO;
    }
}
