package trust.web3.bean;

public class EthBean {

    /**
     * id : 73
     * jsonrpc : 2.0
     * result : 0x09184e72a000
     */

    private int id;
    private String jsonrpc;
    private String result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
