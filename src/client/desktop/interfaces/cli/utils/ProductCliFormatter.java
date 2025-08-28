package client.desktop.interfaces.cli.utils;

import server.application.dto.ProductDTO;

import java.util.List;

public class ProductCliFormatter {
    public static void printHeader() {
        System.out.printf("%-25s %-40s %12s%n", "Item Code", "Item Name", "Price (Rs.)");
        System.out.println("----------------------------------------------------------------------------------");
    }

    public static void print(ProductDTO p) {
        System.out.printf("%-25s %-40s Rs. %9.2f%n",
                p.getItemCode(),
                p.getItemName(),
                p.getPrice());
    }

    public static void printList(List<ProductDTO> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        printHeader();
        for (ProductDTO p : products) {
            print(p);
        }
    }
}
