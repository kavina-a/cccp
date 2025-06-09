package com.syos.application.dto.request;

public class BusinessDayLogRequest {
    private boolean isClosed;
    private String initiatedBy;

    public BusinessDayLogRequest(Boolean isClosed, String initiatedBy) {
        this.isClosed = isClosed != null && isClosed;
        this.initiatedBy = initiatedBy;
    }

    public BusinessDayLogRequest() {}

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }


}