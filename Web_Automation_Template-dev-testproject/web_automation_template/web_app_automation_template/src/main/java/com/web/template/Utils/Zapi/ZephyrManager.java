package com.web.template.Utils.Zapi;

import com.web.template.Utils.APIBase;
import com.web.template.Utils.PropertyReader;
import com.thed.zephyr.cloud.rest.ZFJCloudRestClient;
import com.thed.zephyr.cloud.rest.client.JwtGenerator;
import okhttp3.Credentials;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
    @desc      This class holds different zephyr api methods
    @author    Ostan Dsouza
    @Date      02/06/2020
 */

public class ZephyrManager extends APIBase {

    private int MAX_BULK_OPERATION_COUNT = 50;
    private int expirationInSec = 1800;
    private HashMap<String,String> zephyrProperties= PropertyReader.getInstance().getPropValues("zephyr.properties");
    private JSONObject data;
    HttpResponse response = null;


    public Long getProjectIdByName(String projectName) {
        Long projectId = 0L;

        try {
            String url = zephyrProperties.get("Jirarurl") + "/rest/api/3/project/"+projectName;
            System.out.println(url);
            String credential = Credentials.basic(zephyrProperties.get("userName"), zephyrProperties.get("userPassword"));

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("Authorization",credential);
            Response dataResponse = get(url, headers);
            data = parse(dataResponse);
            projectId = Long.parseLong((String)data.get("id"));

            System.out.println("projectId:= "+projectId);

            return projectId;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return projectId;
    }



    public String createCycleZFJC(ZephyrModel zephyrData) {

        String cycleId = null;

        try {

            String createCycleURL = zephyrProperties.get("Zephyrurl") + "/public/rest/api/1.0/cycle";
            System.out.println("createCycleURL:="+createCycleURL);
            ZFJCloudRestClient client = ZFJCloudRestClient.restBuilder(zephyrProperties.get("Zephyrurl"), zephyrProperties.get("accessKey"), zephyrProperties.get("secretKey"), zephyrProperties.get("userName")).build();
            JwtGenerator jwtGenerator = client.getJwtGenerator();
            URI uri = new URI(createCycleURL);
            String jwtHeaderValue = jwtGenerator.generateJWT("POST", uri, expirationInSec);


            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("E dd, yyyy hh:mm a");
            String dateFormatForCycleCreation = sdf.format(date);

            String cycleName = zephyrData.getCyclePrefix() + dateFormatForCycleCreation;
//            String cycleName = zephyrData.getCyclePrefix();

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = sdf1.format(date);

            GregorianCalendar gCal = new GregorianCalendar();

            if (zephyrData.getCycleDuration().trim().equalsIgnoreCase("30 days")) {
                gCal.add(Calendar.DAY_OF_MONTH, +29);
            } else if (zephyrData.getCycleDuration().trim().equalsIgnoreCase("7 days")) {
                gCal.add(Calendar.DAY_OF_MONTH, +6);
            } else if (zephyrData.getCycleDuration().trim().equalsIgnoreCase("1 day")) {
                gCal.add(Calendar.DAY_OF_MONTH, +1);
            }

            String endDate = sdf1.format(gCal.getTime());

            HttpResponse response = null;

            JSONObject jObject = new JSONObject();
            jObject.put("name", cycleName);
            jObject.put("projectId", zephyrData.getZephyrProjectId());
            jObject.put("versionId", zephyrData.getVersionId());
            jObject.put("startDate", date.getTime());
            jObject.put("endDate", gCal.getTimeInMillis());
            jObject.put("environment", zephyrData.getEnvironment());

            StringEntity se = new StringEntity(jObject.toString(), "utf-8");

            HttpPost createCycleRequest = new HttpPost(createCycleURL);

            createCycleRequest.addHeader("Content-Type", "application/json");
            createCycleRequest.addHeader("Authorization", jwtHeaderValue);
            createCycleRequest.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));

            createCycleRequest.setEntity(se);

            response = RestClient.getHttpclient().execute(createCycleRequest);
            System.out.println("response:= "+response);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode:= "+statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                String string = null;
                string = EntityUtils.toString(entity);
                System.out.println("string:"+string);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser. parse(string);
                cycleId = (String) json.get("id");
                System.out.println("cycleId:= "+cycleId);
            }
            else{
                System.out.println("Something went wrong: Cycle was not created");
                PropertyReader.getInstance().setPropsValue("zephyr.properties","/src/main/resources/zephyr.properties","jira","false");
                System.out.println("Jira property value was modified to false");
            }

            return cycleId;

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
            System.out.println("Something went wrong: Cycle was not created");
            PropertyReader.getInstance().setPropsValue("zephyr.properties","/src/main/resources/zephyr.properties","jira","false");
            System.out.println("Jira property value was modified to false");
        }
        return "";
    }



    public void assignTestsZFJC(ZephyrModel zephyrData, HashMap<String, Object> jsonObject) {
        try{

            String assignTestsToCycleURL = zephyrProperties.get("Zephyrurl") + "/public/rest/api/1.0/executions/add/cycle/" + zephyrData.getCycleIdZfjCloud();
            System.out.println("assignTestsToCycleURL:= "+assignTestsToCycleURL);
            System.out.println("jsonObject:= "+jsonObject.toString());
            ZFJCloudRestClient client = ZFJCloudRestClient.restBuilder(zephyrProperties.get("Zephyrurl"), zephyrProperties.get("accessKey"), zephyrProperties.get("secretKey"), zephyrProperties.get("userName")).build();
            JwtGenerator jwtGenerator = client.getJwtGenerator();
            URI uri = new URI(assignTestsToCycleURL);
            String jwtHeaderValue = jwtGenerator.generateJWT("POST", uri, expirationInSec);

            StringEntity se = new StringEntity(jsonObject.toString());

            HttpPost createCycleRequest = new HttpPost(assignTestsToCycleURL);

            createCycleRequest.addHeader("Content-Type", "application/json");
            createCycleRequest.addHeader("Authorization", jwtHeaderValue);
            createCycleRequest.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));

            createCycleRequest.setEntity(se);
            response = RestClient.getHttpclient().execute(createCycleRequest, RestClient.getContext(zephyrProperties.get("Zephyrurl"),zephyrProperties.get("userName"), zephyrProperties.get("userPassword")));

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode:= "+statusCode);
            String token = null;
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                token = EntityUtils.toString(entity);
                System.out.println("string:"+token);
            }

            int maxTryCount = 0;
            boolean checkJobProgress = false;

            while (!checkJobProgress) {
                maxTryCount++;
                try {
                    checkJobProgress = checkJobProgress(zephyrData,token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(maxTryCount == 10) checkJobProgress = true;
            }
        }

        catch(Exception e){
            System.out.println(e);
        }
    }



    public HashMap<String, Integer> fetchExecutionIdsZFJC(ZephyrModel zephyrData, Map<String, String> issueKeyExecutionIdMap, int offset) {

        HashMap<String, Integer> values = new HashMap<String,Integer>();
        Map<String, String> map = new HashMap<String, String>();

        try {
            String executionsURL = zephyrProperties.get("Zephyrurl") + "/public/rest/api/1.0/executions/search/cycle/" + zephyrData.getCycleIdZfjCloud() + "?projectId=" + zephyrData.getZephyrProjectId() + "&versionId=" + zephyrData.getVersionId() + "&offset=" + offset + "&action=expand";
            System.out.println("executionsURL:= "+executionsURL);
            ZFJCloudRestClient client = ZFJCloudRestClient.restBuilder(zephyrProperties.get("Zephyrurl"), zephyrProperties.get("accessKey"), zephyrProperties.get("secretKey"), zephyrProperties.get("userName")).build();
            JwtGenerator jwtGenerator = client.getJwtGenerator();
            URI uri = new URI(executionsURL);
            String jwtHeaderValue = jwtGenerator.generateJWT("GET", uri, expirationInSec);

            HttpGet executionsURLRequest = new HttpGet(executionsURL);

            executionsURLRequest.addHeader("Content-Type", "application/json");
            executionsURLRequest.addHeader("Authorization", jwtHeaderValue);
            executionsURLRequest.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));


            response = RestClient.getHttpclient().execute(executionsURLRequest, RestClient.getContext(zephyrProperties.get("Zephyrurl"),zephyrProperties.get("userName"), zephyrProperties.get("userPassword")));

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode:= "+statusCode);
            ArrayList<HashMap<String,  Object>> executions = new ArrayList<HashMap<String,  Object>>();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                String string = null;
                string = EntityUtils.toString(entity);

                JSONParser parser = new JSONParser();
                HashMap<String, Object> json = (HashMap<String, Object>) parser.parse(string);
                executions = (ArrayList<HashMap<String, Object>>)json.get("searchObjectList");

                values.put("totalCount",((int) (long)json.get("totalCount")));
                values.put("currentCount", executions.size());

                for (int i = 0; i < executions.size(); i++) {
                    HashMap<String, Object> execution = executions.get(i);
                    String issueKey = ((String)execution.get("issueKey")).trim();
                    String executionId = (String)((HashMap<String, Object>)execution.get("execution")).get("id");

                    map.put(issueKey, executionId);
                }
            }
            issueKeyExecutionIdMap.putAll(map);
            System.out.println("executions.size():= "+executions.size());
            System.out.println("issueKeyExecutionIdMap size= "+issueKeyExecutionIdMap);
            return values;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return values;
    }



    public void executeTestsZFJC(ZephyrModel zephyrData, List<String> passList, List<String> failList) {

        try {
            String bulkExecuteTestsURL = zephyrProperties.get("Zephyrurl") +"/public/rest/api/1.0/executions";
            System.out.println("bulkExecuteTestsURL:= "+bulkExecuteTestsURL);
            ZFJCloudRestClient client = ZFJCloudRestClient.restBuilder(zephyrProperties.get("Zephyrurl"), zephyrProperties.get("accessKey"), zephyrProperties.get("secretKey"), zephyrProperties.get("userName")).build();
            JwtGenerator jwtGenerator = client.getJwtGenerator();
            URI uri = new URI(bulkExecuteTestsURL);
            String jwtHeaderValue = jwtGenerator.generateJWT("POST", uri, expirationInSec);

            int failListSize = failList.size();
            if (failListSize > 0) {
                int bulkOperationSetCount = failListSize / MAX_BULK_OPERATION_COUNT;
                bulkOperationSetCount += (failListSize % MAX_BULK_OPERATION_COUNT) > 0 ? 1 : 0;

                for (int i = 0; i < bulkOperationSetCount; i++) {
                    List<String> tempFailList = failList.subList((i*MAX_BULK_OPERATION_COUNT), ((bulkOperationSetCount - i) > 1) ? ((i*MAX_BULK_OPERATION_COUNT) + MAX_BULK_OPERATION_COUNT) : failList.size());
                    JSONArray failedTests = new JSONArray();

                    for (String failedTest : tempFailList) {
                        failedTests.add(failedTest);
                    }

                    JSONObject failObj = new JSONObject();
                    failObj.put("executions", failedTests);
                    failObj.put("status", 2);
                    failObj.put("stepStatus", -1);
                    failObj.put("testStepStatusChangeFlag", true);
                    failObj.put("clearDefectMappingFlag", false);
                    StringEntity failEntity = new StringEntity(failObj.toString());

                    HttpPost bulkUpdateFailedTests = new HttpPost(bulkExecuteTestsURL);
                    bulkUpdateFailedTests.addHeader("Content-Type", "application/json");
                    bulkUpdateFailedTests.addHeader("Authorization", jwtHeaderValue);
                    bulkUpdateFailedTests.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));
                    bulkUpdateFailedTests.setEntity(failEntity);

                    response = RestClient.getHttpclient().execute(bulkUpdateFailedTests);

                    int statusCode = response.getStatusLine().getStatusCode();
                    System.out.println("statusCode:= "+statusCode);
                    String token = null;
                    if (statusCode >= 200 && statusCode < 300) {
                        HttpEntity entity = response.getEntity();
                        token = EntityUtils.toString(entity);
                        System.out.println(token);
                    }
                    int maxTryCount = 0;
                    boolean checkJobProgress = false;

                    while (!checkJobProgress) {
                        maxTryCount++;
                        try {
                            checkJobProgress = checkJobProgress(zephyrData,token);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(maxTryCount == 10) checkJobProgress = true;
                    }
                }
            }

            int passListSize = passList.size();
            if (passListSize > 0) {
                int bulkOperationSetCount = passListSize / MAX_BULK_OPERATION_COUNT;
                bulkOperationSetCount += (passListSize % MAX_BULK_OPERATION_COUNT) > 0 ? 1 : 0;

                for (int i = 0; i < bulkOperationSetCount; i++) {
                    List<String> tempPassList = passList.subList((i * MAX_BULK_OPERATION_COUNT), ((bulkOperationSetCount - i) > 1) ? ((i * MAX_BULK_OPERATION_COUNT) + MAX_BULK_OPERATION_COUNT) : passList.size());

                    JSONArray passedTests = new JSONArray();
                    HashMap<String, String> body = new HashMap<>();

                    for (String passedTest : tempPassList) {
                        passedTests.add(passedTest);
                    }

                    JSONObject passObj = new JSONObject();
                    passObj.put("executions", passedTests);
                    passObj.put("status", 1);
                    passObj.put("stepStatus", -1);
                    passObj.put("testStepStatusChangeFlag", true);
                    passObj.put("clearDefectMappingFlag", false);
                    StringEntity passEntity = new StringEntity(passObj.toString());

                    HttpPost bulkUpdatePassedTests = new HttpPost(bulkExecuteTestsURL);
                    bulkUpdatePassedTests.addHeader("Content-Type", "application/json");
                    bulkUpdatePassedTests.addHeader("Authorization", jwtHeaderValue);
                    bulkUpdatePassedTests.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));
                    bulkUpdatePassedTests.setEntity(passEntity);

                    response = RestClient.getHttpclient().execute(bulkUpdatePassedTests);

                    int statusCode = response.getStatusLine().getStatusCode();
                    System.out.println("statusCode:= "+statusCode);
                    String token = null;
                    if (statusCode >= 200 && statusCode < 300) {
                        HttpEntity entity = response.getEntity();
                        token = EntityUtils.toString(entity);
                        System.out.println(token);
                    }
                    int maxTryCount = 0;
                    boolean checkJobProgress = false;

                    while (!checkJobProgress) {
                        maxTryCount++;
                        try {
                            checkJobProgress = checkJobProgress(zephyrData,token);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(maxTryCount == 10) checkJobProgress = true;
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }


    private boolean checkJobProgress(ZephyrModel zephyrData, String token) {
        boolean jobCompleted = false;
        try {
            TimeUnit.SECONDS.sleep(2);

            String url = zephyrProperties.get("Zephyrurl") +"/public/rest/api/1.0/jobprogress/"+token;
            System.out.println("url:= "+url);
            ZFJCloudRestClient client = ZFJCloudRestClient.restBuilder(zephyrProperties.get("Zephyrurl"), zephyrProperties.get("accessKey"), zephyrProperties.get("secretKey"), zephyrProperties.get("userName")).build();
            JwtGenerator jwtGenerator = client.getJwtGenerator();
            URI uri = new URI(url);
            String jwtHeaderValue = jwtGenerator.generateJWT("GET", uri, expirationInSec);

            HttpGet jobProgressRequest = new HttpGet(url);

            jobProgressRequest.addHeader("Content-Type", "application/json");
            jobProgressRequest.addHeader("Authorization", jwtHeaderValue);
            jobProgressRequest.addHeader("zapiAccessKey", zephyrProperties.get("accessKey"));

            response = RestClient.getHttpclient().execute(jobProgressRequest, RestClient.getContext(zephyrProperties.get("Zephyrurl"),zephyrProperties.get("userName"), zephyrProperties.get("userPassword")));
            String result = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            HashMap<String, Object> json = (HashMap<String, Object>) parser.parse(result);

            String progress = String.valueOf((double)json.get("progress"));
            if (progress != null && progress.equals("1.0")) {
                jobCompleted = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("jobCompleted:= "+jobCompleted);
        return jobCompleted;
    }



    public void setCycleIDGlobally(String cycleID){
        String url = "https://global-allure.herokuapp.com/cycle/35";
        System.out.println(url);
        System.out.println("cycleID: ="+cycleID);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");

        try{
            Response dataResponse = jsonPost(url, headers,"{\"cycle\":\""+cycleID+"\"}");
            System.out.println(dataResponse);
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public void setLaunchGlobally(String launch){
        String url = "https://global-allure.herokuapp.com/launch/35";
        System.out.println(url);
        System.out.println("launch: ="+launch);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");

        try{
            Response dataResponse = jsonPost(url, headers,"{\"launch\":\""+launch+"\"}");
            System.out.println(dataResponse);
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
