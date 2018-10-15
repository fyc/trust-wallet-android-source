package com.wallet.crypto.trustapp.router;

import android.content.Context;
import android.content.Intent;

import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.ui.Web3ViewActivity;

import static com.wallet.crypto.trustapp.C.Key.WALLET;

public class MyWeb3Router {
    public void open(Context context, Wallet wallet) {
        Intent intent = new Intent(context, Web3ViewActivity.class);
//        intent.putExtra(WALLET, wallet);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
