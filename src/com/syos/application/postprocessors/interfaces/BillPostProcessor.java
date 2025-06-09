package com.syos.application.postprocessors.interfaces;

import com.syos.domain.entity.Bill;

import java.sql.Connection;
import java.sql.SQLException;

public interface BillPostProcessor {
    void process(Bill bill, Connection connection) throws SQLException;
}