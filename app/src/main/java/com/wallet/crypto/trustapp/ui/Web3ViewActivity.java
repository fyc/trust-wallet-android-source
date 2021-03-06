package com.wallet.crypto.trustapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wallet.crypto.trustapp.BuildConfig;
import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.util.BalanceUtils;
import com.wallet.crypto.trustapp.viewmodel.SendViewModel;
import com.wallet.crypto.trustapp.viewmodel.SendViewModelFactory;

import java.math.BigInteger;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import trust.Call;
import trust.SignMessageRequest;
import trust.SignPersonalMessageRequest;
import trust.SignTransactionRequest;
import trust.SignTypedMessageRequest;
import trust.Trust;
import trust.core.entity.Address;
import trust.core.entity.Message;
import trust.core.entity.Transaction;
import trust.core.entity.TypedData;
import trust.core.util.Hex;
import trust.web3.OnSignMessageListener;
import trust.web3.OnSignPersonalMessageListener;
import trust.web3.OnSignTransactionListener;
import trust.web3.OnSignTypedMessageListener;
import trust.web3.Web3View;
import trust.web3.utils.LoadingDialogUtils;

public class Web3ViewActivity extends AppCompatActivity implements
        OnSignTransactionListener, OnSignPersonalMessageListener, OnSignTypedMessageListener, OnSignMessageListener {

    @Inject
    SendViewModelFactory sendViewModelFactory;
    SendViewModel viewModel;

    private TextView url;
    private Web3View web3;
    private Call<SignMessageRequest> callSignMessage;
    private Call<SignPersonalMessageRequest> callSignPersonalMessage;
    private Call<SignTypedMessageRequest> callSignTypedMessage;
    private Call<SignTransactionRequest> callSignTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web3view);
        LoadingDialogUtils.init(this);

        viewModel = ViewModelProviders.of(this, sendViewModelFactory)
                .get(SendViewModel.class);

        url = findViewById(R.id.url);
        web3 = findViewById(R.id.web3view);
        findViewById(R.id.go).setOnClickListener(v -> {
            web3.loadUrl(url.getText().toString());
            web3.requestFocus();
        });

        setupWeb3();
    }

    private void setupWeb3() {
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
//        web3.setChainId(1);
//        web3.setRpcUrl("https://mainnet.infura.io/llyrtzQ3YhkdESt2Fzrk");

        web3.setChainId(42);
        web3.setRpcUrl("https://kovan.infura.io/EjkWWVCqSgNOgYy7BdNj");

//        web3.setWalletAddress(new Address("0x242776e7ca6271e416e737adffcfeb22e8dc1b3c"));
        web3.setWalletAddress(new Address("0x6e6e5483757572319830e131cbca4921d24ad045"));
        web3.setOnSignMessageListener(message ->
                callSignMessage = Trust.signMessage().message(message).call(this));
        web3.setOnSignPersonalMessageListener(message ->
                callSignPersonalMessage = Trust.signPersonalMessage().message(message).call(this));
//        web3.setOnSignTransactionListener(transaction ->
//                callSignTransaction = Trust.signTransaction().transaction(transaction).call(this));
        web3.setOnSignTransactionListener(new OnSignTransactionListener() {
            @Override
            public void onSignTransaction(Transaction transaction) {
                callSignTransaction = Trust.signTransaction().transaction(transaction).call(Web3ViewActivity.this);
            }

            @Override
            public void onSignTransaction2(int callbackId, String recipient, String value, String nonce, String gasLimit, String gasPrice, String payload) {
//                BigInteger amountInSubunits = BalanceUtils.baseToSubunit("0.002", C.ETHER_DECIMALS);
                BigInteger amountBigInteger = Hex.hexToBigInteger(value);
                BigInteger gasPriceBigInteger = Hex.hexToBigInteger(gasPrice, BigInteger.ZERO);
                BigInteger gasLimitBigInteger = Hex.hexToBigInteger(gasLimit, BigInteger.ZERO);
                viewModel.openConfirmation2(Web3ViewActivity.this, recipient, amountBigInteger,gasPriceBigInteger,gasLimitBigInteger, null, C.ETHER_DECIMALS, C.ETH_SYMBOL, false);
            }
        });
        web3.setOnSignTypedMessageListener(message ->
                callSignTypedMessage = Trust.signTypedMessage().message(message).call(this));
    }

    private void loadUrl(){
        web3.loadUrl(url.getText().toString());
        web3.requestFocus();
    }

    @Override
    public void onSignMessage(Message<String> message) {
        Toast.makeText(this, message.value, Toast.LENGTH_LONG).show();
        web3.onSignCancel(message);
    }

    @Override
    public void onSignPersonalMessage(Message<String> message) {
        Toast.makeText(this, message.value, Toast.LENGTH_LONG).show();
        web3.onSignCancel(message);
    }

    @Override
    public void onSignTypedMessage(Message<TypedData[]> message) {
        Toast.makeText(this, new Gson().toJson(message), Toast.LENGTH_LONG).show();
        web3.onSignCancel(message);
    }

    @Override
    public void onSignTransaction(Transaction transaction) {
        String str = new StringBuilder()
                .append(transaction.recipient == null ? "" : transaction.recipient.toString()).append(" : ")
                .append(transaction.contract == null ? "" : transaction.contract.toString()).append(" : ")
                .append(transaction.value.toString()).append(" : ")
                .append(transaction.gasPrice.toString()).append(" : ")
                .append(transaction.gasLimit).append(" : ")
                .append(transaction.nonce).append(" : ")
                .append(transaction.payload).append(" : ")
                .toString();
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        web3.onSignCancel(transaction);
    }

    @Override
    public void onSignTransaction2(int callbackId, String recipient, String value, String nonce, String gasLimit, String gasPrice, String payload) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callSignTransaction != null) {
            callSignTransaction.onActivityResult(requestCode, resultCode, data, response -> {
                Transaction transaction = response.request.body();
                if (response.isSuccess()) {
                    web3.onSignTransactionSuccessful(transaction, response.result);
                } else {
                    if (response.error == Trust.ErrorCode.CANCELED) {
                        web3.onSignCancel(transaction);
                    } else {
                        web3.onSignError(transaction, "Some error");
                    }
                }
            });
        }

        if (callSignMessage != null) {
            callSignMessage.onActivityResult(requestCode, resultCode, data, response -> {
                Message message = response.request.body();
                if (response.isSuccess()) {
                    web3.onSignMessageSuccessful(message, response.result);
                } else {
                    if (response.error == Trust.ErrorCode.CANCELED) {
                        web3.onSignCancel(message);
                    } else {
                        web3.onSignError(message, "Some error");
                    }
                }
            });
        }

        if (callSignPersonalMessage != null) {
            callSignPersonalMessage.onActivityResult(requestCode, resultCode, data, response -> {
                Message message = response.request.body();
                if (response.isSuccess()) {
                    web3.onSignMessageSuccessful(message, response.result);
                } else {
                    if (response.error == Trust.ErrorCode.CANCELED) {
                        web3.onSignCancel(message);
                    } else {
                        web3.onSignError(message, "Some error");
                    }
                }
            });
        }

        if (callSignTypedMessage != null) {
            callSignTypedMessage.onActivityResult(requestCode, resultCode, data, response -> {
                Message message = response.request.body();
                if (response.isSuccess()) {
                    web3.onSignMessageSuccessful(message, response.result);
                } else {
                    if (response.error == Trust.ErrorCode.CANCELED) {
                        web3.onSignCancel(message);
                    } else {
                        web3.onSignError(message, "Some error");
                    }
                }
            });
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        web3.pauseTimers();
        if(isFinishing()){
            web3.loadUrl("about:blank");
            setContentView(new FrameLayout(this));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        web3.resumeTimers();
        loadUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingDialogUtils.unInit();
    }
}
