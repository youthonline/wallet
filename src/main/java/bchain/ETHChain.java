package bchain;

import bchain.entity.ReturnsEntity;
import common.BigDecimalUtils;
import common.DefaultInfo;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ETHChain {

    private static final String DEFAULT_GAS_LIMIT = "50000";


    private static ETHChain instance = new ETHChain();

    private ETHChain() {
    }

    public static ETHChain getInstance() {
        return instance;
    }


    public Web3j initClient(String url) {
        return Web3j.build(new HttpService(url));
    }


    public BigDecimal balanceOf(Web3j web3j, String addr) {
        try {
            BigInteger balance = web3j.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send().getBalance();
            BigInteger bigInteger = new BigInteger(String.valueOf(0));
            if (balance.compareTo(bigInteger) > 0) {
                return Convert.fromWei(String.valueOf(balance), Convert.Unit.ETHER);
            }
            return BigDecimal.ZERO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReturnsEntity sendNativeTransaction(Web3j web3j, Credentials credentials, String to, String amount, String gasPrice, String gasLimit) throws Exception {
        String hexValue = createNativeSign(web3j, credentials, to, amount, gasPrice, gasLimit);
        return boardNativeTx(web3j, hexValue);
    }

    public ReturnsEntity sendTokenTransaction(Web3j web3j, Credentials credentials, String to, String contractAddr, String amount, String gasPrice, String gasLimit) {
        try {
            BigInteger chainId = web3j.ethChainId().send().getChainId();
            // get token decimals
            String tokenDecimals = getDecimals(web3j, contractAddr);
            String callFuncData = createTokenTransferData(to, amount, tokenDecimals);
            // set nonce
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), contractAddr, callFuncData);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            return boardNativeTx(web3j, hexValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String sendCallDataByNative(Web3j web3j, Credentials credentials, String contractAddr, String amount, String gasPrice, String gasLimit, String callFuncData) throws Exception {
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), contractAddr, new BigInteger(amount), callFuncData);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (200 != ethSendTransaction.getError().getCode()) {
            return ethSendTransaction.getError().getMessage();
        }
        return ethSendTransaction.getTransactionHash();
    }

    public String sendCallData(Web3j web3j, Credentials credentials, String contractAddr, String gasPrice, String gasLimit, String callFuncData) throws Exception {
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), contractAddr, callFuncData);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (200 != ethSendTransaction.getError().getCode()) {
            return ethSendTransaction.getError().getMessage();
        }
        return ethSendTransaction.getTransactionHash();
    }


    private String createTokenTransferData(String to, String tokenAmount, String decimals) {
        List<Type> inputParams = new ArrayList<>();
        List<TypeReference<?>> outputParams = new ArrayList<>();
        BigDecimal wei = new BigDecimal(10).pow(Integer.parseInt(decimals));
        BigDecimal multiply = wei.multiply(new BigDecimal(tokenAmount));
        inputParams.add(new Address(to));
        inputParams.add(new Uint256(multiply.toBigInteger()));
        Function function = new Function("transfer", inputParams, outputParams);
        return FunctionEncoder.encode(function);
    }


    /**
     * 根据交易哈希查询交易状态 0-未查询到(还未处理)，1-成功，2-失败
     *
     * @param web3j
     * @param txHash
     * @return
     */
    public int getTransactionStatus(Web3j web3j, String txHash) {
        try {
            TransactionReceipt result = web3j.ethGetTransactionReceipt(txHash).send().getResult();
            if (null == result) {
                return 0;
            } else if (result.getStatus().contains("1")) {
                return 1;
            }
            return 2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public BigInteger gasLimit(Web3j web3j, String index, String from, String to) {
        try {
            String data = getData(index);
            Transaction transaction = Transaction.createFunctionCallTransaction(from, null, null, null, to, data);
            EthEstimateGas send = web3j.ethEstimateGas(transaction).send();
            BigInteger amountUsed = send.getAmountUsed();
            return amountUsed;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getData(String index) {
        List<Type> inputParams = new ArrayList<>();
        List<TypeReference<?>> outputParams = new ArrayList<>();
        inputParams.add(new Uint256(Long.parseLong(index)));
        Function function = new Function("mint", inputParams, outputParams);
        return FunctionEncoder.encode(function);
    }

    public BigInteger nonceAt(Web3j web3j, String addr) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    addr, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            return nonce;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> getGasPrice(Web3j web3j) {
        try {
            EthGasPrice gasPrice = web3j.ethGasPrice().send();
            String slowGas = gasPrice.getGasPrice().toString();
            HashMap<String, String> res = new LinkedHashMap<>();
            res.put("slow", slowGas);
            res.put("mid", BigDecimalUtils.calculateGas(slowGas, "1.05"));
            res.put("fast", BigDecimalUtils.calculateGas(slowGas, "1.2"));
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNativeGasLimit() {
        return DefaultInfo.DEFAULT_GAS_LIMIT;
    }

    public String getTokenGasLimit() {
        return DefaultInfo.DEFAULT_ERC20_GAS_LIMIT;
    }

    public String getGasInfo(String gasPrice, String gasLimit) {
        return BigDecimalUtils.calculateETHGas(gasPrice, gasLimit, 18);
    }

    /**
     * get token balance
     *
     * @param web3j
     * @param from
     * @param conAddr
     * @return
     */
    public String getTokenBalance(Web3j web3j, String from, String conAddr) {

        List input = Arrays.asList(new Address(from));
        List output = Arrays.asList(new TypeReference<Uint256>() {
        });
        List result = readContract(web3j, from, conAddr, "balanceOf", input, output);
        if (null != result && result.size() > 0) {
            String decimals = getDecimals(web3j, conAddr);
            Uint256 balance = (Uint256) result.get(0);
            return BigDecimalUtils.calculateBalance(balance.getValue().toString(), -1 * Integer.parseInt(decimals));
        }
        return "0";
    }

    public String getDecimals(Web3j web3j, String conAddr) {
        List input = Arrays.asList();
        List output = Arrays.asList(new TypeReference<Uint256>() {
        });
        List result = readContract(web3j, conAddr, conAddr, "decimals", input, output);
        Uint256 decimals = new Uint256(0);
        if (null != result && result.size() > 0) {
            decimals = (Uint256) result.get(0);
        }
        return decimals.getValue().toString();
    }

    private List<Type> readContract(Web3j web3j, String from, String conAddr, String funName, List<Type> input, List<TypeReference<?>> output) {
        try {
            Function function = new Function(funName, input, output);
            String data = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(from, conAddr, data),
                    DefaultBlockParameterName.LATEST).send();
            return FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createNativeTxSign(Web3j web3j, Credentials credentials, String to, String amount, String gasPrice, String gasLimit) throws Exception {
        return createNativeSign(web3j, credentials, to, amount, gasPrice, gasLimit);
    }

    public ReturnsEntity boardTx(Web3j web3j, String hexValue) throws Exception {
        return boardNativeTx(web3j, hexValue);
    }


    private String createNativeSign(Web3j web3j, Credentials credentials, String to, String amount, String gasPrice, String gasLimit) throws Exception {
        BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), to, Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger());
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        return hexValue;
    }

    private ReturnsEntity boardNativeTx(Web3j web3j, String hexValue) throws Exception {
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        ReturnsEntity entity = new ReturnsEntity();
        if (null != ethSendTransaction.getError() && 200 != ethSendTransaction.getError().getCode()) {
            entity.setCode(32000);
            entity.setMsg(ethSendTransaction.getError().getMessage());
        } else {
            entity.setCode(200);
            entity.setMsg(ethSendTransaction.getTransactionHash());
        }
        return entity;
    }
}
