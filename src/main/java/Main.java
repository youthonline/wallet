import bchain.BTCChain;
import org.bitcoinj.params.TestNet3Params;
import utils.GenerateWalletKeyUtil;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

public class Main {

    public static void main(String[] args) {
        try {

            BTCChain instance = BTCChain.getInstance();
            BitcoinJSONRPCClient client = instance.initUrl("https://vertu2022:omnivertu2022@btc.mainnet.valuewallet.co");
            String btcBalance = instance.getBTCBalance(client, "19nKp7ZfW4V4kr9k1KNbuJCwGWvdFJxvKF");
            System.out.println(btcBalance);
            String usdtBalance = instance.getUsdtBalance(client, "19nKp7ZfW4V4kr9k1KNbuJCwGWvdFJxvKF", 31);
            System.out.println(usdtBalance);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
