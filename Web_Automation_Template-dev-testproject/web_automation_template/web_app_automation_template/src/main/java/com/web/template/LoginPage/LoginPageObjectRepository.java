package com.web.template.LoginPage;

import com.web.template.Utils.Object_Base;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPageObjectRepository extends Object_Base {

    public LoginPageObjectRepository(WebDriver driver){
        super(driver);
    }

    @FindBy(xpath = "//input[@type='email']")
    protected  WebElement emailField;

    @FindBy(xpath = "")
    protected  WebElement passwordField;

    @FindBy(xpath = "")
    protected  WebElement signInButton;

    @FindBy(xpath = "")
    public  WebElement firstPage;

}