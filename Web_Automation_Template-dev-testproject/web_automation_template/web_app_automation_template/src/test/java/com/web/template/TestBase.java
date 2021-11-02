package com.web.template;

import com.web.template.Utils.DriverFactory;
import com.web.template.Utils.InitiateDriver;
import com.web.template.Utils.PropertyReader;
import com.web.template.Utils.Zapi.ZephyrManager;
import com.web.template.Utils.Zapi.ZephyrModel;
import org.apache.commons.lang3.SystemUtils;
import org.testng.annotations.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by codecraft on 08/11/16.
 */
public class TestBase extends DriverFactory {

    public  HashMap<String,String> configProperties;
    public  HashMap<String,String> credentialsProperties;
    public  HashMap<String,String> testProperties;
    public  HashMap<String,String> getProperties;
    public  String windowsPath=System.getProperty("user.dir")+"\\src\\main\\resources\\"; //For Windows Properties path
    public  String linux_MacPath=System.getProperty("user.dir")+"/src/main/resources/";  //For Linux Properties path
    public  HashMap<String, String> zephyrProperties = PropertyReader.getInstance().getPropValues("zephyr.properties");
    public  ZephyrModel zephyrData = new ZephyrModel();
    public  ZephyrManager zephyr = new ZephyrManager();
    boolean jira = System.getenv("jira")== null? Boolean.parseBoolean(zephyrProperties.get("jira")): Boolean.parseBoolean(System.getenv("jira"));

    public TestBase() {
        configProperties = PropertyReader.getInstance().getPropValues("config.properties");
        credentialsProperties = PropertyReader.getInstance().getPropValues("credentials.properties");
    }

    @BeforeSuite(alwaysRun=true)
    public void zephyrSetup(){
        if(jira) {
            zephyrData.setCyclePrefix("Automation_");
            zephyrData.setCycleDuration("1 day");
            zephyrData.setVersionId(-1L);
            zephyrData.setZephyrProjectId(zephyr.getProjectIdByName(zephyrProperties.get("project")));
            zephyrData.setEnvironment(System.getenv("env")== null? "qa2":System.getenv("env"));
            zephyrData.setCycleIdZfjCloud(zephyr.createCycleZFJC(zephyrData));
        }
    }

    //use BeforeSuite to start session at begining
    @BeforeSuite(alwaysRun = true)
    @org.testng.annotations.Parameters(value = {"environment"})
    public void setup(@Optional("parallel") String environment) {
        if (!environment.equalsIgnoreCase("parallel")) {
            try {
                if (zephyrData.getCycleIdZfjCloud() == null) {
                    jira = Boolean.parseBoolean(PropertyReader.getProp("/src/main/resources/zephyr.properties").get("jira"));
                    System.out.println("jira:= " + jira);
                }
                getProperties = PropertyReader.getInstance().getPropValues("config.properties");

                //Creating getDriver() instance.
                InitiateDriver.getInstance().createDriver();
                //For AWS
                //final String URL_STRING = "http://127.0.0.1:4723/wd/hub";
                setDriver(InitiateDriver.getInstance().getDriver());

//                setDriver(EventFiringWebDriverFactory.getEventFiringWebDriver(getDriver(), EventListener.getInstance()));
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException("Driver setup has failed");
            }
        }
    }

    //use BeforeTest to rerun session after every suites
    @BeforeTest(alwaysRun = true)
    @org.testng.annotations.Parameters(value = {"config", "environment"})
    public void setup(@Optional("single") String config_file, @Optional("single") String environment) {

        if (!config_file.equalsIgnoreCase("single")) {
            try {
                if (zephyrData.getCycleIdZfjCloud() == null) {
                    jira = Boolean.parseBoolean(PropertyReader.getProp("/src/main/resources/zephyr.properties").get("jira"));
                    System.out.println("jira:= " + jira);
                }
                getProperties = PropertyReader.getInstance().getPropValues("config.properties");

                //Creating getDriver() instance.
                InitiateDriver.getInstance().createDriver();
                //For AWS
                //final String URL_STRING = "http://127.0.0.1:4723/wd/hub";
                setDriver(InitiateDriver.getInstance().getDriver());

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                throw new RuntimeException("Driver setup has failed");
            }
        }

    }

    @AfterTest(alwaysRun = true)
    @org.testng.annotations.Parameters(value = {"config"})
    public void teardownDriver(@Optional("functional") String config) {
        try {
            if (!config.equalsIgnoreCase("single")) {
                getDriver().close();
                getDriver().quit();
            }
        }
        catch(Exception e) {
            System.out.println(e);
//            throw new RuntimeException("Driver was already killed");
        }
    }

    @AfterSuite(alwaysRun = true)
    public void publishCycle() {
        try {
            getDriver().close();
            getDriver().quit();
        } catch (Exception e) {
            System.out.println(e);
//            throw new RuntimeException("Driver was already killed");
        }
    }


    public  void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private String encodeString(String text){
        String theString="";
        try{
            byte[] byteText = text.getBytes("ISO-8859-1");
            //To get original string from byte.
            theString =  new String(byteText , "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            System.out.println(e);
        }
        return theString;
    }

    public  String checkOS(){
        if (SystemUtils.IS_OS_WINDOWS) {
            return windowsPath;
        } else if (SystemUtils.IS_OS_MAC) {
            return linux_MacPath;
        } else if (SystemUtils.IS_OS_LINUX) {
            return linux_MacPath;
        } else{
            return null;
        }
    }


}
