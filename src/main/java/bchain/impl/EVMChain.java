package bchain.impl;

import bchain.entity.GenerateEntity;
import bchain.factory.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;
import static org.web3j.crypto.WalletUtils.generateWalletFile;

public class EVMChain extends BlockChain {
    @Override
    public GenerateEntity CreateWallet(NetworkParameters params, String filePath, String mnemonics) {
        try {
            return createWalletFromMnemonics(mnemonics, filePath, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GenerateEntity importMnc(String filePath, String mnemonics) {
        try {
            return createWalletFromMnemonics(mnemonics, filePath, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GenerateEntity importPrivateKey(NetworkParameters params, String filePath, String privateKey) {
        try {
            File file = createFile(filePath + "/eth");
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
            String walletFile = generateWalletFile("", ecKeyPair, file, false);
            Credentials credentials = Credentials.create(ecKeyPair);
            GenerateEntity generateEntity = new GenerateEntity();
            generateEntity.setMnemonics("");
            generateEntity.setPrivateKey(privateKey);
            generateEntity.setAddress(credentials.getAddress());
            generateEntity.setId(0);
            generateEntity.setFileName(walletFile);
            return generateEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String importKeystore() {
        return null;
    }

//    @Override
//    public BigDecimal balanceOf(Object... params) {
//        try {
//            Web3j web3j = (Web3j) params[0];
//            String addr = (String) params[1];
//            BigInteger balance = web3j.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send().getBalance();
//            BigInteger bigInteger = new BigInteger(String.valueOf(0));
//            if (balance.compareTo(bigInteger) > 0) {
//                return Convert.fromWei(String.valueOf(balance), Convert.Unit.ETHER);
//            }
//            return BigDecimal.ZERO;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public GenerateEntity createWalletFromMnemonics(String mnemonics, String filePath, int id) {
        try {
            File file = createFile(filePath + "/eth");
            byte[] seed = MnemonicUtils.generateSeed(mnemonics, null);
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, id};
            Bip32ECKeyPair bip32ECKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
            String walletFile = generateWalletFile("", bip32ECKeyPair, file, false);
            Credentials credentials = Credentials.create(bip32ECKeyPair);
            GenerateEntity generateEntity = new GenerateEntity();
            generateEntity.setMnemonics(mnemonics);
            generateEntity.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
            generateEntity.setAddress(credentials.getAddress());
            generateEntity.setId(id);
            generateEntity.setFileName(walletFile);
            return generateEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
}
