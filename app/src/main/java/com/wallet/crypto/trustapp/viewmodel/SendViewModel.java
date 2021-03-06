package com.wallet.crypto.trustapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.wallet.crypto.trustapp.entity.Transaction;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.router.ConfirmationRouter;

import java.math.BigInteger;

public class SendViewModel extends BaseViewModel {
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<Transaction> transaction = new MutableLiveData<>();
    private final ConfirmationRouter confirmationRouter;

    SendViewModel(ConfirmationRouter confirmationRouter) {
        this.confirmationRouter = confirmationRouter;
    }

    public void openConfirmation(Context context, String to, BigInteger amount, String contractAddress, int decimals, String symbol, boolean sendingTokens) {
        confirmationRouter.open(context, to, amount, contractAddress, decimals, symbol, sendingTokens);
    }

    //int callbackId, String recipient, String value, String nonce, String gasLimit, String gasPrice, String payload
    public void openConfirmation2(Context context, String to, BigInteger amountBigInteger,BigInteger gasPriceBigInteger,BigInteger gasLimitBigInteger, String contractAddress, int decimals, String symbol, boolean sendingTokens) {
        confirmationRouter.open2(context, to, amountBigInteger,gasPriceBigInteger,gasLimitBigInteger, contractAddress, decimals, symbol, sendingTokens);
    }
}
