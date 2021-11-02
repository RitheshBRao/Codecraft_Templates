package com.web.template;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/*
    @desc      This class is uses IRetryAnalyzer for retry logic after test failures
    @author    Harish Merugu
    @Date      08/03/2021
 */

public class RetryAnalyzer implements IRetryAnalyzer{

    int counter = 0;
    int retryLimit = 1;      //change retry count here

    public boolean retry(ITestResult result) {
        if(counter < retryLimit){
            counter++;
            return true;
        }
        return false;
    }

}
