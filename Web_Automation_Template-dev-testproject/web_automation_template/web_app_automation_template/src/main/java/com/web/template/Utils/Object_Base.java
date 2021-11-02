package com.web.template.Utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.time.Duration;

public class Object_Base {

    public  long wait = 10;

    public Object_Base(WebDriver driver){
//        FieldDecorator fieldDecorator= new AppiumFieldDecorator(driver, Duration.ofSeconds(wait));
//        this.driver=driver;
        PageFactory.initElements(driver, this);

    }
}
