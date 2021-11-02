package com.web.template.Utils.MailApi;

import com.web.template.Utils.APIBase;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Gmail extends APIBase {

    public static String host = "pop.gmail.com";// change accordingly
    public static String mailStoreType = "pop3";

    public static Folder listEmailsFromInbox() throws MessagingException {
        Store store = null;
        Folder folder= null;
        try {
            Properties props = System.getProperties();

            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");
            store.connect("imap.googlemail.com",configProperties.get("new_ui_email"), configProperties.get("gmail_pwd"));

            folder = (IMAPFolder) store.getFolder("inbox");

            if(!folder.isOpen())
                folder.open(Folder.READ_WRITE);

            return folder;

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folder;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    public static ArrayList<HashMap<String, String>> getAllUnreadMails(Folder folder) throws MessagingException, IOException {
        Message[] messages = folder.getMessages();
        ArrayList<HashMap<String, String>> mails = null;
        HashMap<String, String> content = null;
        for (int i=0; i < messages.length;i++) {

            System.out.println("*****************************************************************************");
            System.out.println("MESSAGE " + (i + 1) + ":");
            Message msg = messages[i];
            if (!msg.isSet(Flags.Flag.SEEN)) {
                System.out.println("Subject: " + msg.getSubject());
                System.out.println("From: " + msg.getFrom()[0]);
                System.out.println("To: " + msg.getAllRecipients()[0]);
                System.out.println("Date: " + msg.getReceivedDate());
                System.out.println("Size: " + msg.getSize());
                System.out.println("Body: \n" + getTextFromMessage(msg));
                System.out.println(msg.getContentType());
                content.put("subject",msg.getSubject());
                mails.add(content);
                msg.setFlag(Flags.Flag.SEEN, true);
            }
        }
        return mails;
    }

    public static String getLatestSubject() throws MessagingException {
        Folder folder = listEmailsFromInbox();
        String result = "";
        Message[] messages = folder.getMessages();
        Message msg =  messages[messages.length-1];
        if(!msg.isSet(Flags.Flag.SEEN)) {
            result = msg.getSubject();
            System.out.println("Subject: " + msg.getSubject());
        }
        return result;
    }

    public static String getLatestMsgBody() throws MessagingException, IOException {
        Folder folder = listEmailsFromInbox();
        String result = "";
        Message[] messages = folder.getMessages();
        Message msg =  messages[messages.length-1];
        if(!msg.isSet(Flags.Flag.SEEN)) {
            result = getTextFromMessage(msg);
            System.out.println("Body: " + getTextFromMessage(msg));
        }
        return result;
    }

    public static String getMailSnippet() throws MessagingException, IOException {
        Folder folder = listEmailsFromInbox();
        String result = "";
        Message[] messages = folder.getMessages();
        Message msg =  messages[messages.length-1];
        if(!msg.isSet(Flags.Flag.SEEN)) {
            result = getTextFromMessage(msg);
            System.out.println("Body: \n" + result);
        }
        return result;
    }

    public static int getInboxCount() throws MessagingException {
        Folder folder = listEmailsFromInbox();
        System.out.println("count: \n" + folder.getUnreadMessageCount());
        return folder.getUnreadMessageCount();
    }


    public static int getMailSubjectCount(String subject) throws IOException, MessagingException {
        Folder folder = listEmailsFromInbox();
        ArrayList<HashMap<String, String>> mails = getAllUnreadMails(folder);
        int counter = 0;
        for (int i = 0; i < mails.size(); i++) {
            if(mails.get(i).get("subject").equalsIgnoreCase(subject))
                counter++;
        }
        return counter;
    }
}
