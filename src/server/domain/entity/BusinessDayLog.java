package server.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BusinessDayLog {
    private int id;
    private LocalDate businessDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isClosed;
    private Employee openedBy;
    private Employee closedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Employee getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(Employee openedBy) {
        this.openedBy = openedBy;
    }

    public Employee getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Employee closedBy) {
        this.closedBy = closedBy;
    }
}