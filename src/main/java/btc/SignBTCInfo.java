package btc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.BigDecimalUtils;
import common.UnSpentUtxo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;
import utils.GenerateBTC;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.bitcoinj.core.Utils.HEX;

public class SignBTCInfo {

    private static final Coin DEFAULT_FEE = Coin.parseCoin("0.00004");
    private static final Coin MIN_AMOUNT = Coin.valueOf(546L);


    /**
     * 发送BTC交易
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
    public static String sendBTC(NetworkParameters params, BitcoinJSONRPCClient client, String spenderPriKey, String toAddr, String amount, String fee) throws Exception {
        byte[] decode = Base58.decode(spenderPriKey);
        String substring = Hex.toHexString(decode).substring(2, 66);
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(substring));
        List<UnSpentUtxo> unUtxos = GetAddressUtxo(client, ecKey.toAddress(params).toBase58(), amount);
        if (null == unUtxos || unUtxos.size() == 0) {
            return "UTXO null";
        }
        String txHex = signBTCTransaction(params, unUtxos, ecKey, ecKey.toAddress(params), Address.fromBase58(params, toAddr), Coin.parseCoin(amount), fee);
        return broadcast(client, txHex);
    }

    /**
     * 发送Omni交易
     *
     * @param params
     * @param fromKey
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    public static String sendOmni(NetworkParameters params, BitcoinJSONRPCClient client, String fromKey, String to, String amount, String fee, int usdtId) throws Exception {
        byte[] decode = Base58.decode(fromKey);
        String substring = Hex.toHexString(decode).substring(2, 66);
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(substring));
        // 提取需要的unspent列表
        List<UnSpentUtxo> unUtxos = GetAddressUtxo(client, ecKey.toAddress(params).toBase58(), amount);
        if (null == unUtxos || unUtxos.size() == 0) {
            return "";
        }
        // 签名交易
        String hexTx = signOmniTransaction(params, unUtxos, ecKey, ecKey.toAddress(params), Address.fromBase58(params, to), BigDecimalUtils.toMin(amount, 8), fee, usdtId);
        return broadcast(client, hexTx);
    }


    public static String sendTxToAPI(NetworkParameters params, BitcoinJSONRPCClient client, String url, String spenderPriKey, String toAddr, String amount, String fee) throws Exception {
        ECKey ecKey = new GenerateBTC().getECKeyByPrivateKey(spenderPriKey);
        List<UnSpentUtxo> unUtxos = getAddressUTXOFromAPI(url, ecKey.toAddress(params).toBase58(), Coin.parseCoin(amount).toString());
        if (null == unUtxos || unUtxos.size() == 0) {
            return "UTXO null";
        }
        String txHex = signBTCTransaction(params, unUtxos, ecKey, ecKey.toAddress(params), Address.fromBase58(params, toAddr), Coin.parseCoin(amount), fee);
        String txId = broadcast(client, txHex);
        return txId;
    }


    /**
     * 将离线裸交易提交到节点
     *
     * @param client
     * @param hexTx
     * @return
     * @throws Exception
     */
    public static String broadcast(BitcoinJSONRPCClient client, String hexTx) throws Exception {
        return client.sendRawTransaction(hexTx);
    }

    public static String getBTCBalance(BitcoinJSONRPCClient client, String addr) {
        return String.valueOf(BigDecimalUtils.toMax(String.valueOf(client.getAddressBalance(addr).getBalance()), 8));
    }


    /**
     * omni usdt sign
     *
     * @param parameters
     * @param spenderKey
     * @param toAddress
     * @param amount
     * @param changeAmount
     * @param outputs
     * @return
     */
    public String signOmniUsdt(NetworkParameters parameters, String spenderKey, String toAddress, long amount, long changeAmount, List<UTXO> outputs) {
        Transaction tx = new Transaction(parameters);
        // 比特币限制最小转账额度，所以很多usdt会收到一笔0.00000546的btc
        tx.addOutput(Coin.valueOf(546L), Address.fromBase58(parameters, toAddress));
        // 构建usdt的输出脚本，注意金额是需要乘上10的8次方
        String usdtHex = "6a146f6d6e69" + String.format("%016x", 31) + String.format("%016x", amount);
        tx.addOutput(Coin.valueOf(0L), new Script(Utils.HEX.decode(usdtHex)));
        ECKey ecKey = DumpedPrivateKey.fromBase58(parameters, spenderKey).getKey();
        if (changeAmount != 0L) {
            tx.addOutput(Coin.valueOf(changeAmount), ecKey.toAddress(parameters));
        }
        // 先添加未签名的输入，即utxo
        for (UTXO output : outputs) {
            tx.addInput(output.getHash(), output.getIndex(), output.getScript());
        }
        // 签名
        for (int i = 0; i < outputs.size(); i++) {
            UTXO output = outputs.get(i);
            TransactionInput transactionInput = tx.getInput(i);
            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(parameters, output.getAddress()));
            Sha256Hash hash = tx.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
            transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
        }
        String hexTx = HEX.encode(tx.bitcoinSerialize());
        return hexTx;
    }


    /**
     * 从节点获取UTXO
     *
     * @param client     节点连接
     * @param address    查询地址
     * @param sendAmount 转账金额(不会获取所有UTXO，只会处理需要的数量的UTXO)
     * @return
     */
//    private static List<UnSpentUtxo> getAddressUTXOFromRPC(BitcoinJSONRPCClient client, String address, String sendAmount) {
//        BigDecimal balance = new BigDecimal(0);
//        List<UnSpentUtxo> utxos = new ArrayList<UnSpentUtxo>();
//        List<BitcoindRpcClient.Unspent> unspentList = client.listUnspent(6, 99999999, address);
//        for (BitcoindRpcClient.Unspent unspent : unspentList) {
//            utxos.add(new UnSpentUtxo(
//                    unspent.txid(),
//                    unspent.vout(),
//                    unspent.amount().toString(),
//                    unspent.confirmations(),
//                    unspent.scriptPubKey(),
//                    unspent.address()
//            ));
//            balance = balance.add(unspent.amount());
//            if (balance.compareTo(new BigDecimal(sendAmount)) >= 0) {
//                return utxos;
//            }
//        }
//        return null;
//    }


    /**
     * 通过第三方API 获取 UTXO(此处使用blockchain的api，如若使用其他的api需要重新对接)
     *
     * @param url    第三方API
     * @param addr   地址
     * @param amount 转账数量(不会获取所有UTXO，只会处理需要的数量的UTXO)
     * @return
     */
    private static List<UnSpentUtxo> getAddressUTXOFromAPI(String url, String addr, String amount) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url + addr);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            // 解析json
            JSONObject json = JSONObject.parseObject(resp);
            JSONArray array = json.getJSONArray("unspent_outputs");
            List<UnSpentUtxo> unSpentUtxos = new ArrayList<>();
            String total = "0";
            for (int i = 0; i < array.size(); i++) {
                JSONObject pojo = array.getJSONObject(i);
                unSpentUtxos.add(new UnSpentUtxo(
                        pojo.getString("tx_hash"),
                        pojo.getLong("tx_output_n"),
                        pojo.getString("value"),
                        pojo.getInteger("confirmations"),
                        pojo.getString("script"),
                        addr
                ));
                total = BigDecimalUtils.add(total, pojo.getString("value"));
                if (BigDecimalUtils.compareTo(total, amount) >= 0) {
                    return unSpentUtxos;
                }
            }
            response.close();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> getSinglePriceFromAPI(String url) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            // 解析json
            JSONObject json = JSONObject.parseObject(resp);
            HashMap<String, String> res = new LinkedHashMap<>();
            if (null != json) {
                res.put("fast", String.valueOf(json.get("fastestFee")));
                res.put("mid", String.valueOf(json.get("hourFee")));
                res.put("slow", String.valueOf(json.get("halfHourFee")));
            } else {
                res.put("fast", "190");
                res.put("mid", "150");
                res.put("slow", "100");
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过api获取余额
     *
     * @param url  API url
     * @param addr
     * @return
     */
    public static String getBalanceFromAPI(String url, String addr) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url + addr);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
            // 解析json
            JSONObject json = JSONObject.parseObject(resp);
            JSONObject json1 = json.getJSONObject(addr);
            String final_balance = json1.getString("final_balance");
            response.close();
            return BigDecimalUtils.toMax(final_balance, 8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBalanceFromRPC(BitcoinJSONRPCClient client, String address) {
        BigDecimal balance = new BigDecimal(0);
        List<BitcoindRpcClient.Unspent> unspentList = client.listUnspent(6, 99999999, address);
        if (!unspentList.isEmpty()) {
            for (BitcoindRpcClient.Unspent u : unspentList) {
                balance = balance.add(u.amount());
            }
        }
        return balance.toPlainString();
    }

    /**
     * 计算手续费使用，计算需要输入的input数量
     *
     * @param client
     * @param address
     * @param amount
     * @return
     */
    public static int calculateInput(BitcoinJSONRPCClient client, String address, String amount) {
        List<UnSpentUtxo> unUtxos = GetAddressUtxo(client, address, amount);
        if (null == unUtxos || unUtxos.size() == 0) {
            return 0;
        }
        return unUtxos.size();
    }

    /**
     * 构建BTC离线交易
     *
     * @param params
     * @param unSpents
     * @param ecKey
     * @param spendAddr
     * @param receiveAddr
     * @param transAmount
     * @param fee
     * @return
     */
    private static String signBTCTransaction(NetworkParameters params, List<UnSpentUtxo> unSpents, ECKey ecKey, Address spendAddr, Address receiveAddr, Coin transAmount, String fee) {
        // 计算找零数量
        Coin total = Coin.parseCoin("0");
        for (UnSpentUtxo u : unSpents) {
            total = total.add(Coin.parseCoin(String.valueOf(u.getValue())));
        }
        Coin changeAmount;
        if ("0".equals(fee)) {
            changeAmount = total.subtract(transAmount).subtract(DEFAULT_FEE);
        } else {
            changeAmount = total.subtract(transAmount).subtract(Coin.parseCoin(fee));
        }
        Transaction tx = new Transaction(params);
        // 转账添加进output
        tx.addOutput(transAmount, receiveAddr);
        // 找零添加到output
        tx.addOutput(changeAmount, spendAddr);

        List<UTXO> utxos = formatUTXO(unSpents);
        // 设置utxo输入
        for (UTXO utxo : utxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            // YOU HAVE TO CHANGE THIS
            tx.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        }
        // 签名交易
        String hexTx = HEX.encode(tx.bitcoinSerialize());
        return hexTx;
    }

    /**
     * 签名Omni USDT交易，USDT交易是依托于BTC交易的，所以必须有一笔最低的BTC交易，
     * 然后还有个重点是BTC链上的代币的ID，封装USDT的时候第二个参数就是。
     *
     * @param params
     * @param unSpents
     * @param ecKey
     * @param from
     * @param to
     * @param usdtAmount
     * @param fee
     * @param chainId
     * @return
     */
    private static String signOmniTransaction(NetworkParameters params, List<UnSpentUtxo> unSpents, ECKey ecKey, Address from, Address to, String usdtAmount, String fee, int chainId) {
        // 计算找零数量
        Coin total = Coin.parseCoin("0");
        for (UnSpentUtxo u : unSpents) {
            total = total.add(Coin.parseCoin(String.valueOf(u.getValue())));
        }
        Coin changeAmount;
        if ("0".equals(fee)) {
            changeAmount = total.subtract(MIN_AMOUNT).subtract(DEFAULT_FEE);
        } else {
            changeAmount = total.subtract(MIN_AMOUNT).subtract(Coin.parseCoin(fee));
        }

        Transaction tx = new Transaction(params);
        // 处理 USDT 交易 测试网络USDT为1
        String usdtHex = "6a146f6d6e69" + String.format("%016x", chainId) + String.format("%016x", Long.valueOf(usdtAmount));
        tx.addOutput(Coin.valueOf(0L), new Script(Utils.HEX.decode(usdtHex)));
        // 因为Omni交易是在btc网络上的，所以每次交易需要和btc转账一起
        tx.addOutput(MIN_AMOUNT, to);
        // 找零转回原地址
        tx.addOutput(changeAmount, from);

        // 处理UTXO
        List<UTXO> utxos = formatUTXO(unSpents);
        for (UTXO utxo : utxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            tx.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        }
        // 签名交易
        String hexTx = HEX.encode(tx.bitcoinSerialize());
        return hexTx;
    }

    private static List<UTXO> formatUTXO(List<UnSpentUtxo> unSpentEntityList) {
        List<UTXO> utxos = new ArrayList<>();
        for (UnSpentUtxo unUtxo : unSpentEntityList) {
            utxos.add(new UTXO(Sha256Hash.wrap(unUtxo.getHash()),
                    unUtxo.getTxN(),
                    Coin.parseCoin(unUtxo.getValue()),
                    unUtxo.getHeight(),
                    false,
                    new Script(Utils.HEX.decode(unUtxo.getScript())),
                    unUtxo.getAddress()));
        }
        return utxos;
    }





    private static List<UnSpentUtxo> GetAddressUtxo(BitcoinJSONRPCClient client, String address, String sendAmount) {
        String balance = "0";
        List<UnSpentUtxo> utxos = new ArrayList<UnSpentUtxo>();
        // 获取unspent信息
        List<LinkedHashMap<String, Object>> unspentList = (List<LinkedHashMap<String, Object>>) client.query("getaddressutxos", address);
        if (unspentList.size() <= 0) {
            return null;
        }
        for (LinkedHashMap<String, Object> unspent : unspentList) {
            // 将 satoshis 转换为 btc单位
            long satoshis = (long) unspent.get("satoshis");
            String amount = BigDecimalUtils.toMax(String.valueOf(satoshis), 8);
            int height = Integer.valueOf(String.valueOf((long) unspent.get("height")));
            utxos.add(new UnSpentUtxo(
                    (String) unspent.get("txid"),
                    (Long) unspent.get("outputIndex"),
                    amount,
                    height,
                    (String) unspent.get("script"),
                    (String) unspent.get("address")
            ));
            balance = BigDecimalUtils.add(balance, amount);
            if (BigDecimalUtils.compareTo(balance, sendAmount) != 0) {
                return utxos;
            }
        }
        return null;

    }




}
