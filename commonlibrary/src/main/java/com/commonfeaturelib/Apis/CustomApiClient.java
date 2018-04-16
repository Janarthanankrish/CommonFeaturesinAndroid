package com.commonfeaturelib.Apis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by janarthananr on 21/3/18.
 */

public class CustomApiClient {
    public String get(okhttp3.Request getRequest) throws IOException {
        String result = "";
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", "cookie-name=cookie-value")
                                .build();
                        return chain.proceed(authorized);
                    }
                }).connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
        okhttp3.Response getResponse = client.newCall(getRequest).execute();
        final String getbody = new String(getResponse.body().string());
        result = getbody;
        return result;
    }
}
