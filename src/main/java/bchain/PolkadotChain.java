/*package bchain;

public class PolkadotChain {
    private static PolkadotChain instance = new PolkadotChain();

    private PolkadotChain() {
    }

    public static  PolkadotChain getInstance() {
    return instance;
    }

    public Web3j initClient(string url) {

    }

    public BigDecimal balanceOf(Web3j web3j,String addr) {

    }

    public ReturnsEntity sendNativeTransaction(Web3j web3j, Credentials credentials, String to, String amount, String gasPrice, String gasLimit) throws Exception {
        // BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();
        // BigInteger chainId = web3j.ethChainId().send().getChainId();
        // RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, new BigInteger(gasPrice), new BigInteger(gasLimit), to, Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger());
        // byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
        // String hexValue = Numeric.toHexString(signedMessage);
        // return boardNativeTx(web3j, hexValue);
    }

    public ReturnsEntity sendTokenTransaction(Web3j web3j, Credentials credentials, String to, String contractAddr, String amount, String gasPrice, String gasLimit){

    }


    public int getTransactionStatus(Web3j web3j, String txHash){

    }


    private ReturnsEntity boardNativeTx(Web3j web3j, String hexValue) throws Exception {

    }


}

*/