package com.web.template.LoginTests;

import com.web.template.Pages;
import com.web.template.TestBase;
import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Test;


public class LoginTests extends TestBase {

    @Test(testName = "ENHO-577", groups = {"Sanity"})
    @Description("[ENHO-577]loginSuccessful")
    public void loginSuccessful() {
        try {
            String email = credentialsProperties.get("email");
            String password = credentialsProperties.get("password");
            Pages.LoginPage().login(email, password);
            Assert.assertTrue(Pages.LoginPage().isTutorialPageDisplayed(), "Failed to Login");
        }
        catch(Exception e){
            e.printStackTrace();
            Assert.fail("EM-577 loginSuccessful is failed"+"\n"+e);
        }
    }

}
