package bchain.entity;

public class UnSpentEntity {

    private String txId;
    private int vout;
    private String address;
    private String label;
    private String scriptPubKey;
    private double amount;
    private int confirmations;
    private boolean spendable;
    private boolean solvable;
    private boolean safe;

    public UnSpentEntity(String txId, int vout, String address, String label, String scriptPubKey, double amount, int confirmations, boolean spendable, boolean solvable, boolean safe) {
        this.txId = txId;
        this.vout = vout;
        this.address = address;
        this.label = label;
        this.scriptPubKey = scriptPubKey;
        this.amount = amount;
        this.confirmations = confirmations;
        this.spendable = spendable;
        this.solvable = solvable;
        this.safe = safe;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public int getVout() {
        return vout;
    }

    public void setVout(int vout) {
        this.vout = vout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public boolean isSpendable() {
        return spendable;
    }

    public void setSpendable(boolean spendable) {
        this.spendable = spendable;
    }

    public boolean isSolvable() {
        return solvable;
    }

    public void setSolvable(boolean solvable) {
        this.solvable = solvable;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    public String toString() {
        return "UnSpentEntity{" +
                "txId='" + txId + '\'' +
                ", vout=" + vout +
                ", address='" + address + '\'' +
                ", label='" + label + '\'' +
                ", scriptPubKey='" + scriptPubKey + '\'' +
                ", amount=" + amount +
                ", confirmations=" + confirmations +
                ", spendable=" + spendable +
                ", solvable=" + solvable +
                ", safe=" + safe +
                '}';
    }
}
