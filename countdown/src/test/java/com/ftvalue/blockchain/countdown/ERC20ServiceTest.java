package com.ftvalue.blockchain.countdown;

import com.ftvalue.blockchain.countdown.service.blockchain.ERC20Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ERC20ServiceTest {
    @Autowired
    @Qualifier("ERC20Service")
    ERC20Service service;

    String account = "3427764cb614f0fb95bb5fba54947eaf8e9f2757";
    String password = "123456";
    String contractAddress = "0x55923d84ad63c1607cd6378d320ac7388c7c957d";
    BigInteger gasPrice = BigInteger.valueOf(20000000000l);
    BigInteger gasLimit = BigInteger.valueOf(6721975);

    @Before
    public void init(){
        service.init(account,password,contractAddress,gasPrice,gasLimit);
    }

    @Test
    public void getBalanceTest(){
        log.info("balance: "+service.getBalance("0x"+account));
    }
/*

    @Test
    public void getBlockNumberTest(){
        log.info(""+service.getBlockNumber());
    }
*/

}
