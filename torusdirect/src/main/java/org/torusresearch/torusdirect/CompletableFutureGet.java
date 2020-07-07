package org.torusresearch.torusdirect;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class CompletableFutureGet {
    private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        CompletableFuture<Response> future = new CompletableFuture<>();
        client.newCall(request).enqueue(toCallback(future));

        Response response = future.get();
        System.out.println(response.body().string());
    }

    public Callback toCallback(CompletableFuture<Response> future) {
        return new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override public void onResponse(Call call, Response response) {
                future.complete(response);
            }
        };
    }

    public static void main(String... args) throws Exception {
        new CompletableFutureGet().run();
    }
}