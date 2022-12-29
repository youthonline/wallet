import bchain.BTCChain;
import org.bitcoinj.params.TestNet3Params;
import utils.GenerateWalletKeyUtil;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

// import io.emeraldpay.polkaj.api.PolkadotApi;
// import io.emeraldpay.polkaj.apihttp.JavaHttpAdapter;

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
        

        // try {
        //     // PolkadotApi api = PolkadotApi.newBuilder().rpcCallAdapter(JavaHttpAdapter.newBuilder().build()).build();
        //     PolkadotApi api = PolkadotApi.newBuilder()
        // .rpcCallAdapter(JavaHttpAdapter.newBuilder()
        //     .rpcCoder(new RpcCoder(objectMapper)) // (1)
        //     .connectTo("http://10.0.1.20:9333") // (2)
        //     .basicAuth("alice", "secret") // (3)
        //     .build())
        // .build();
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }

    }


}
