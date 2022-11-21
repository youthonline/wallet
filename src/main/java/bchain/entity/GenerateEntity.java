package bchain.entity;

public class GenerateEntity {

    private String mnemonics;

    private String privateKey;

    private String address;

    private String fileName;

    private int id;

    public String getMnemonics() {
        return mnemonics;
    }

    public void setMnemonics(String mnemonics) {
        this.mnemonics = mnemonics;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GenerateEntity{" +
                "mnemonics='" + mnemonics + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", address='" + address + '\'' +
                ", fileName='" + fileName + '\'' +
                ", id=" + id +
                '}';
    }
}
