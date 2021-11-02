package com.web.template.Utils.MailApi;

import com.web.template.Utils.APIBase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class Mailcc extends APIBase {

    public static OkHttpClient client;


    public Mailcc(){
        client = new OkHttpClient();
    }

    public static ArrayList<HashMap<String,String>> listEmailsFromInbox(String inbox) throws ParseException, IOException, org.json.simple.parser.ParseException {

        client=new OkHttpClient();
        System.out.println("Inside get Emails:"+inbox);

        ArrayList<HashMap<String,String>> emails   = new ArrayList<HashMap<String,String>>();
        Request request = new Request.Builder()
                .url("https://api.maildrop.cc/v2/mailbox/"+inbox)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", "QM8VTHrLR2JloKTJMZ3N6Qa93FVsx8LapKCzEjui")
                .build();

        System.out.println("request: "+request);
        Response response = client.newCall(request).execute();
        System.out.println("Inside get Emails after");
        //System.out.println(response.body().string());
        JSONParser parser = new JSONParser();
        String jsonData = response.body().string();
        JSONObject data = (JSONObject) parser.parse(jsonData);
        System.out.println(data);
        emails = (ArrayList<HashMap<String,String>>) data.get("messages");
//        String subject= emails.get(0).get("subject");
//        System.out.println(subject);
        return emails;
    }



    public HashMap<String,String> readEmailFromInbox(String id, String inbox) throws ParseException, IOException{
        HashMap<String, String>email = new HashMap<String, String>();
        Request request = new Request.Builder()
                .url("https://api.maildrop.cc/v2/mailbox/"+inbox+"/"+id)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", "QM8VTHrLR2JloKTJMZ3N6Qa93FVsx8LapKCzEjui")
                .build();
        System.out.println("https://api.maildrop.cc/v2/mailbox/"+inbox+"/"+id);
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        JSONParser parser = new JSONParser();
        return email;
    }

    public static String getLatestSubject(String email) throws Exception{
        ArrayList<HashMap<String,String>> emails   = new ArrayList<HashMap<String,String>>();
        emails= Mailcc.listEmailsFromInbox(email);
        String subject= emails.get(0).get("subject");
        System.out.println(subject);
        return subject;
    }


    public static String getLatestMsgBody(String email) throws Exception{
        ArrayList<HashMap<String,String>> emails   = new ArrayList<HashMap<String,String>>();
        emails= Mailcc.listEmailsFromInbox(email);
        String id= emails.get(0).get("id");
        Request request = new Request.Builder()
                .url("https://api.maildrop.cc/v2/mailbox/"+email+"/"+id)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", "QM8VTHrLR2JloKTJMZ3N6Qa93FVsx8LapKCzEjui")
                .build();
        Response response = client.newCall(request).execute();
        String jsonData=response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(jsonData);
        String body = (String) data.get("html");
        System.out.println(body);
        return body;
    }

    public static int getInboxCount(String email) throws Exception{
        ArrayList<HashMap<String,String>> emails   = new ArrayList<HashMap<String,String>>();
        emails= Mailcc.listEmailsFromInbox(email);
        System.out.println(emails.size());
        return emails.size();
    }


    public static int getMailSubjectCount(String subject, String username) throws ParseException, org.json.simple.parser.ParseException, IOException {
        ArrayList<HashMap<String,String>> mails   = new ArrayList<HashMap<String,String>>();
        mails= Mailcc.listEmailsFromInbox(username);
        int counter = 0;
        for (int i = 0; i < mails.size(); i++) {
            if(mails.get(i).get("subject").equalsIgnoreCase(subject))
                counter++;
        }
        return counter;
    }

}
