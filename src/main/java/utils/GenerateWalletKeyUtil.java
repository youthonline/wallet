package utils;

import bchain.entity.GenerateEntity;
import bchain.factory.BlockChain;
import bchain.factory.FactoryChain;
import org.bitcoinj.core.NetworkParameters;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GenerateWalletKeyUtil {


    public HashMap<String, GenerateEntity> importMnemonics(NetworkParameters parameters, String path, String mnemonics, String[] typeList) {
        HashMap<String, GenerateEntity> res = new LinkedHashMap<>();
        for (String type : typeList) {
            BlockChain blockChain = FactoryChain.newBlockChain(type);
            if (blockChain != null) {
                GenerateEntity entity = blockChain.CreateWallet(parameters, path, mnemonics);
                res.put(type, entity);
            } else {
                res.put(type, null);
            }
        }
        return res;
    }

    public GenerateEntity importPrivateKey(NetworkParameters params, String path, String priKey, String type) {
        BlockChain blockChain = FactoryChain.newBlockChain(type);
        if (blockChain != null) {
            return blockChain.importPrivateKey(params, path, priKey);
        }
        return null;
    }

    /**
     * create wallet
     * @param parameters network params
     * @param filePath   file path
     * @param typeList   chain name array：["ETH","BTC"......]
     * @return key-value
     */
    public HashMap<String, GenerateEntity> generateWallet(NetworkParameters parameters, String filePath, String[] typeList) {
        HashMap<String, GenerateEntity> res = new LinkedHashMap<>();
        String mnemonics = generateMnemonics();
        for (String type : typeList) {
            BlockChain blockChain = FactoryChain.newBlockChain(type);
            if (blockChain != null) {
                GenerateEntity entity = blockChain.CreateWallet(parameters, filePath, mnemonics);
                res.put(type, entity);
            } else {
                res.put(type, null);
            }
        }
        return res;
    }

    /**
     * 根据私钥获取credentials
     *
     * @param privateKey 私钥
     * @return credentials(用于签名交易)
     */
    public Credentials getCredentialsByPrvKey(String privateKey) {
        try{
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
            return Credentials.create(ecKeyPair);
        }catch (Exception e){
            return null;
        }
    }

    private String generateMnemonics() {
        byte[] initialEntropy = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return mnemonic;
    }
}
