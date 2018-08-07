package com.ftvalue.blockchain.countdown.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigInteger;

/**
 * Created by mylonelyplanet on 2018/7/26.
 */
@Value
@AllArgsConstructor
public class TransactionInfoDTO {

    private String hash;

    private BigInteger amount;

    private Boolean sending;

    private String fromAddress;

    private String toAddress;
}
