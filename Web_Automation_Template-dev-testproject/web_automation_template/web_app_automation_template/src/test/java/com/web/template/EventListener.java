package com.web.template;

import io.appium.java_client.events.api.general.AppiumWebDriverEventListener;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/*
    @desc      This class is uses AppiumWebDriverEventListener for logging event to dashboard
    @author    Harish Merugu
    @Date      08/03/2021
 */

public class EventListener extends TestBase implements AppiumWebDriverEventListener {

    private static EventListener obj=null;

    public static EventListener getInstance()
    {
        if(obj==null)
            obj = new EventListener();
        return obj;

    }

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {
//        System.out.println("before Navigating to: '"+s+"'");
    }

    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {
//        System.out.println("Navigated to:'" + s + "'");
    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {
//        System.out.println("Navigated back to previous page");
    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {
//        System.out.println("Navigating back to previous page");
    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {
//        System.out.println("Navigating forward to next page");
    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {
//        System.out.println("Navigating forward to next page");
    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
//        System.out.println("Trying to find Element By : " + by.toString());
    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
//        System.out.println("Found Element By : " + by.toString());
    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
//        System.out.println("Trying to click on: " + webElement.toString());
    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
//        System.out.println("Clicked on: " + webElement.toString());
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void beforeSwitchToWindow(String s, WebDriver webDriver) {

    }

    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {
        System.out.println("event firing listener..........................."+throwable);
    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> outputType) {

    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {

    }

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {
//        System.out.println("Before get text: " + webElement.toString());
    }

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String s) {
//        System.out.println("After get text: " + webElement.toString());
    }
}
