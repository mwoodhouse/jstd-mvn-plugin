package com.jstd;

import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.output.TestResultListener;
import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.List;

import static com.google.jstestdriver.TestResult.Result.failed;

public class MvnTestResultLogger implements TestResultListener
{
    final Log log;

    final List<TestResult> failedTests;

    public MvnTestResultLogger(final Log log)
    {
        this.log = log;
        this.failedTests = new ArrayList<TestResult>();
    }

    public void finish()
    {
        // not implemented
    }

    public void onFileLoad(final String browser, final FileResult fileResult)
    {
        // not implemented
    }

    public void onTestComplete(final TestResult testResult)
    {
        logTestResult(testResult);

        if(testFailed(testResult))
        {
            failedTests.add(testResult);
        }
    }

    private boolean testFailed(final TestResult testResult)
    {
        return testResult.getResult().equals(failed);
    }

    private void logTestResult(final TestResult testResult)
    {
        log.info(testResult.getResult().name().toUpperCase()+", "+browserAndPlatform(testResult)+", "+testCase(testResult));
    }

    private String testCase(final TestResult testResult)
    {
        return testResult.getTestCaseName() + ":" + testResult.getTestName();
    }

    private String browserAndPlatform(final TestResult testResult)
    {
        return testResult.getBrowserInfo().getName()+"("+testResult.getBrowserInfo().getVersion()+") "+testResult.getBrowserInfo().getOs();
    }

    public List<TestResult> getFailedTests()
    {
        return failedTests;
    }
}
