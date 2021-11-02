package com.web.template.Utils;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class APIBase {

    private OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final MediaType FORM = MediaType.parse("multipart/form-data");
    public static String baseUrl;
    public static HashMap<String, String>configProperties;


    public APIBase(){
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.connectTimeout(90, TimeUnit.SECONDS);
        b.readTimeout(90, TimeUnit.SECONDS);
        b.writeTimeout(90, TimeUnit.SECONDS);
        client = b.build();
    }

    public  Response get(String url, HashMap<String, String> headers) throws IOException{

        Request.Builder request = new Request.Builder()
                .url(url)
                .get();
        for(Map.Entry<String,String> map:headers.entrySet()){
            String key=map.getKey();
            String value=map.getValue();
            request.addHeader(key,value);
        }
        Request getRequest = request.build();
        Response response = client.newCall(getRequest).execute();
        return response;
    }

    public  Response jsonPost(String url, HashMap<String,String> headers, String json) throws IOException{
        RequestBody body = RequestBody.create(JSON,json);
        Request.Builder request = new Request.Builder()
                .url(url)
                .post(body);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            //to get key
            String key = e.getKey();
            //and to get value
            String value = e.getValue();
            request.addHeader(key, value);
        }
        Request postRequest = request.build();
        Response response = client.newCall(postRequest).execute();
        return response;
    }

    public  Response formurlencodedPost(String url, HashMap<String,String> headers, HashMap<String,String> formData) throws IOException{
        FormBody.Builder formBody = new FormBody.Builder();
        String form = "";
        for (Map.Entry<String, String> e : formData.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            formBody.addEncoded(key,value);
        }

        RequestBody body = formBody.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        for(Map.Entry<String, String> e : headers.entrySet()){
                String key=e.getKey();
                String value=e.getValue();
                requestBuilder.addHeader(key,value);
        }
        //MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = requestBuilder
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public  Response formurlencodedPut(String url, HashMap<String,String> headers, HashMap<String,String> formData) throws IOException{
        FormBody.Builder formBody = new FormBody.Builder();
        String form = "";
        for (Map.Entry<String, String> e : formData.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            formBody.add(key,value);
        }

        RequestBody body = formBody.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        for(Map.Entry<String, String> e : headers.entrySet()){
            String key=e.getKey();
            String value=e.getValue();
            requestBuilder.addHeader(key,value);
        }
        //MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = requestBuilder
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }


    public JSONObject parse(Response dataResponse){
        JSONParser parser = new JSONParser();
        JSONObject data = null;
        try{
            String jsonData = dataResponse.body().string();
            data =  (JSONObject) parser.parse(jsonData);
        }catch (ParseException e){
            System.out.println("JSON Parse Exception Occured");
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println("IO Exception Occured");
            System.out.println(e);
        }

        return data;
    }

}
