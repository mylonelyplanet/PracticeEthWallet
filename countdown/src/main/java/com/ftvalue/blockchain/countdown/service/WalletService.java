package com.ftvalue.blockchain.countdown.service;

import com.ftvalue.blockchain.countdown.keystore.Keystore;
import com.ftvalue.blockchain.countdown.model.dto.WalletAddressDTO;
import com.ftvalue.blockchain.countdown.model.dto.WalletInfoDTO;
import com.ftvalue.blockchain.countdown.service.blockchain.BlockChainService;
import com.ftvalue.blockchain.countdown.service.wallet.FileSystemWalletStore;
import com.ftvalue.blockchain.countdown.service.wallet.WalletAddressItem;
import com.ftvalue.blockchain.countdown.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mylonelyplanet on 2018/7/24.
 */
@Slf4j(topic = "wallet")
@Service
public class WalletService {

    private static final BigInteger gasLimit = BigInteger.valueOf(21_000L);

    /**
     * key - hex address in lower case
     * value - wallet
     */
    final Map<String, String> addresses = new ConcurrentHashMap<>();

    @Autowired
    FileSystemWalletStore fileSystemWalletStore;

    @Autowired
    Keystore keystore;

    @Autowired
    BlockChainService blockChainService;


    @Autowired(required = false)
    ClientMessageService clientMessageService;



    @PostConstruct
    public void init() {
        addresses.clear();

        final AtomicInteger index = new AtomicInteger();
        Arrays.asList(keystore.listStoredKeys())
                .forEach(a -> addresses.put(AddressUtil.cleanAddress(a), "Account " + index.incrementAndGet()));

        fileSystemWalletStore.fromStore().stream()
                .forEach(a -> addresses.put(a.address, a.name));

    }

    //@Scheduled(fixedRate = 60000)
    private void doSendWalletInfo() {
        clientMessageService.sendToTopic("/topic/getWalletInfo", getWalletInfo());
    }


    public WalletInfoDTO getWalletInfo() {

        List<WalletAddressDTO> list = addresses.entrySet().stream()
                .flatMap(e -> {
                    final String hexAddress = e.getKey();
                    try {
                        final BigInteger balance = blockChainService.getBalance(hexAddress);

                        return Stream.of(new WalletAddressDTO(
                                e.getValue(),
                                e.getKey(),
                                balance,
                                keystore.hasStoredKey(e.getKey())));
                    } catch (Exception exception) {
                        log.error("Error in making wallet address " + hexAddress, exception);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        BigInteger totalAmount = list.stream()
                .map(t -> t.getAmount())
                .reduce(BigInteger.ZERO, (state, amount) -> state.add(amount));

        WalletInfoDTO result = new WalletInfoDTO(totalAmount);

        result.getAddresses().addAll(list);
        return result;
    }

    /**
     * Generate new key and address. Key will be kept in keystore.
     */
    public String newAddress(String name, String password) {
        log.info("newAddress " + name);
        if (addresses.values().contains(name)) {
            log.warn("find same name in wallet, return existing address.");
            return addresses.values().stream().filter(v->v.equalsIgnoreCase(name)).findFirst().get();
        }
        // generate new private key
        String getAddress = keystore.storeKey(password);
        log.info(getAddress);
        addresses.put(getAddress,name);

        flushWalletToDisk();

        clientMessageService.sendToTopic("/topic/getWalletInfo", getWalletInfo());

        return getAddress;
    }

    /**
     * Stores provided key in keystore and addresses (with auto-generated name)
     * @param key           credential
     * @param password      Protection password
     * @return  address
     */
    public String importPersonal(Credentials key, String password) {

        final String address = AddressUtil.cleanAddress(key.getAddress());
        // Giving next vacant name to this unnamed account
        int i = 1;
        boolean vacant = false;
        String name = null;
        while(!vacant) {
            name = String.format("Account #%s", i);
            if (!addresses.values().contains(name)) {
                vacant = true;
            } else {
                ++i;
            }
        }
        log.info("newPersonal " + name);

        keystore.storeRawKeystore(key, password);
        addresses.put(address, name);

        flushWalletToDisk();

        return key.getAddress();
    }

    /**
     * Import address without keeping key on server.
     */
    public String importAddress(String addressValue, String name) {
        Objects.requireNonNull(addressValue);

        final String address = AddressUtil.cleanAddress(addressValue);

        validateAddress(address);

        addresses.put(address, name);

        flushWalletToDisk();

        clientMessageService.sendToTopic("/topic/getWalletInfo", getWalletInfo());

        return address;
    }

    private void validateAddress(String value) {
        Objects.requireNonNull(value);
        if (value.length() != 40) {
            throw new RuntimeException("Address value is invalid");
        }
        Hex.decode(value);
    }

    public void removeAddress(String value) {
        Objects.requireNonNull(value);

        final String address = AddressUtil.cleanAddress(value);
        addresses.remove(address);
        keystore.removeKey(address);

        flushWalletToDisk();

        clientMessageService.sendToTopic("/topic/getWalletInfo", getWalletInfo());
    }


    private void flushWalletToDisk() {
        fileSystemWalletStore.toStore(addresses.entrySet().stream()
                .map(e -> new WalletAddressItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }


}
