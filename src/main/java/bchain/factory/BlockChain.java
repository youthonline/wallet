package bchain.factory;

import bchain.entity.GenerateEntity;
import org.bitcoinj.core.NetworkParameters;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

public abstract class BlockChain {

    /**
     * create wallet
     *
     * @param filePath
     * @param mnemonics
     * @return
     */
    public abstract GenerateEntity CreateWallet(NetworkParameters params,String filePath, String mnemonics);

    /**
     * create wallet from mnemonics
     *
     * @param filePath
     * @param mnemonics
     * @return
     */
    public abstract GenerateEntity importMnc(String filePath, String mnemonics);

    /**
     * create wallet from privateKey
     *
     * @param filePath
     * @param privateKey
     * @return
     */
    public abstract GenerateEntity importPrivateKey(NetworkParameters params,String filePath, String privateKey);

    /**
     * create wallet from keystore(just ETH)
     *
     * @return
     */
    public abstract String importKeystore();

}
