package com.web.template;

import com.web.template.Utils.PropertyReader;
import com.web.template.Utils.ReusuableUtility;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;

import java.io.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmailableTestNGReport implements IReporter {

    protected PrintWriter writer;

    protected final List<SuiteResult> suiteResults = Lists.newArrayList();

    // Reusable buffer
    private final StringBuilder buffer = new StringBuilder();

    private String reportFileName = "customized-emailable-report.html";
    private String reportTitle= PropertyReader.getInstance().getPropValues("config.properties","ReportTitle");


    public void setFileName(String fileName) {
        this.reportFileName = fileName;
    }

    public String getFileName() {
        return reportFileName;
    }

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            writer = createWriter(outputDirectory);
        } catch (IOException e) {
//            logger.error("Unable to create output file- reporting failed", e);
            return;
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();
//        writeDocumentEnd();

        writer.close();
    }

    protected PrintWriter createWriter(String outdir) throws IOException {
        new File(outdir).mkdirs();
//        String jvmArg = RuntimeBehavior.getDefaultEmailableReport2Name();
//        if (jvmArg != null && !jvmArg.trim().isEmpty()) {
//            fileName = jvmArg;
//        }
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, reportFileName))));
    }

    protected void writeReportTitle(String title) {
        writer.println("<center><h1>" + title + " - " + ReusuableUtility.getInstance().getCurrentDateTime() + "</h1></center>");
    }

    protected void writeDocumentStart() {
        writer.println(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"https://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.println("<html xmlns=\"https://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writer.println("<head>");
        writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writer.println("<title>ENHO Report</title>");
        writeStylesheet();

        writer.println("</head>");
    }

    protected void writeStylesheet() {

        //writer.println("<link  href=\"/Users/vijayanand/Documents/Repository/itk_automation/src/main/resources/bootstrap.min.css\" rel=\"stylesheet\" media =\"all\" type=\"text/css\">");
        writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("#summary {margin-top:30px}");
        writer.print("h1 {font-size:30px}");
        writer.print("body {width:100%;}");
        // writer.print("th,td {padding: 8px}");
        writer.print("th,td {padding: 8px; border-bottom: 1px solid #ddd;}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table, th, td { border: 1px solid black}");
        writer.print("table a {font-weight:bold;color:#0D1EB6;}");
        writer.print(".easy-overview {margin-left: auto; margin-right: auto;} ");
        writer.print(".easy-test-overview tr:first-child {background-color:#ADD8E6}");
        writer.print(".stripes tr:nth-child(odd) {background-color: #87CEEB}");
        writer.print(".num {text-align:right}");
        //https://encycolorpedia.com/ff3333#:~:text=The%20hexadecimal%20color%20code%20%23ff3333,medium%20light%20shade%20of%20red.
        writer.print(".passedodd td,.passed {background-color: #3F3}");
        writer.print(".passedeven td,.passed {background-color: #0A0}");
        writer.print(".skippedodd td,.skipped {background-color: #DDD}");
        writer.print(".skippedeven td,.skipped {background-color: #CCC}");
        writer.print(".failedodd td,.attn {background-color: #ff8080}");
        writer.print(".failedeven td,.stripe .attn {background-color: #ff8080}");
        writer.print(".stacktrace {font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print(".invisible {display:none}");
        writer.println("</style>");
    }

    protected void writeBody() {
        writer.println("<body>");
        writeReportTitle(reportTitle);
//        writeDocumentParagraph();
        writeTestDetails();
        writeSuiteSummary();
        writeScenarioSummary();
//        writeScenarioDetails();
        writer.println("</body>");
    }

    protected void writeDocumentEnd() {
        writer.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        NumberFormat decimalFormat = NumberFormat.getNumberInstance();

        int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;
        int totalRetriedTests = 0;

        writer.println("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"stripe easy-overview\">");
        writer.print("<tr>");
        writer.print("<th>Features/Devices</th>");
        writer.print("<th>Total</th>");
        writer.print("<th>Passed</th>");
        writer.print("<th>Failed</th>");
        writer.print("<th>Skipped</th>");
//        writer.print("<th>Time (ms)</th>");
//        writer.print("<th>Included Groups</th>");
//        writer.print("<th>Excluded Groups</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("<th>Total<br/>Time(hh:mm:ss)</th>");
        writer.println("</tr>");

        int testIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
//            writer.print("<tr><th colspan=\"11\">");
//            writer.print(Utils.escapeHtml(suiteResult.getSuiteName()));
//            writer.println("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                int testsCount = testResult.getTestCount();
                int passedTests = testResult.getPassedTestCount();
                int failedTests = testResult.getFailedTestCount();
                int skippedTests = testResult.getSkippedTestCount();

                //int retriedTests = testResult.getRetriedTestCount();
                Date startTime = testResult.getTestStartTime();
                Date endTime = testResult.getTestEndTime();
                long duration = testResult.getDuration();
                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print(" class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(
                        buffer
                                .append("<a href=\"#t")
                                .append(testIndex)
                                .append("\">")
                                .append(Utils.escapeHtml(testResult.getTestName()))
                                .append("</a>")
                                .toString());

//                logger.debug("Platform value for report " + runONFromProperty);
                writeTableData(integerFormat.format(testsCount), "num");
                writeTableData(integerFormat.format(passedTests), (passedTests > 0 ? "num passed" : "num"));
                writeTableData(integerFormat.format(failedTests), (failedTests > 0 ? "num attn" : "num"));
                writeTableData(integerFormat.format(skippedTests), (skippedTests > 0 ? "num skipped" : "num"));
                // writeTableData(decimalFormat.format(duration), "num");
                writeTableData(dateFormat.format(startTime),  "num");
                writeTableData(dateFormat.format(endTime),  "num");
                writeTableData(ReusuableUtility.getInstance().convertTimeToString(duration), "num");
//                writeTableData(testResult.getIncludedGroups());
//                writeTableData(testResult.getExcludedGroups());

                writer.println("</tr>");
                totalTestsCount +=testsCount;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                //totalRetriedTests += retriedTests;
                totalDuration += duration;

                testIndex++;
            }

            boolean testsInParallel = ParallelMode.TESTS.equals(suiteResult.getParallelMode());
            if (testsInParallel) {
                Optional<TestResult> maxValue = suiteResult.testResults.stream()
                        .max(Comparator.comparing(TestResult::getDuration));
                if (maxValue.isPresent()) {
                    totalDuration = Math.max(totalDuration, maxValue.get().duration);
                }
            }
        }

        // Print totals if there was more than one test
        if (testIndex > 1) {
            writer.print("<tr bgcolor=\"lightblue\">");
            writer.print("<th>Total</th>");
            writeTableHeader(integerFormat.format(totalTestsCount), "num");
            writeTableHeader(integerFormat.format(totalPassedTests), (totalPassedTests > 0 ? "num passed" : "num"));
            writeTableHeader(
                    integerFormat.format(totalFailedTests), (totalFailedTests > 0 ? "num attn" : "num"));

            writeTableHeader(
                    integerFormat.format(totalSkippedTests), (totalSkippedTests > 0 ? "num skipped" : "num"));
            writer.print("<th colspan=\"2\"></th>");

            writeTableHeader(ReusuableUtility.getInstance().convertTimeToString(totalDuration), "num");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    /** Writes a summary of all the test scenarios. <HERE> </HERE>*/
    protected void writeScenarioSummary() {
        writer.print("<div class=\"easy-test-summary\">");
        writer.print("<table class=\"table-bordered easy-overview\" id='summary'>");
        writer.print("<thead>");
        writer.print("<tr>");
        writer.print("<th>Class</th>");
        writer.print("<th>Method</th>");
        writer.print("<th>Exception</th>");
        writer.print("<th>Screenshot</th>");
        writer.print("<th>Start Time</th>");
//        writer.print("<th>Time (ms)</th>");
        writer.print("<th>End Time</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tbody><tr><th colspan=\"6\">");
            //writer.print(Utils.escapeHtml(suiteResult.getSuiteName()));
            writer.print("</th></tr></tbody>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.printf("<tbody id=\"t%d\">", testIndex);

                String testName = Utils.escapeHtml(testResult.getTestName());
                int startIndex = scenarioIndex;

                scenarioIndex +=
                        writeScenarioSummary(
                                testName + " &#8212; failed (configuration methods)",
                                testResult.getFailedConfigurationResults(),
                                "failed",
                                scenarioIndex);
                scenarioIndex +=
                        writeScenarioSummary(
                                testName + " &#8212; failed",
                                testResult.getFailedTestResults(),
                                "failed",
                                scenarioIndex);
//                scenarioIndex +=
//                        writeScenarioSummary(
//                                testName + " &#8212; skipped (configuration methods)",
//                                testResult.getSkippedConfigurationResults(),
//                                "skipped",
//                                scenarioIndex);
//                scenarioIndex +=
//                        writeScenarioSummary(
//                                testName + " &#8212; skipped",
//                                testResult.getSkippedTestResults(),
//                                "skipped",
//                                scenarioIndex);
//                scenarioIndex +=
//                        writeScenarioSummary(
//                                testName + " &#8212; retried",
//                                testResult.getRetriedTestResults(),
//                                "retried",
//                                scenarioIndex);
//                scenarioIndex +=
//                        writeScenarioSummary(
//                                testName + " &#8212; passed",
//                                testResult.getPassedTestResults(),
//                                "passed",
//                                scenarioIndex);

                if (scenarioIndex == startIndex) {
                    writer.print("<tr><th colspan=\"4\" class=\"invisible\"/></tr>");
                }

                writer.println("</tbody>");

                testIndex++;
            }
        }

        writer.println("</table>");
    }

    /** Writes the scenario summary for the results of a given state for a single test. */
    private int writeScenarioSummary(
            String description,
            List<ClassResult> classResults,
            String cssClassPrefix,
            int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            writer.print("<tr><th colspan=\"6\">");
            writer.print(description);
            writer.print("</th></tr>");

            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            for (ClassResult classResult : classResults) {
                String cssClass = cssClassPrefix + ((classIndex % 2) == 0 ? "even" : "odd");

                buffer.setLength(0);

                int scenariosPerClass = 0;
                int methodIndex = 0;
                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    ITestResult firstResult = results.iterator().next();
                    String methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());
                    long start = firstResult.getStartMillis();
//                    long end = firstResult.getEndMillis() - start;
                    long end = firstResult.getEndMillis();

                    String shortException="";
                    String failureScreenShot="";

                    Throwable exception=firstResult.getThrowable();
                    boolean hasThrowable = exception != null;
                    if(hasThrowable){
                        String str = Utils.shortStackTrace(exception, true);
                        Scanner scanner = new Scanner(str);
                        shortException = scanner.nextLine();
                        scanner.close();
                        List<String> msgs = Reporter.getOutput(firstResult);
                        boolean hasReporterOutput = msgs.size() > 0;
                        if(hasReporterOutput){
                            for (String info : msgs) {
                                failureScreenShot+=info+"<br/>";
                            }
                        }
                    }


                    DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(start);

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTimeInMillis(end);


                    // The first method per class shares a row with the class
                    // header
                    if (methodIndex > 0) {
                        buffer.append("<tr class=\"").append(cssClass).append("\">");
                    }

                    // Write the timing information with the first scenario per
                    // method
                    buffer
                            .append("<td><a href=\"#m")
                            .append(scenarioIndex)
                            .append("\">")
                            .append(methodName)
                            .append("</a></td>")
                            .append("<td rowspan=\"")
                            .append("\">")
                            .append(shortException)
                            .append("</td>").append("<td rowspan=\"")
                            .append("\">").append(failureScreenShot)
                            .append("</td>").append("<td rowspan=\"")
                            .append("\">").append(formatter.format(startTime.getTime()))
                            .append("</td>").append("<td rowspan=\"")
                            .append("\">").append(formatter.format(endTime.getTime())).append("</td></tr>");


                    scenarioIndex++;

                    // Write the remaining scenarios for the method
                    for (int i = 1; i < resultsCount; i++) {
                        buffer
                                .append("<tr class=\"")
                                .append(cssClass)
                                .append("\">")
                                .append("<td><a href=\"#m")
                                .append(scenarioIndex)
                                .append("\">")
                                .append(methodName)
                                .append("</a></td></tr>");
                        scenarioIndex++;
                    }

                    scenariosPerClass += resultsCount;
                    methodIndex++;
                }

                // Write the test results for the class
                writer.print("<tr class=\"");
                writer.print(cssClass);
                writer.print("\">");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass);
                writer.print("\">");
                writer.print(Utils.escapeHtml(classResult.getClassName()));
                writer.print("</td>");
                writer.print(buffer);

                classIndex++;
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /** Writes the details for all test scenarios. */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<h2>");
                writer.print(Utils.escapeHtml(testResult.getTestName()));
                writer.print("</h2>");

                scenarioIndex +=
                        writeScenarioDetails(testResult.getFailedConfigurationResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getFailedTestResults(), scenarioIndex);
                scenarioIndex +=
                        writeScenarioDetails(testResult.getSkippedConfigurationResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getSkippedTestResults(), scenarioIndex);
//                scenarioIndex += writeScenarioDetails(testResult.getRetriedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getPassedTestResults(), scenarioIndex);
            }
        }
    }

    /** Writes the scenario details for the results of a given state for a single test. */
    private int writeScenarioDetails(List<ClassResult> classResults, int startingScenarioIndex) {
        int scenarioIndex = startingScenarioIndex;
        for (ClassResult classResult : classResults) {
            String className = classResult.getClassName();
            for (MethodResult methodResult : classResult.getMethodResults()) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();

                String label =
                        Utils.escapeHtml(
                                className + "#" + results.iterator().next().getMethod().getMethodName());
                for (ITestResult result : results) {
                    writeScenario(scenarioIndex, label, result);
                    scenarioIndex++;
                }
            }
        }

        return scenarioIndex - startingScenarioIndex;
    }

    /** Writes the details for an individual test scenario. */
    private void writeScenario(int scenarioIndex, String label, ITestResult result) {
        writer.print("<h3 id=\"m");
        writer.print(scenarioIndex);
        writer.print("\">");
        //writer.print(label);
        writer.print("</h3>");

        writer.print("<table class=\"table-bordered result\">");

        boolean hasRows = false;

        // Write test parameters (if any)
        Object[] parameters = result.getParameters();
        int parameterCount = (parameters == null ? 0 : parameters.length);
//        hasRows = dumpParametersInfo("Factory Parameter", result.getFactoryParameters());
//        parameters = result.getParameters();
//        parameterCount = (parameters == null ? 0 : parameters.length);
//        hasRows = dumpParametersInfo("Parameter", result.getParameters());

        if (parameterCount > 0) {
            writer.print("<tr class=\"param\">");
            for (int i = 1; i <= parameterCount; i++) {
                writer.print("<th>Parameter #");
                writer.print(i);
                writer.print("</th>");
            }
            writer.print("</tr><tr class=\"param stripe\">");
            for (Object parameter : parameters) {
                writer.print("<td>");
                writer.print(Utils.escapeHtml(Utils.toString(parameter)));
                writer.print("</td>");
            }
            writer.print("</tr>");
            hasRows = true;
        }

        // Write reporter messages (if any)
        List<String> reporterMessages = Reporter.getOutput(result);
        if (!reporterMessages.isEmpty()) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">Messages</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeReporterMessages(reporterMessages);
            writer.print("</td></tr>");
            hasRows = true;
        }

        // Write exception (if any)
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writer.print(
                    (result.getStatus() == ITestResult.SUCCESS ? "Expected Exception" : "Exception"));
            writer.print("</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeStackTrace(throwable);
            writer.print("</td></tr>");
            hasRows = true;
        }

        if (!hasRows) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(" class=\"invisible\"/></tr>");
        }

        writer.print("</table>");
        //writer.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }

    private boolean dumpParametersInfo(String prefix, Object[] parameters) {
        int parameterCount = (parameters == null ? 0 : parameters.length);
        if (parameterCount == 0) {
            return false;
        }
        writer.print("<tr class=\"param\">");
        for (int i = 1; i <= parameterCount; i++) {
            writer.print(String.format("<th>%s #", prefix));
            writer.print(i);
            writer.print("</th>");
        }
        writer.print("</tr><tr class=\"param stripe\">");
        for (Object parameter : parameters) {
            writer.print("<td>");
            writer.print(Utils.escapeHtml(Utils.toString(parameter)));
            writer.print("</td>");
        }
        writer.print("</tr>");
        return true;
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
            writer.print(Utils.escapeHtml(iterator.next()));
        } else {
            writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
                writer.print(Utils.escapeHtml(iterator.next()));
            } else {
                writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS class names.
     *
     * @param tag the tag name
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /** Groups {@link TestResult}s by suite. */
    protected  class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();
        private final ParallelMode mode;

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            mode = suite.getXmlSuite().getParallel();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /** @return the test results (possibly empty) */
        public List<TestResult> getTestResults() {
            return testResults;
        }

        public ParallelMode getParallelMode() {
            return mode;
        }
    }

    /** Groups {@link ClassResult}s by test, type (configuration or test), and status. */
    protected  class TestResult {
        /** Orders test results by class name and then by method name (in lexicographic order). */
        protected  final Comparator<ITestResult> RESULT_COMPARATOR =
                Comparator.comparing((ITestResult o) -> o.getTestClass().getName())
                        .thenComparing(o -> o.getMethod().getMethodName());

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        //private final List<ClassResult> retriedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        // private final int retriedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
            //Set<ITestResult> skippedTests = pruneSkipped(rawSkipped);
            //Set<ITestResult> retriedTests = pruneRetried(rawSkipped);

            Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            //retriedTestResults = groupResults(retriedTests);
            passedTestResults = groupResults(passedTests);

            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();


            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;

            duration = context.getEndDate().getTime() - context.getStartDate().getTime();

            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

//        private  Set<ITestResult> pruneSkipped(Set<ITestResult> results) {
//            return results.stream().filter(result -> !result.wasRetried()).collect(Collectors.toSet());
//        }
//
//        private  Set<ITestResult> pruneRetried(Set<ITestResult> results) {
//            return results.stream().filter(ITestResult::wasRetried).collect(Collectors.toSet());
//        }

        /**
         * Groups test results by method and then by class.
         *
         * @param results All test results
         * @return Test result grouped by method and class
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList,RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName, resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName, resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }
        public Date getTestStartTime() {
            return testStartTime;
        }

        public Date getTestEndTime() {
            return testEndTime;
        }
        /** @return the results for failed configurations (possibly empty) */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /** @return the results for failed tests (possibly empty) */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /** @return the results for skipped configurations (possibly empty) */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /** @return the results for skipped tests (possibly empty) */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

//        public List<ClassResult> getRetriedTestResults() {
//            return retriedTestResults;
//        }

        /** @return the results for passed tests (possibly empty) */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

//        public int getRetriedTestCount() {
//            return retriedTestCount;
//        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }
        public int getTestCount() {
            return testCount;
        }


        /**
         * Formats an array of groups for display.
         *
         * @param groups The groups
         * @return The String value of the groups
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /** Groups {@link MethodResult}s by class. */
    protected  class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /** @return the non-null, non-empty {@link MethodResult} list */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /** Groups test results by method. */
    protected  class MethodResult {
        private final List<ITestResult> results;

        /** @param results the non-null, non-empty result list */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /** @return the non-null, non-empty result list */
        public List<ITestResult> getResults() {
            return results;
        }
    }

    protected void writeTestDetails() {

        writer.print("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"stripes easy-overview\">");
        writer.print("<tr>");
        writer.print("<th>Project</th>");
        writer.print("<th>Platform</th>");
        writer.print("<th>Env</th>");
        writer.print("<th>Device</th>");
        writer.print("<th>Type</th>");
//        writer.print("<th>Time (ms)</th>");
//        writer.print("<th>Included Groups</th>");
//        writer.print("<th>Excluded Groups</th>");
        writer.print("<th>Test Coverage</th>");
        writer.print("<th>Automation Report URL</th>");
        writer.print("<th>Zephyr Url</th>");
        writer.println("</tr>");

        writer.print("<tr");
        writer.print(">");
        String paltform= System.getenv("platform") == null ? PropertyReader.getInstance().getPropValues("config.properties","RUN_ON"): System.getenv("platform");
        String env= System.getenv("env")== null ? "qa2": System.getenv("env");
        String device= System.getenv("device")== null ? "samsung": System.getenv("device");
        String type= System.getenv("type")== null ? "sanity": System.getenv("type");
        String automatableUrl= System.getenv("AutomatableUrl")== null ? "#": System.getenv("AutomatableUrl");
        String coverage= System.getenv("Automated")== null ? "45": String.valueOf((Float.parseFloat(System.getenv("Automated"))/ 	Float.parseFloat(System.getenv("Automatable")))*100);
        String zephyrUrl= System.getenv("zephyrUrl")== null ? "#": System.getenv("zephyrUrl");
        writeColumnValue("EnlightenMobile", "num");
        writeColumnValue(paltform, "");
        writeColumnValue(env, "num");
        writeColumnValue(device, "num");
        writeColumnValue(type, "num");
        writeColumnValue(coverage, "num");
        writeColumnHref(automatableUrl, "num");
        if(System.getenv("device")== null ? false: Boolean.parseBoolean(System.getenv("jira")))
            writeColumnHref(zephyrUrl, "num");
        else  writeColumnValue("-", "");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</div>");
        writer.print("<br>");
    }

    protected void writeDocumentParagraph() {
        writer.print("<br>");
        writer.println("<div>");
        writer.println("<table class=\"table-bordered easy-overview\">");
        writeTableContents("Project", "EnlightenMobile");
        String paltform= System.getenv("Platform") == null ? PropertyReader.getInstance().getPropValues("config.properties","RUN_ON"): System.getenv("Platform");
        writeTableContents("Platform", paltform);
        String env= System.getenv("env")== null ? "qa2": System.getenv("env");
        writeTableContents("Env", env);
        String device= System.getenv("device")== null ? "samsung": System.getenv("device");
        writeTableContents("Device", device);
        String type= System.getenv("type")== null ? "sanity": System.getenv("type");
        writeTableContents("Type", type);
        String automatableUrl= System.getenv("AutomatableUrl")== null ? "#": System.getenv("AutomatableUrl");
        writeTableContentLink("Automation Report URL", automatableUrl);
        String coverage= System.getenv("Automated")== null ? "45": String.valueOf((Float.parseFloat(System.getenv("Automated"))/ 	Float.parseFloat(System.getenv("Automatable")))*100);
        writeTableContents("Test Coverage", coverage);
        String zephyrUrl= System.getenv("zephyrUrl")== null ? "#": System.getenv("zephyrUrl");
        writeTableContentLink("Zephyr URL", zephyrUrl);
        writer.println("</table>");
        writer.println("</div>");
        writer.print("<br>");
    }

    protected void writeColumnValue(String value, String css) {
        writer.print("<th class=\""+css+"\">"+value+"</th>");
    }

    protected void writeColumnHref(String value, String css) {
        writer.print("<th class=\""+css+"\"><a href=\""+value+"\" target = _blank>click here</a></th>");
    }

    protected void writeTableContents(String title, String value) {
        writer.print("<tr>");
        writer.print("<th>"+title+"</th>");
        writer.print("<th>"+value+"</th>");
        writer.println("</tr>");
    }

    protected void writeTableContentLink(String title, String value) {
        writer.print("<tr>");
        writer.print("<th>"+title+"</th>");
        writer.print("<th><a href="+value+" target = _blank>"+value+"</a></th>");
        writer.println("</tr>");
    }
}
