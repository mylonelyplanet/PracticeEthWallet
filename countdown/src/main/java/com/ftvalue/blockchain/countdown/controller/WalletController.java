package com.ftvalue.blockchain.countdown.controller;

import com.ftvalue.blockchain.countdown.model.dto.WalletInfoDTO;
import com.ftvalue.blockchain.countdown.service.WalletService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by mylonelyplanet on 2018/7/19.
 */
@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    WalletService walletService;

    @RequestMapping("/getWalletInfo")
    public WalletInfoDTO getWalletInfo() {
        return walletService.getWalletInfo();
    }

    @RequestMapping("/newAddress")
    public String newAddress(NewAddressDTO data) {
        return walletService.newAddress(data.getName(), data.getSecret());
    }

    @MessageMapping("/importAddress")
    public String importAddress(ImportAddressDTO data) {
        return walletService.importAddress(data.getAddress(), data.getName());
    }

    @RequestMapping("/removeAddress")
    public void removeAddress(StringValueDTO data) {
        walletService.removeAddress(data.value);
    }


    /**
     * Created by Stan Reshetnyk on 26.08.16.
     */
    @Data
    public static class StringValueDTO {

        public String value;
    }

    /**
     * Created by Stan Reshetnyk on 25.08.16.
     */
    @Data
    public static class ImportAddressDTO {

        private String address;

        private String name;
    }

    /**
     * Created by Stan Reshetnyk on 25.08.16.
     */
    @Data
    public static class NewAddressDTO {

        private String secret;

        private String name;
    }
}