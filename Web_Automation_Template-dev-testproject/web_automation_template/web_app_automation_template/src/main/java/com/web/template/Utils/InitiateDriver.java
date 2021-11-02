package com.web.template.Utils;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by codecraft on 08/11/16.
 */
public class InitiateDriver {
    private WebDriver driver;
    //    private Map<String,String> getProperties;
    private String setup, browser,AUTUrl,url,runONFromProperty,token;
    //Windows, OSX or any
    private String platform;


    //Instance of classes
    private static InitiateDriver obj=null;


    Properties getProperties= null;
    public InitiateDriver(){
        getProperties= new Properties();
        try {
            getProperties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Single instance of class to control the execution
    public static InitiateDriver getInstance()
    {
        if(obj==null)
            obj = new InitiateDriver();
        return obj;

    }


    public void createDriver()
    {

        setup = getProperties.get("SETUP").toString();

        platform= System.getProperty("os.name");

        runONFromProperty=System.getenv("platform") == null ? getProperties.get("RUN_ON").toString():System.getenv("platform");

//        For WebBrowser stepup=Local
        if(setup.equalsIgnoreCase("Local"))
            setConfigValuesForWebsiteTesting();

        else
            setTestProjectCredentials();

        initiateDriver();

    }

    /**
     * Function assign values to variable with setup=local and runON= website (mobile app testing)
     */
    private void setConfigValuesForWebsiteTesting()
    {
        browser=System.getProperty("browser") == null ? getProperties.get("BROWSER").toString() : System.getProperty("browser");
        AUTUrl =System.getProperty("auturl") == null ? getProperties.get("AUTUrl").toString() : System.getProperty("auturl");
        url = System.getProperty("url") == null ? getProperties.get("SELENIUMSERVERURL").toString() : System.getProperty("url");
    }

    /**
     * Function assign values to variable with setup=browserstack
     */
//    Need to add condition
    public void setTestProjectCredentials()
    {
        browser=System.getProperty("browser") == null ? getProperties.get("BROWSER").toString() : System.getProperty("browser");
        AUTUrl =System.getProperty("auturl") == null ? getProperties.get("AUTUrl").toString() : System.getProperty("auturl");        //TOKEN passed to TESTPROJECT
        token= System.getProperty("token") == null ? getProperties.get("TOKEN").toString() : System.getProperty("token");

    }

    public WebDriver initiateDriver()
    {

        try {

            switch (setup.toLowerCase()){
                case "local":
                    driver = new RemoteWebDriver(new URL(url), getCapabilities(runONFromProperty));
                    driver.get(AUTUrl);
                    driver.manage().window().maximize();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    break;
                case "testproject":
                    driver = new io.testproject.sdk.drivers.web.RemoteWebDriver(token,
                            getCapabilities(runONFromProperty), "");
                    driver.get(AUTUrl);
                    driver.manage().window().maximize();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return driver;
    }



    //unused
    public RemoteWebDriver getDriver()
    {
        if(driver==null)
            throw new RuntimeException("Driver has not been Instantiated");

        return (RemoteWebDriver) driver;
    }

    private DesiredCapabilities getCapabilities(String runONValue)
    {
        DesiredCapabilities capabilities = null;

        if (runONValue.equalsIgnoreCase("WEBSITE"))
        {
            switch(browser.toLowerCase())
            {
                case "firefox":
                    capabilities= new DesiredCapabilities();
                    FirefoxOptions ffoptions= new FirefoxOptions();
                    ffoptions.setCapability("platformName",Platform.WINDOWS);
                    ffoptions.setCapability("browserName","Firefox");
                    capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS,ffoptions);
                    break;
                case "chrome":
                    capabilities= new DesiredCapabilities();
                    ChromeOptions options = new ChromeOptions();
                    options.setCapability("platformName",Platform.WINDOWS);
                    options.setCapability("browserName","chrome");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                    break;
                case "ie":
                    //InternetExplorerDriverManager.getInstance().setup();
                    //System.setProperty("webgetDriver().ie.getDriver()", "../../../../resources/IEDriverServer.exe");
                    capabilities = DesiredCapabilities.internetExplorer();
                    capabilities.setBrowserName("internet explorer");
                    capabilities.setCapability("ie.ensureCleanSession", true);
                    capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
                    capabilities.setPlatform(Platform.WINDOWS);
                    break;
            }
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        }
        return capabilities;
    }


}
