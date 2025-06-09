package com.syos.interfaces.cli.utils;

import java.time.LocalDate;

public class BillSerialFormatter {
    public static String format(int serialNumber, LocalDate billDate) {
        return String.format("BILL-%s-%04d",
                billDate.toString().replace("-", ""),
                serialNumber);
    }
}
