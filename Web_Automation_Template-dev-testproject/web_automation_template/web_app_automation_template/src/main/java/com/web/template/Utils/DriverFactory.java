package com.web.template.Utils;

//import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverFactory {

//    public static ThreadLocal<AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(RemoteWebDriver localdriver) {
        driver.set(localdriver);
    }
}
