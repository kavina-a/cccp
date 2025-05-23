package com.syos.domain.entity;

import jakarta.persistence.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tblEmployee")
public class Employee extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private EmployeeRole role;

    @Column(name = "Status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "EmployeeNumber", unique = true)
    private String employeeNumber;

    @Column(name = "HireDate")
    @Temporal(TemporalType.DATE)
    private Date hireDate;


    public EmployeeRole getRole() { return role; }
    public void setRole(EmployeeRole role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
}