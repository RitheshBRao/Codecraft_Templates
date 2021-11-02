package com.web.template;

import com.web.template.Utils.Base;
import com.web.template.Utils.DriverFactory;
import io.qameta.allure.Attachment;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import org.testng.IMethodInstance;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/*
    @desc      This class is uses
                a)IMethodInterceptor to add priority to testcases at run time
                b)IAnnotationTransformer to add retry label at run time to all tc
                c)ITestListener will added logs and screenshots to allure report.
                d)Updates zephyr test cycle based on flag
    @author    Ostan Dsouza
    @Date      02/06/2020
 */

public class TestListener extends TestBase implements INewListener {

    JSONObject test = new JSONObject();
    JSONArray assign = new JSONArray();
    JSONArray pass;
    JSONArray fail;
    int totalExecutionsCount;
    Map<String, String> issueKeyExecutionIdMap = new HashMap<String, String>();

    private  String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    @Attachment(value = "Failure in method {0}", type = "image/png")
    private byte[] takeScreenShot(WebDriver driver) throws WebDriverException {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "{0}", type = "text/plain")
    public  String saveTextLogs(String message) {
        return message;
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("Test Method Failed " + getTestMethodName(iTestResult));
        Object testClass = iTestResult.getInstance();
        WebDriver driver = Base.getDriver();

        //Allure ScreenShotRobot and SaveTestLog
        if (driver instanceof WebDriver) {
            try {
                takeScreenShot(DriverFactory.getDriver());
                System.out.println("Screenshot captured for test case: " + getTestMethodName(iTestResult));
            } catch (Exception e) {
                System.out.println("Failed to capture screenshot "+e);
            }

        }
        saveTextLogs(getTestMethodName(iTestResult) + " failed and screenshot taken!");
        if(jira) {
            assign.clear();
            assign.add(iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName());
            test.put("issues", assign);
            zephyr.assignTestsZFJC(zephyrData, test);
            totalExecutionsCount = 0;
            totalExecutionsCount = zephyr.fetchExecutionIdsZFJC(zephyrData, issueKeyExecutionIdMap, 0).get("totalCount");
            System.out.println("totalExecutionsCount: ="+totalExecutionsCount);
            System.out.println("(issueKeyExecutionIdMap.size: ="+issueKeyExecutionIdMap.size());
            int n=1;
            while (totalExecutionsCount >= 50) {
                System.out.println("(issueKeyExecutionIdMap.size(): ="+issueKeyExecutionIdMap.size());
                totalExecutionsCount = zephyr.fetchExecutionIdsZFJC(zephyrData, issueKeyExecutionIdMap, 50*n).get("currentCount");
                n++;
            }
            fail.add(issueKeyExecutionIdMap.get(iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName()));
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("Skipped Test: " + getTestMethodName(iTestResult));
//        test.remove("issues");
//        test.put("issues",assign);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("Test Case Passed: " + getTestMethodName(iTestResult));
        if(jira) {
            assign.clear();
            System.out.println("assign: ="+assign.toString());
            assign.add(iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName());
            test.put("issues", assign);
            zephyr.assignTestsZFJC(zephyrData, test);
            totalExecutionsCount = 0;
            totalExecutionsCount = zephyr.fetchExecutionIdsZFJC(zephyrData, issueKeyExecutionIdMap, 0).get("totalCount");
            System.out.println("totalExecutionsCount: ="+totalExecutionsCount);
            int n=1;
            while (totalExecutionsCount >= 50) {
                totalExecutionsCount = zephyr.fetchExecutionIdsZFJC(zephyrData, issueKeyExecutionIdMap, 50*n).get("currentCount");
                n++;
            }
            pass.add(issueKeyExecutionIdMap.get(iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName()));
        }
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("Test Case Started: " + getTestMethodName(iTestResult));
        if(jira) {
            System.out.println("testname: ="+iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName());
            assign.add(iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).testName());
            test.put("issues", assign);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test Failed within success percentage: " + getTestMethodName(iTestResult));
    }

    @Override
    public void onStart(ITestContext iTestResult) {
        System.out.println("Started: ");
        if(jira) {
            pass = new JSONArray();
            fail = new JSONArray();
            test.put("versionId", zephyrData.getVersionId());
            test.put("cycleId", zephyrData.getCycleIdZfjCloud());
            test.put("projectId", zephyrData.getZephyrProjectId());
            test.put("method", "1");
            test.put("assigneeType", "currentUser");
        }
    }

    @Override
    public void onFinish(ITestContext iTestResult) {
        System.out.println("Finished: ");
        if(jira) {
            System.out.println("pass:= " + pass);
            System.out.println("fail:= " + fail);
            zephyr.executeTestsZFJC(zephyrData, pass, fail);
        }
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {
            private int getLineNo(IMethodInstance mi) {
                int result = 0;

                String methodName = mi.getMethod().getConstructorOrMethod().getMethod().getName();
                String className  = mi.getMethod().getConstructorOrMethod().getDeclaringClass().getCanonicalName();
                ClassPool pool    = ClassPool.getDefault();

                try {
                    CtClass cc        = pool.get(className);
                    CtMethod ctMethod = cc.getDeclaredMethod(methodName);
                    result            = ctMethod.getMethodInfo().getLineNumber(0);
                } catch (NotFoundException | javassist.NotFoundException e) {
                    e.printStackTrace();
                }

                return result;
            }

            public int compare(IMethodInstance m1, IMethodInstance m2) {
                return getLineNo(m1) - getLineNo(m2);
            }
        };

        IMethodInstance[] array = methods.toArray(new IMethodInstance[methods.size()]);
        Arrays.sort(array, comparator);
        return Arrays.asList(array);
    }
}