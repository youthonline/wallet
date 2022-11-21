package bchain;


import btc.SignBTCInfo;
import common.BigDecimalUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class BTCChain {
    private static BTCChain instance = new BTCChain();

    private BTCChain() {
    }

    public static BTCChain getInstance() {
        return instance;
    }

    public NetworkParameters initParam(String type) {
        NetworkParameters parameters = null;
        if ("main".equals(type)) {
            parameters = MainNetParams.get();
        } else if ("test".equals(type)) {
            parameters = TestNet3Params.get();
        } else {
            parameters = RegTestParams.get();
        }
        return parameters;
    }

    public BitcoinJSONRPCClient initUrl(String url) throws Exception {
        return new BitcoinJSONRPCClient(url);
    }

    /**
     * 发送交易
     *
     * @param params
     * @param client
     * @param spenderPriKey
     * @param toAddr
     * @param amount
     * @param fee
     * @return
     * @throws Exception
     */
    public String sendNativeTransaction(NetworkParameters params, BitcoinJSONRPCClient client, String spenderPriKey, String toAddr, String amount, String fee) throws Exception {
        return SignBTCInfo.sendBTC(params, client, spenderPriKey, toAddr, amount, fee);
    }

    public String sendTokenTransaction(NetworkParameters params, BitcoinJSONRPCClient client, String spenderPriKey, String toAddr, String amount, String fee, int usdtId) throws Exception {
        return SignBTCInfo.sendOmni(params, client, spenderPriKey, toAddr, amount, fee, usdtId);
    }


    public String sendTxTwo(NetworkParameters params, BitcoinJSONRPCClient client, String spenderPriKey, String toAddr, String amount, String fee) throws Exception {
        return SignBTCInfo.sendTxToAPI(params, client, "", spenderPriKey, toAddr, amount, fee);
    }


    /**
     * 广播交易
     *
     * @param client
     * @param hexTx
     * @return
     * @throws Exception
     */
    public String broadcast(BitcoinJSONRPCClient client, String hexTx) throws Exception {
        return SignBTCInfo.broadcast(client, hexTx);
    }

    /**
     * 在私链节点查询余额
     *
     * @param client
     * @param addr
     * @return
     */
    public String getBalanceFromRPC(BitcoinJSONRPCClient client, String addr) {
        return SignBTCInfo.getBalanceFromRPC(client, addr);
    }

    public String getBTCBalance(BitcoinJSONRPCClient client, String addr) {
        return SignBTCInfo.getBTCBalance(client, addr);
    }

    /**
     * 查询USDT余额
     *
     * @param client
     * @param address
     * @return
     */
    public String getUsdtBalance(BitcoinJSONRPCClient client, String address, int i) {
        HashMap<String, String> omni_getbalance = (HashMap<String, String>) client.query("omni_getbalance", address, i);
        return omni_getbalance.get("balance");
    }

    public boolean validAddr(BitcoinJSONRPCClient client, String address) {
        HashMap<String, Boolean> omni_getbalance = (HashMap<String, Boolean>) client.query("validateaddress", address);
        return omni_getbalance.get("isvalid");
    }

    public boolean valid(NetworkParameters parameters,String input){
        try{
            Address address = Address.fromBase58(parameters, input);
            if (address != null){
                return  true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 在正式区块链浏览器查询余额
     *
     * @param url  https://blockchain.info/balance?active=
     * @param addr 地址
     * @return
     */
    public String getBalanceFromAPI(String url, String addr) {
        return SignBTCInfo.getBalanceFromAPI(url, addr);
    }


    public int getInputNum(BitcoinJSONRPCClient client, String address, String amount) {
        return SignBTCInfo.calculateInput(client, address, amount);
    }

    public String getBTCBytes(String singleByte, int input) {
        //(input*148+34*out+10)*singleByte
        int bytesNum = input * 148 + 34 * 2 + 10;
        String res = BigDecimalUtils.calculateGas(String.valueOf(bytesNum), singleByte);
        return BigDecimalUtils.toMax(res, 8);
    }

    public String getUSDTBytes(String singleByte, int input) {
        //(input*144+34*out+10)*singleByte
        int bytesNum = input * 144 + 34 * 3 + 10;
        String res = BigDecimalUtils.calculateGas(String.valueOf(bytesNum), singleByte);
        return BigDecimalUtils.toMax(res, 8);

    }

    public String getBTCGasInfo(BitcoinJSONRPCClient client, String address, String bytesFee, String amount) {
        int i = SignBTCInfo.calculateInput(client, address, amount);
        return getBTCBytes(bytesFee, i);
    }

    public String getUSDTGasInfo(BitcoinJSONRPCClient client, String address, String bytesFee) {
        int i = SignBTCInfo.calculateInput(client, address, "0.00000546");
        return getUSDTBytes(bytesFee, i);
    }


    public HashMap<String, String> getSinglePrice(String url) {
        // https://bitcoinfees.earn.com/api/v1/fees/recommended
        HashMap<String, String> res = new LinkedHashMap<>();
        res.put("fast", "190");
        res.put("mid", "150");
        res.put("slow", "100");
        return res;
    }

    /**
     * BTC 交易状态查询 0-未查询到，1-确认中，2-确认成功
     *
     * @param client
     * @param txHash
     * @return
     */
    public int getTransactionStatus(BitcoinJSONRPCClient client, String txHash) {
        BitcoindRpcClient.RawTransaction rawTransaction = client.getRawTransaction(txHash);
        if (null == rawTransaction) {
            return 0;
        }
        Integer confirmations = rawTransaction.confirmations();
        if (null != confirmations) {
            if (confirmations < 6) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

}
