package com.web.template;

import org.testng.IAnnotationTransformer;
import org.testng.IMethodInterceptor;
import org.testng.ITestListener;

/*
    @desc      This interface will extend all listeners that are used in project
    @author    Harish Merugu
    @Date      08/03/2021
 */

public interface INewListener extends ITestListener, IAnnotationTransformer, IMethodInterceptor {
}
