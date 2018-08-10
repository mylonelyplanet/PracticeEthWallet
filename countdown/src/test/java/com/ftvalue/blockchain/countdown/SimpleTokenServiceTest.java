package com.ftvalue.blockchain.countdown;

import com.ftvalue.blockchain.countdown.service.blockchain.SimpleTokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SimpleTokenServiceTest {
    @Autowired
    SimpleTokenService service;

    @Test
    public void test() throws Exception {
        service.getBalance("363ce6dbb927271249c3da04cc2b31d504ce1ef1","123456");
    }
}
