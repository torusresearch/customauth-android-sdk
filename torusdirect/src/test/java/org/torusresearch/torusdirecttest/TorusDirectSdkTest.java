package org.torusresearch.torusdirecttest;

import okhttp3.HttpUrl;

public class TorusDirectSdkTest {
    public static void main(String[] args) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.google.com")
                .addPathSegment("search")
                .addQueryParameter("q", "polar bears")
                .build();
        System.out.println(url);
    }
}
