package com.web.template.Utils.Zapi;

/*
    @desc      This class is meant to keep all the data required for zephyr methods.
    @author    Ostan Dsouza
    @Date      02/06/2020
 */

public class ZephyrModel {

    private Long zephyrProjectId;
    private Long versionId;
    private String cyclePrefix;
    private long testIssueTypeId;
    private String cycleName;
    private String environment;
    private String cycleDuration;
    private String cycleIdZfjCloud;
    private int builNumber;


    public long getTestIssueTypeId() {
        return testIssueTypeId;
    }

    public void setTestIssueTypeId(long testIssueTypeId) {
        this.testIssueTypeId = testIssueTypeId;
    }

    public String getCyclePrefix() {
        return cyclePrefix;
    }

    public void setCyclePrefix(String cyclePrefix) {
        this.cyclePrefix = cyclePrefix;
    }

    public Long getZephyrProjectId() {
        return zephyrProjectId;
    }

    public void setZephyrProjectId(Long zephyrProjectId) {
        this.zephyrProjectId = zephyrProjectId;
    }

    public String getCycleDuration() {
        return cycleDuration;
    }

    public void setCycleDuration(String cycleDuration) {
        this.cycleDuration = cycleDuration;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getCycleIdZfjCloud() {
        return cycleIdZfjCloud;
    }

    public void setCycleIdZfjCloud(String cycleIdZfjCloud) {
        this.cycleIdZfjCloud = cycleIdZfjCloud;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public int getBuilNumber() {
        return builNumber;
    }

    public void setBuilNumber(int builNumber) {
        this.builNumber = builNumber;
    }

}