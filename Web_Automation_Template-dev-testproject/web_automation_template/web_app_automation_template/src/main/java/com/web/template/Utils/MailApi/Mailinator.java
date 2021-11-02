package com.web.template.Utils.MailApi;

import com.web.template.Utils.Stream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Mailinator {

    public static OkHttpClient client;
    ArrayList<String> values;
    ArrayList<HashMap<String, String>> mails;

    public Mailinator() {
        client = new OkHttpClient();
    }

    public ArrayList<HashMap<String, String>> mailData(String mail) throws IOException, InterruptedException {
        client = new OkHttpClient();
        System.out.println("wss://www.mailinator.com/ws/fetchinbox?zone=public&query=" + mail);
        Stream stream = new Stream(java.net.URLDecoder.decode("wss://www.mailinator.com/ws/fetchinbox?zone=public&query=" + mail, StandardCharsets.UTF_8.name()));
        TimeUnit.SECONDS.sleep(2);
        values = stream.getDataStream();
        System.out.println("values := " + values);
        mails = ConvertStringtoHashMap(values);
        return ConvertStringtoHashMap(values);
    }


    public ArrayList<HashMap<String, String>> listEmailsFromInbox(){
        return ConvertStringtoHashMap(values);
    }

    public String getLatestSubject(String mail) throws IOException, InterruptedException {
        mailData(mail);
        ArrayList<HashMap<String,String>> listEmailsFromInbox = ConvertStringtoHashMap(values);
        System.out.println("allMails:= " + listEmailsFromInbox.get(listEmailsFromInbox.size()-1).get("subject"));
        return listEmailsFromInbox.get(listEmailsFromInbox.size()-1).get("subject");
    }

    public String getLatestMsgBody(String mail) throws IOException, InterruptedException, ParseException {
        mailData(mail);
        ArrayList<HashMap<String,String>> listEmailsFromInbox = ConvertStringtoHashMap(values);
        System.out.println("allMails:= " + listEmailsFromInbox.get(listEmailsFromInbox.size()-1).get("id"));
        String id= listEmailsFromInbox.get(listEmailsFromInbox.size()-1).get("id");
        Request request = new Request.Builder()
                .url("https://www.mailinator.com/fetch_email?msgid="+id+"&zone=public")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String jsonData=response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(jsonData);
        String body = ((ArrayList<HashMap<String, String>>)((HashMap<String, Object>) data.get("data")).get("parts")).get(0).get("body");
        return body;
    }


    public int getInboxCount(String mail) throws IOException, InterruptedException {
        mailData(mail);
        return ConvertStringtoHashMap(values).size();
    }


    public ArrayList<HashMap<String, String>> ConvertStringtoHashMap(ArrayList<String> value) {
        ArrayList<HashMap<String, String>> allMails = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            if (value.get(i).contains("inbox_status"))
                continue;
            String[] keyValuePairs = StringUtils.substringBetween(value.get(i), "{", "}").replace("\"", "").split(",");

            HashMap<String, String> map = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                if (entry.length > 1)
                    map.put(entry[0].trim(), entry[1].trim());
            }
            allMails.add(map);
        }
        return allMails;
    }


    public int getMailSubjectCount(String subject, String mail) throws IOException, InterruptedException {
        mailData(mail);
        int counter = 0;
        for (int i = 0; i < mails.size(); i++) {
            if(mails.get(i).get("subject").equalsIgnoreCase(subject))
                counter++;
        }
        return counter;
    }


}
