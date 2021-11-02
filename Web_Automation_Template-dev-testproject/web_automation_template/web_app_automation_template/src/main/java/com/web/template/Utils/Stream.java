package com.web.template.Utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import okio.Buffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.squareup.okhttp.ws.WebSocket.TEXT;

public class Stream implements WebSocketListener {
    private ArrayList<String> chunk = new ArrayList<>();
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    public Stream(String url) {
        System.out.println("url:= "+url);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("sec-websocket-protocol","mqtt")
                .build();
        WebSocketCall.create(client, request).enqueue(this);

        client.getDispatcher().getExecutorService().shutdown();
    }

    public ArrayList<String> getDataStream() {
        return chunk;
    }




    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    webSocket.close(1000, "Goodbye, World!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onFailure(IOException e, Response response) {
        System.out.println("FAILURE: ");
        e.printStackTrace();
        writeExecutor.shutdown();
    }

    @Override
    public void onMessage(ResponseBody responseBody) {
        try {
            System.out.println("MESSAGE: ");
            if (responseBody.contentType() == TEXT) {
                chunk.add(responseBody.string());
                System.out.println("chunk: ="+chunk);
            } else {
                System.out.println("MESSAGE: " + responseBody.source().readByteString().hex());
            }
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPong(Buffer buffer) {
        System.out.println("PONG: " + buffer.readUtf8());
    }

    @Override
    public void onClose(int i, String s) {
        System.out.println("CLOSE: " + i + " " + s);
        writeExecutor.shutdown();
    }
}
