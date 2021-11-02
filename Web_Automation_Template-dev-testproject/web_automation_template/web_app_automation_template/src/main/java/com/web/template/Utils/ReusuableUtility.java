package com.web.template.Utils;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReusuableUtility {

    private static ReusuableUtility obj;

    private ReusuableUtility() {
    }

    ;

    public static ReusuableUtility getInstance() {
        if (obj == null)
            obj = new ReusuableUtility();
        return obj;

    }

    public String executeCommand(String command) {
        try {
            //ProcessBuilder is recommended if dependency on environment variable is high.
//            Process process = Runtime.getRuntime().exec(command);
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-l", "-c", command});

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String parsedResult = "";
            while ((parsedResult = reader.readLine()) != null) {
                sb.append(parsedResult);

            }

            return sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";

    }

    public String executeCommand(String[] command) {
        try {
            //ProcessBuilder is recommended if dependency on environment variable is high.
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String parsedResult = "";
            while ((parsedResult = reader.readLine()) != null) {
                sb.append(parsedResult);

            }

            return sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";

    }


    public List<String> convertListIntoString(List<WebElement> ListOfElements) {
        return ListOfElements.stream().filter(x -> !x.equals("")).map(WebElement::getText).collect(Collectors.toList());
    }

    public boolean compareTwoListAreSame(List<String> list1, List<String> list2) {
        try {
            return CollectionUtils.isEqualCollection(list1, list2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> replaceAValueInAList(List<String> listOfValues, String oldValue, String newValue) {
        if (listOfValues.contains(oldValue))
            listOfValues.set(listOfValues.indexOf(oldValue), newValue);
        return listOfValues;
    }

    public Map<String, String> replaceAValueInAMap(Map<String, String> listOfValues, String oldValue, String newValue) {
        if (listOfValues.containsKey(oldValue)) {
            listOfValues.put(newValue, listOfValues.get(oldValue));
            listOfValues.remove(oldValue);
        }
        return listOfValues;
    }

    /* Convert long type milliseconds to format hh:mm:ss */
    public String convertTimeToString(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    public static String getCurrentDateTime() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy:HH.mm.ss");
        return formatter.format(currentDate.getTime());
    }
}