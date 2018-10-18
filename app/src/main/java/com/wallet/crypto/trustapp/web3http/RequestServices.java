package com.wallet.crypto.trustapp.web3http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestServices {
    @GET("basil2style")
        //定义返回的方法，返回的响应体使用了ResponseBody
    Call<ResponseBody> getString();
}
