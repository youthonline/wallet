package bchain.entity;

public class BlockChainInfoEntity {

    private String outputIndex;
    private String txHash;
    private String txHashBigEndian;
    private String valueHex;
    private String confirmations;
    private String txIndex;
    private String value;
    private String script;

    public BlockChainInfoEntity(String outputIndex, String txHash, String txHashBigEndian, String valueHex, String confirmations, String txIndex, String value, String script) {
        this.outputIndex = outputIndex;
        this.txHash = txHash;
        this.txHashBigEndian = txHashBigEndian;
        this.valueHex = valueHex;
        this.confirmations = confirmations;
        this.txIndex = txIndex;
        this.value = value;
        this.script = script;
    }


    public String getOutputIndex() {
        return outputIndex;
    }

    public void setOutputIndex(String outputIndex) {
        this.outputIndex = outputIndex;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTxHashBigEndian() {
        return txHashBigEndian;
    }

    public void setTxHashBigEndian(String txHashBigEndian) {
        this.txHashBigEndian = txHashBigEndian;
    }

    public String getValueHex() {
        return valueHex;
    }

    public void setValueHex(String valueHex) {
        this.valueHex = valueHex;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

    public String getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(String txIndex) {
        this.txIndex = txIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String toString() {
        return "BlockChainInfoEntity{" +
                "outputIndex='" + outputIndex + '\'' +
                ", txHash='" + txHash + '\'' +
                ", txHashBigEndian='" + txHashBigEndian + '\'' +
                ", valueHex='" + valueHex + '\'' +
                ", confirmations='" + confirmations + '\'' +
                ", txIndex='" + txIndex + '\'' +
                ", value='" + value + '\'' +
                ", script='" + script + '\'' +
                '}';
    }
}
