package com.ftvalue.blockchain.countdown.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by mylonelyplanet on 2018/7/26.
 */
@Slf4j
public class AddressUtil {

    public static String cleanAddress(String input) {
        log.info("cleanAddress:{}",input);
        if (input != null) {
            if (input.startsWith("0x")) {
                return input.substring(2,input.length()-5).toLowerCase();
            }
            return input.toLowerCase();
        }
        return input;
    }

}

