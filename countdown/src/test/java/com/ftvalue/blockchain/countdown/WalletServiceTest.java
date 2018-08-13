package com.ftvalue.blockchain.countdown;

import com.ftvalue.blockchain.countdown.model.dto.WalletInfoDTO;
import com.ftvalue.blockchain.countdown.service.WalletService;
import com.ftvalue.blockchain.countdown.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.Credentials;

/**
 * Created by mylonelyplanet on 2018/7/25.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletServiceTest {

    @Autowired
    WalletService walletService;

    @Test
    public void getWalletInfoTest(){

        WalletInfoDTO walletInfoDTO = walletService.getWalletInfo();
        log.info(JsonUtil.toJson(walletInfoDTO));
    }

    @Test
    public void newAddress(){

        walletService.newAddress("tt3","123456");
    }

    @Test
    public void importAddress(){
        walletService.importAddress("2299D2233ACD2A44781472d2C576009F55017E07","money");
    }

    @Test
    public void importPersonalAddressTest(){
        Credentials credentials = Credentials.create("6041dcf866a5d2f8e7f3af19eeae4cfe5fde552db6b370a6aa4ecd90ab61c929");

        walletService.importPersonal(credentials,"123456");
    }

}
