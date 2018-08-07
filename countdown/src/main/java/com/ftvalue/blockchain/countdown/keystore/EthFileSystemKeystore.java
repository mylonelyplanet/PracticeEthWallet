/*
 * Copyright 2015, 2016 Ether.Camp Inc. (US)
 * This file is part of Ethereum Harmony.
 *
 * Ethereum Harmony is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ethereum Harmony is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ethereum Harmony.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ftvalue.blockchain.countdown.keystore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Key store manager working in user file system. Can store and load keys.
 * Comply to go-ethereum key store format.
 * https://github.com/ethereum/wiki/wiki/Web3-Secret-Storage-Definition
 *
 * Created by Stan Reshetnyk on 26.07.16.
 */
@Component
@Slf4j(topic = "keystore")
public class EthFileSystemKeystore implements Keystore {

    //@Value("#{ ${keystore.dir} != #null ? ${keystore.dir} : #null }")
    //@Value("${keystore.dir:}")
    //public String keystoreDir;


    @Override
    public void removeKey(String address) {
        getFiles().stream()
                .filter(f -> hasAddressInName(address, f))
                .findFirst()
                .ifPresent(f -> f.delete());
    }

    @Override
    public String storeKey( String password) throws RuntimeException {
        String walletFileName = null;
        try{
            final File keysFolder = getKeyStoreLocation().toFile();
            keysFolder.mkdirs();

            walletFileName = WalletUtils.generateFullNewWalletFile(password,keysFolder);
            log.info("Create a new wallet file:{}",walletFileName);

        } catch (IOException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException | CipherException e) {
            log.error("Problem storing key for address: {}",e);
            throw new RuntimeException("Problem storing key for address");
        }

        return  getCleanAddress(walletFileName);

    }

    @Override
    public void storeRawKeystore(Credentials content, String password) throws RuntimeException {

        try {
            final File keysFolder = getKeyStoreLocation().toFile();
            keysFolder.mkdirs();

            String walletFileName = WalletUtils.generateWalletFile(password,content.getEcKeyPair(),keysFolder,true);
            log.info("store a new wallet file:{}",walletFileName);
        } catch (CipherException | IOException e) {
            throw new RuntimeException("Problem storing key for address");
        }
    }

    /**
     * @return array of addresses in format "0x123abc..."
     */
    @Override
    public String[] listStoredKeys() {
        return getFiles().stream()
                .filter(f -> !f.isDirectory())
                .map(f -> f.getName().split("--"))
                .filter(n -> n != null && n.length == 3)
                .map(a -> ("0x" + a[2]).split(",")[0])
                .toArray(size -> new String[size]);
    }

    /**
     * @return some loaded key or null
     */
    @Override
    public Credentials loadStoredKey(String address, String password) throws RuntimeException {
        return getFiles().stream()
                .filter(f -> hasAddressInName(address, f))
                .map(f -> {
                    try {
                        return WalletUtils.loadCredentials(password,f);
                    } catch (Exception e) {
                        throw new RuntimeException("Problem reading keystore file for address:" + address);
                    }
                })
                .findFirst()
                .orElse(null);
    }

    private boolean hasAddressInName(String address, File file) {
        return !file.isDirectory() && file.getName().toLowerCase().endsWith("--" + address.toLowerCase()+".json");
    }

    @Override
    public boolean hasStoredKey(String address) {
        return getFiles().stream()
                .filter(f -> hasAddressInName(address, f))
                .findFirst()
                .isPresent();
    }


    private String getCleanAddress(String walletFileName) {

        if (walletFileName != null) {
            String[] fetchAddress=walletFileName.split("--");

            String getAddress = fetchAddress[fetchAddress.length-1].split("\\.")[0];

            log.info("walletFileName>>>>>" + walletFileName.substring(0));
            log.info("walletFile Address>>>>>" + "0x" + getAddress);

            if (getAddress.startsWith("0x")) {
                return getAddress.substring(2).toLowerCase();
            }
            return getAddress.toLowerCase();
        }
        return walletFileName;
    }

    private List<File> getFiles() {
        final File dir = getKeyStoreLocation().toFile();
        final File[] files = dir.listFiles();
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    /**
     * @return platform dependent path to Ethereum folder
     */
    public Path getKeyStoreLocation() {
//        if (!StringUtils.isEmpty(keystoreDir)) {
//            return Paths.get(keystoreDir);
//        }

        final String keystoreDir = "keystore";
        final String osName = System.getProperty("os.name").toLowerCase();

        if (osName.indexOf("win") >= 0) {
            return Paths.get(System.getenv("APPDATA") + "/Ethereum/" + keystoreDir);
        } else if (osName.indexOf("mac") >= 0) {
            return Paths.get(System.getProperty("user.home") + "/Library/Ethereum/" + keystoreDir);
        } else {    // must be linux/unix
            return Paths.get(System.getProperty("user.home") + "/.ethereum/" + keystoreDir);
        }
    }
}
