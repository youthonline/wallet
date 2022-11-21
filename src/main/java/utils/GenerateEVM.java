package utils;

import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.io.File;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;
import static org.web3j.crypto.WalletUtils.generateWalletFile;


public class GenerateEVM {

    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    public static String ETH_TYPE = "m/44'/60'/0'/0/0";
    public static String BTC_TYPE = "m/44'/0'/0'/0/0";
    public static String TRX_TYPE = "m/44'/195'/0'/0/0";

    /**
     * 创建钱包
     *
     * @param filePath keystore文件存储位置路径
     * @return 返回助记词
     */
    public HashMap<String, String> generateEVMWallet(String filePath) {
        try {
            byte[] initialEntropy = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(initialEntropy);
            String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
            return createWalletFromMnemonics(mnemonic, filePath, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 助记词生成多个子钱包/恢复钱包
     *
     * @param mnemonics 助记词
     * @param filePath  文件夹路径
     * @param id        钱包id
     * @return
     */
    public HashMap<String, String> createWalletFromMnemonics(String mnemonics, String filePath, int id) {
        try {
            File file = createFile(filePath + "/eth");
            byte[] seed = MnemonicUtils.generateSeed(mnemonics, null);

            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, id};
            Bip32ECKeyPair bip32ECKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
            String walletFile = generateWalletFile("", bip32ECKeyPair, file, false);
            Credentials credentials = Credentials.create(bip32ECKeyPair);
            HashMap<String, String> resultMap = new LinkedHashMap<String, String>();
            resultMap.put("mnemonics", mnemonics);
            resultMap.put("fileName", walletFile);
            resultMap.put("address", credentials.getAddress());
            resultMap.put("id", String.valueOf(id));
            resultMap.put("privateKey", masterKeypair.getPrivateKey().toString(16));
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 助记词恢复
     *
     * @param mnemonic 助记词
     * @param filePath 文件目录
     * @return 地址
     */
    public String importEVMMne(String mnemonic, File filePath) {
        try {
            File file = createFile(filePath + "/eth");
            Credentials credentials = Bip44WalletUtils.loadBip44Credentials("", mnemonic);
            generateWalletFile("", credentials.getEcKeyPair(), file, false);
            return credentials.getAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 私钥恢复
     *
     * @param privateKey 私钥
     * @param filePath   文件目录
     * @return 地址
     */
    public String importEVMPrvKey(String privateKey, File filePath) {
        try {
            File file = createFile(filePath + "/eth");
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
            generateWalletFile("", ecKeyPair, file, false);
            Credentials credentials = Credentials.create(ecKeyPair);
            return credentials.getAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据keystore获取credentials
     *
     * @param fileName keystore 文件 全路径
     * @return credentials(用于签名交易)
     */
    public Credentials getCredentialsByKeyStore(File fileName) {
        try {
            Credentials credentials = Bip44WalletUtils.loadCredentials("", fileName);
            return credentials;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据私钥获取credentials
     *
     * @param privateKey 私钥
     * @return credentials(用于签名交易)
     */
    public Credentials getCredentialsByPrvKey(String privateKey) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return Credentials.create(ecKeyPair);
    }

    private File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }


}
