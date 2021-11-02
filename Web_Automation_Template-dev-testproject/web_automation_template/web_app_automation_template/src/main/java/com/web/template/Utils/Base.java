package com.web.template.Utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Base extends DriverFactory {

    public  long wait=90;
    public  String windowsPath="C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local\\Android\\android-sdk\\platform-tools\\";
    public  String linuxPath="/home/"+"System.getProperty(\"user.name\")"+"/Android/android-sdk/platform-tools/";
    public static Alert alert;
    public static Actions action;

    public  boolean isElementVisible(WebElement element)
    {
        try{
            if(element.isDisplayed())
                return true;
            return false;
        }
        catch (org.openqa.selenium.NoSuchElementException e)
        {
            return false;
        }

    }


    public  void waitForElementToBeInvisible(WebElement element)
    {
        WebDriverWait wwait = new WebDriverWait(getDriver(),wait);
        wwait.until(invisibilityOfWebElementLocated(element));
    }

    public  void waitUntilElementIsVisible(WebElement element){
        WebDriverWait wwait = new WebDriverWait(getDriver(), wait);
        wwait.until(ExpectedConditions.visibilityOf(element));
    }

    public  void waitUntilElementHasText(WebElement element, String text)
    {
        WebDriverWait wwait = new WebDriverWait(getDriver(), wait);
        wwait.until(ExpectedConditions.textToBePresentInElement(element,text));
    }

    private  ExpectedCondition<Boolean> invisibilityOfWebElementLocated(final WebElement element)
    {
        return new ExpectedCondition<Boolean>() {
            //@Override
            public Boolean apply(WebDriver driver) {
                try
                {
                    if (element.isDisplayed())
                        return false;
                    return true;
                }
                catch (Exception e)
                {
                    return true;
                }
            }
        };
    }

    public  void waitUntilElementsAttributeHasChanged(WebElement element, String attribute, String initialValue){
        WebDriverWait wwait = new WebDriverWait(getDriver(), wait);
        wwait.until(attributeValueOfElementChanged(element,attribute,initialValue));
    }

    private  ExpectedCondition<Boolean> attributeValueOfElementChanged(final WebElement element, final String attribute, final String initialValue){
        return new ExpectedCondition<Boolean>() {
            //@Override
            public Boolean apply(WebDriver driver) {
                try
                {
                    if (element.getAttribute(attribute).equalsIgnoreCase(initialValue))
                        return false;
                    return true;
                }
                catch (Exception e)
                {
                    return true;
                }
            }
        };

    }

    public  void navigateBack(){
        getDriver().navigate().back();
    }
    public  void navigateForward(){
        getDriver().navigate().forward();
    }
    public  void navigateRefresh(){
        getDriver().navigate().refresh();
    }

    public  void sleep(long timeout){
        try{
            Thread.sleep(timeout);
        }
        catch (InterruptedException e){

        }
    }

    public  String generateRandomString(int length){
        String randomString = new String();
        randomString = RandomStringUtils.randomAlphabetic(length);
        return randomString;
    }

    public  String generateAlphaNumericString(int length){
        String randomAlphaNumericString = new String();
        randomAlphaNumericString  = RandomStringUtils.randomAlphanumeric(length);
        return randomAlphaNumericString;
    }

    public  String generateRandomMobileNumber(){
        String mobileNumber = new String();
        mobileNumber = RandomStringUtils.random(1,"789");
        mobileNumber=mobileNumber+RandomStringUtils.randomNumeric(9);
        return mobileNumber;
    }

    public  String generateRandomEmail(){
        String email = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
        return email+"@mailinator.com";
    }

    public  String generateRandomNumber(){
        return RandomStringUtils.randomNumeric(13);
    }

    public  void executeCommand(String[] args) {
        ProcessBuilder pb = new ProcessBuilder(args);
        Process pc;
        try {
            pc = pb.start();
            pc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  String checkOS(){
        if (SystemUtils.IS_OS_WINDOWS) {
            return windowsPath;
        } else if (SystemUtils.IS_OS_MAC) {
            return null;
        } else if (SystemUtils.IS_OS_LINUX) {
            return linuxPath;
        } else{
            return null;
        }
    }

    /*public  String getAndroidVersion(){
        String path=checkOS();
        try{
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(path+"adb shell getprop ro.build.version.release").getInputStream()).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }catch (IOException e){
            return "";
        }
    }*/

    public boolean isAlertpresent(WebElement element){
        WebDriverWait wwait = new WebDriverWait(getDriver(), wait);
        if(wwait.until(ExpectedConditions.alertIsPresent())==null){
            System.out.println("Alert is not present");
            return false;
        }else
            System.out.println("Alert is present");
        return true;
    }

    public void AcceptAlert(){
        try{
            Thread.sleep(2);
            getDriver().switchTo().alert().accept();
            System.out.println("Alert is present and Accepted");
        }catch (Exception e){
            System.out.println("Alert is not present");
        }
    }

    public void DismissAlert(){
        try{
            Thread.sleep(2);
            getDriver().switchTo().alert().dismiss();
            System.out.println("Alert is present and Dismissed");
        }catch (Exception e){
            System.out.println("Alert is not present");
        }
    }

    public void AlertText(){
        try {
            Thread.sleep(2);
            getDriver().switchTo().alert().getText();
        }catch (Exception e){
            System.out.println("Alert is not present");
        }
    }

    public void movetoElement(WebElement element){
        action = new Actions(getDriver());
        action.moveToElement(element).build().perform();
    }

    public void MouseCick(WebElement element){
        action = new Actions(getDriver());
        action.moveToElement(element).click().build().perform();
    }

    public void doubleTap(WebElement element){
        action = new Actions(getDriver());
        action.doubleClick(element).build().perform();
    }

    public void dragNDrop(WebElement source,WebElement dest){
        action = new Actions(getDriver());
        action.dragAndDrop(source,dest).build().perform();
    }

}
