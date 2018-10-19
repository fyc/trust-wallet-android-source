package trust.web3;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;

import okhttp3.Call;
import okhttp3.Response;
import trust.core.entity.Address;
import trust.core.entity.Message;
import trust.core.entity.Transaction;
import trust.core.entity.TypedData;
import trust.core.util.Hex;
import trust.web3.bean.EthBean;
import trust.web3.utils.GsonUtil;
import trust.web3.utils.OkHttpUtils;

public class SignCallbackJSInterface {

    private final WebView webView;
    @NonNull
    private final OnSignTransactionListener onSignTransactionListener;
    @NonNull
    private final OnSignMessageListener onSignMessageListener;
    @NonNull
    private final OnSignPersonalMessageListener onSignPersonalMessageListener;
    @NonNull
    private final OnSignTypedMessageListener onSignTypedMessageListener;

    public SignCallbackJSInterface(
            WebView webView,
            @NonNull OnSignTransactionListener onSignTransactionListener,
            @NonNull OnSignMessageListener onSignMessageListener,
            @NonNull OnSignPersonalMessageListener onSignPersonalMessageListener,
            @NonNull OnSignTypedMessageListener onSignTypedMessageListener) {
        this.webView = webView;
        this.onSignTransactionListener = onSignTransactionListener;
        this.onSignMessageListener = onSignMessageListener;
        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
        this.onSignTypedMessageListener = onSignTypedMessageListener;
    }

    @JavascriptInterface
    public void signTransaction(
            int callbackId,
            String recipient,
            String value,
            String nonce,
            String gasLimit,
            String gasPrice,
            String payload) {


        Transaction transaction = new Transaction(
                TextUtils.isEmpty(recipient) ? Address.EMPTY : new Address(recipient),
                null,
                Hex.hexToBigInteger(value),
                Hex.hexToBigInteger(gasPrice, BigInteger.ZERO),
                Hex.hexToLong(gasLimit, 0),
                Hex.hexToLong(nonce, -1),
                payload,
                callbackId);
        onSignTransactionListener.onSignTransaction(transaction);

    }

    int callbackId;
    String value;
    String recipient;
    String payload;
    String from;

    String gasLimit;
    String nonce;
    String gasPrice;

    @JavascriptInterface
    public void signTransaction2(
            int callbackId,
            String value,
            String recipient,
            String payload,
            String from
    ) {

        this.callbackId = callbackId;
        this.value = value;
        this.recipient = recipient;
        this.payload = payload;
        this.from = from;
        eth_gasPrice();
    }

    //生成Transaction
    private Transaction gerTransaction(int callbackId,
                                       String recipient,
                                       String value,
                                       String nonce,
                                       String gasLimit,
                                       String gasPrice,
                                       String payload) {
        Transaction transaction = new Transaction(
                TextUtils.isEmpty(recipient) ? Address.EMPTY : new Address(recipient),
                null,
                Hex.hexToBigInteger(value),
                Hex.hexToBigInteger(gasPrice, BigInteger.ZERO),
                Hex.hexToLong(gasLimit, 0),
                Hex.hexToLong(nonce, -1),
                payload,
                callbackId);
        return transaction;
    }

    public void eth_blockNumber() {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":83}";
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String a = response.body().string();
                    Log.d("post http", "a===" + a);
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
    public void eth_gasPrice() {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_gasPrice\",\"params\":[],\"id\":73}";
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String resultStr = response.body().string();
                    EthBean bean = GsonUtil.GsonToBean(resultStr, EthBean.class);
                    gasPrice = bean.getResult();
                    SignCallbackJSInterface.this.eth_estimateGas(from, recipient, gasPrice, value, payload);
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
    public void eth_getTransactionCount(String from) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\"%s\",\"latest\"],\"id\":%d}";
        params = String.format(params, from, 1);
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String resultStr = response.body().string();
                    EthBean bean = GsonUtil.GsonToBean(resultStr, EthBean.class);
                    nonce = bean.getResult();
//                    Transaction transaction = gerTransaction(callbackId,recipient,value,nonce,gasLimit,gasPrice,payload);
//                    onSignTransactionListener.onSignTransaction(transaction);

//                    Transaction transaction = gerTransaction(callbackId,recipient,value,nonce,gasLimit,gasPrice,payload);
                    onSignTransactionListener.onSignTransaction2(callbackId,recipient,value,nonce,gasLimit,gasPrice,payload);
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
    public void eth_estimateGas(String from, String to, String gasPrice, String value, String data) {
        String url = "https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj";
        String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_estimateGas\",\"params\":[{" +
                "\"from\": \"%s\"," +
                "\"to\": \"%s\"," +
//                "  \"gas\": \"0x76c0\"," +
                "\"gasPrice\": \"%s\"," +
                "\"value\": \"%s\"," +
                "\"data\": \"%s\"" +
                "}],\"id\":1}";
        params = String.format(params, from, to, gasPrice, value, data);
        try {
            OkHttpUtils.getInstance().postJsonAsyn(url, params, new OkHttpUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String resultStr = response.body().string();
                    EthBean bean = GsonUtil.GsonToBean(resultStr, EthBean.class);
                    gasLimit = bean.getResult();
                    SignCallbackJSInterface.this.eth_getTransactionCount(from);
                }

                @Override
                public void failed(Call call, IOException e) {

                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }

    @JavascriptInterface
    public void signMessage(int callbackId, String data) {
        webView.post(() -> onSignMessageListener.onSignMessage(new Message<>(data, getUrl(), callbackId)));
    }

    @JavascriptInterface
    public void signPersonalMessage(int callbackId, String data) {
        webView.post(() -> onSignPersonalMessageListener.onSignPersonalMessage(new Message<>(data, getUrl(), callbackId)));
    }

    @JavascriptInterface
    public void signTypedMessage(int callbackId, String data) {
        webView.post(() -> {
            TrustProviderTypedData[] rawData = new Gson().fromJson(data, TrustProviderTypedData[].class);
            int len = rawData.length;
            TypedData[] typedData = new TypedData[len];
            for (int i = 0; i < len; i++) {
                typedData[i] = new TypedData(rawData[i].name, rawData[i].type, rawData[i].value);
            }
            onSignTypedMessageListener.onSignTypedMessage(new Message<>(typedData, getUrl(), callbackId));
        });
    }

    private String getUrl() {
        return webView == null ? "" : webView.getUrl();
    }

    private static class TrustProviderTypedData {
        public String name;
        public String type;
        public Object value;
    }
}
