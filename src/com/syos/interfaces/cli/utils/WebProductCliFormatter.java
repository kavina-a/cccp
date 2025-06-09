package com.syos.interfaces.cli.utils;

import com.syos.application.dto.WebProductDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class WebProductCliFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void printHeader() {
        System.out.printf("%-10s | %-20s | %-8s | %-20s\n",
                "ItemCode", "Name", "Price", "Updated At");
        System.out.println("---------------------------------------------------------------");
    }

    public static void print(WebProductDTO dto) {
        System.out.printf("%-10s | %-20s | %-8.2f | %-20s\n",
                dto.getItemCode(),
                dto.getItemName(),
                dto.getPrice(),
                dto.getUpdatedDateTime() != null ? formatter.format(dto.getUpdatedDateTime()) : "N/A");
    }

    public static void printList(List<WebProductDTO> dtos) {
        printHeader();
        for (WebProductDTO dto : dtos) {
            print(dto);
        }
    }
}