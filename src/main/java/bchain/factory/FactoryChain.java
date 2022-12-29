package bchain.factory;

import bchain.impl.BTCChain;
import bchain.impl.EVMChain;

public class FactoryChain {

    public static BlockChain newBlockChain(String type) {
        if ("ETH".equals(type)) {
            return new EVMChain();
        } else if ("BTC".equals(type)) {
            return new BTCChain();
        } else if ("BNB".equals(type)) {
            return new EVMChain();
        }else if ("HT".equals(type)) {
            return new EVMChain();
        }else if("MATIC".equals(type)){
            return new EVMChain();
        }else if("AVAX".equals(type)){
            return new EVMChain();
        }else if("POLKADOT".equals(type)){
            return new EVMChain();
        }
        return null;
    }
}
