package com.web.template.LoginPage;

import com.web.template.Utils.Base;

public class LoginPage extends Base {

    private  LoginPageObjectRepository lp;

    public LoginPage(){
        lp = new LoginPageObjectRepository(getDriver());
    }

    public  void waitUntilLoginPageIsHidden(){
        waitForElementToBeInvisible(lp.emailField);
    }

    public  void waitUntillPwdFieldIsVisible(){waitUntilElementIsVisible(lp.passwordField);}


    public  void enterEmail(String email){
        lp.emailField.click();
        lp.emailField.clear();
        lp.emailField.sendKeys(email);
    }

    public  void enterPassword(String password){
        lp.passwordField.click();
        lp.passwordField.sendKeys(password);
    }

    public  void clickSignInButton(){
        lp.signInButton.click();
    }

    public  void login(String email, String password){
        enterEmail(email);
        waitUntillPwdFieldIsVisible();

        enterPassword(password);
        clickSignInButton();
        waitUntilLoginPageIsHidden();
    }

    public  boolean isTutorialPageDisplayed(){
        return isElementVisible(lp.firstPage);
    }

}

