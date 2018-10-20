package trust.web3.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import trust.web3.bean.EthBean;

public class EthRequestManager {
    /**
     * 自定义网络回调接口
     */
    public interface EthRequestCall {
        void success(String result) throws IOException;
    }

    /*// Request
    curl -X POST --data '{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":83}'

    // Result
    {
        "id":83,
            "jsonrpc": "2.0",
            "result": "0xc94" // 1207
    }*/
    public static void eth_blockNumber(EthRequestCall ethRequestCall) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":83}";
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    EthBean bean = GsonUtil.GsonToBean(response.body().string(), EthBean.class);
                    String result = bean.getResult();
                    ethRequestCall.success(result);
                }

                @Override
                public void failed(Call call, IOException e) {
                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }


    //获取 gasPrice
    /*// Request
    curl -X POST --data '{"jsonrpc":"2.0","method":"eth_gasPrice","params":[],"id":73}'

    // Result
    {
        "id":73,
            "jsonrpc": "2.0",
            "result": "0x09184e72a000" // 10000000000000
    }*/
    public static void eth_gasPrice(EthRequestCall ethRequestCall) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_gasPrice\",\"params\":[],\"id\":73}";
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    EthBean bean = GsonUtil.GsonToBean(response.body().string(), EthBean.class);
                    String result = bean.getResult();
                    ethRequestCall.success(result);
                }

                @Override
                public void failed(Call call, IOException e) {
                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }

    //获取 nonce
    /* // Request
     curl -X POST --data '{"jsonrpc":"2.0","method":"eth_getTransactionCount","params":["0xc94770007dda54cF92009BFF0dE90c06F603a09f","latest"],"id":1}'

     // Result
     {
         "id":1,
             "jsonrpc": "2.0",
             "result": "0x1" // 1
     }*/
    public static void eth_getTransactionCount(String from, EthRequestCall ethRequestCall) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\"%s\",\"latest\"],\"id\":%d}";
        params = String.format(params, from, 1);
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    EthBean bean = GsonUtil.GsonToBean(response.body().string(), EthBean.class);
                    String result = bean.getResult();
                    ethRequestCall.success(result);
                }

                @Override
                public void failed(Call call, IOException e) {
                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }

    //获取 gasLimit
    /*// Request
    curl -X POST --data '{"jsonrpc":"2.0","method":"eth_estimateGas","params":[{see above}],"id":1}'

    // Result
    {
        "id":1,
            "jsonrpc": "2.0",
            "result": "0x5208" // 21000
    }*/
    public static void eth_estimateGas(String from, String to, String value, String data, EthRequestCall ethRequestCall) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_estimateGas\",\"params\":[{" +
                "\"from\": \"%s\"," +
                "\"to\": \"%s\"," +
//                "  \"gas\": \"0x76c0\"," +
//                "\"gasPrice\": \"%s\"," +
                "\"value\": \"%s\"," +
                "\"data\": \"%s\"" +
                "}],\"id\":1}";
        params = String.format(params, from, to, value, data);
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    EthBean bean = GsonUtil.GsonToBean(response.body().string(), EthBean.class);
                    String result = bean.getResult();
                    ethRequestCall.success(result);
                }

                @Override
                public void failed(Call call, IOException e) {
                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }
}
