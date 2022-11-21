package common;

import org.bitcoinj.core.Coin;
import org.bitcoinj.script.Script;

import java.io.Serializable;

public class UnSpentUtxo implements Serializable {
	    
	private static final long serialVersionUID = -7417428486644921613L;
	
	private String hash;
	private long txN;
	private String value;
	private int height;
	private String script;
	private String address;


	public UnSpentUtxo(String hash, long txN, String value, int height, String script, String address) {
		this.hash = hash;
		this.txN = txN;
		this.value = value;
		this.height = height;
		this.script = script;
		this.address = address;
	}

	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public long getTxN() {
		return txN;
	}
	public void setTxN(long txN) {
		this.txN = txN;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "UnSpentUtxo{" +
				"hash='" + hash + '\'' +
				", txN=" + txN +
				", value=" + value +
				", height=" + height +
				", script='" + script + '\'' +
				", address='" + address + '\'' +
				'}';
	}
}
