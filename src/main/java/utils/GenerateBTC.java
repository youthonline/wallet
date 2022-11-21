package utils;

import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.wallet.*;
import org.bouncycastle.util.encoders.Hex;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateBTC {

    public NetworkParameters initBtcNet(String type) {
        if (type.equals("0")) {
            return MainNetParams.get();
        } else if (type.equals("1")) {
            return TestNet3Params.get();
        } else {
            return RegTestParams.get();
        }
    }

    /**
     * 生成BTC钱包
     *
     * @return
     */
    public Map<String, String> generateBTCWallet(NetworkParameters params) {
        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), 128, "", Utils.currentTimeSeconds());
        Wallet wallet;
        wallet = Wallet.fromSeed(params, seed);
        String privateKey = wallet.currentReceiveKey().getPrivateKeyAsWiF(params);
        String mnemonics = wallet.getKeyChainSeed().getMnemonicCode().toString();
        String address = wallet.currentReceiveAddress().toBase58();
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        resultMap.put("mnemonics", mnemonics);
        resultMap.put("privateKey", privateKey);
        resultMap.put("address", address);
        return resultMap;
    }


    /**
     * 根据助记词生成BTC钱包/导入助记词恢复钱包
     *
     * @param mnemonics
     * @return 私钥, 地址
     */
    public HashMap<String, String> createWalletByMnemonics(NetworkParameters params, String mnemonics) {
        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonics, null, "", 0L);
            DeterministicKeyChain deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
            BigInteger privKey = deterministicKeyChain.getKeyByPath(HDUtils.parsePath("44H / 0H / 0H / 0 / 0"), true).getPrivKey();
            ECKey ecKey = ECKey.fromPrivate(privKey);
            Address address = ecKey.toAddress(params);
            HashMap<String, String> res = new LinkedHashMap<>();
            res.put("mnemonics", mnemonics);
            res.put("privateKey", ecKey.getPrivateKeyAsWiF(params));
            res.put("address", address.toBase58());
            return res;
        } catch (UnreadableWalletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据私钥导入钱包
     *
     * @param privateKey WIF 压缩私钥
     * @return
     */
    public HashMap<String, String> importWIFPrivateKey(NetworkParameters params, String privateKey) {
        byte[] decode = Base58.decode(privateKey);
        String substring = Hex.toHexString(decode).substring(2, 66);
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(substring));
        Address address = ecKey.toAddress(params);
        HashMap<String, String> res = new LinkedHashMap<>();
        res.put("privateKey", privateKey);
        res.put("address", address.toBase58());
        return res;
    }

    public  ECKey getECKeyByPrivateKey(String privateKey){
        byte[] decode = Base58.decode(privateKey);
        String substring = Hex.toHexString(decode).substring(2, 66);
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(substring));
        return ecKey;
    }



}
