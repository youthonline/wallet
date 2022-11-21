package bchain.impl;

import bchain.entity.GenerateEntity;
import bchain.factory.BlockChain;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BTCChain extends BlockChain {
    @Override
    public GenerateEntity CreateWallet(NetworkParameters params, String filePath, String mnemonics) {
        return createWalletByMnemonics(params, mnemonics, 0);
    }

    public GenerateEntity createWalletByMnemonics(NetworkParameters params, String mnemonics, int id) {
        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonics, null, "", 0L);
            DeterministicKeyChain deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
            String path = "44H / 0H / 0H / 0 / " + id;
            BigInteger privKey = deterministicKeyChain.getKeyByPath(HDUtils.parsePath(path), true).getPrivKey();
            ECKey ecKey = ECKey.fromPrivate(privKey);
            Address address = ecKey.toAddress(params);
            GenerateEntity generateEntity = new GenerateEntity();
            generateEntity.setMnemonics(mnemonics);
            generateEntity.setPrivateKey(ecKey.getPrivateKeyAsWiF(params));
            generateEntity.setAddress(address.toBase58());
            generateEntity.setId(id);
            generateEntity.setFileName("");
            return generateEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GenerateEntity importMnc(String filePath, String mnemonics) {
        return null;
    }

    @Override
    public GenerateEntity importPrivateKey(NetworkParameters params, String filePath, String privateKey) {
        byte[] decode = Base58.decode(privateKey);
        String substring = Hex.toHexString(decode).substring(2, 66);
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(substring));
        Address address = ecKey.toAddress(params);
        GenerateEntity generateEntity = new GenerateEntity();
        generateEntity.setMnemonics("");
        generateEntity.setPrivateKey(privateKey);
        generateEntity.setAddress(address.toBase58());
        generateEntity.setId(0);
        generateEntity.setFileName("");
        return generateEntity;
    }


    @Override
    public String importKeystore() {
        return null;
    }

}
