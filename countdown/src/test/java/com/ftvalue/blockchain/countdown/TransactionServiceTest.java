package com.ftvalue.blockchain.countdown;

import com.ftvalue.blockchain.countdown.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mylonelyplanet on 2018/7/26.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {
    @Autowired
    TransactionService transactionService;

    @Test
    public void transferFromTest(){
        String from = "dF9EF4D8755FAdb27b1258Ac3Ca66078f60F3A35";
        String to = "8f6b17b4cd02736744f086d02b091e2fa68a1365";

        transactionService.transferFrom(from,"123456",to,"0.1");
    }

    @Test
    public void uploadDataTest(){
        String from = "dF9EF4D8755FAdb27b1258Ac3Ca66078f60F3A35";
        String to = "8f6b17b4cd02736744f086d02b091e2fa68a1365";
        String data = "142434423";

        transactionService.uploadData(from,"123456",to,data);
    }
}
