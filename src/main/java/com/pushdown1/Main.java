package com.pushdown1;

import java.sql.Connection;

import com.pushdown1.dao.DBConnection;
import com.pushdown1.dao.DataRetriever;
import com.pushdown1.model.InvoiceStatusTotals;

public class Main {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();
        System.out.println("=== PUSH-DOWN PROCESSING TESTS ===\n");

        testConnection();
        testQ1(dr);
        testQ2(dr);
        testQ3(dr);
        testQ4(dr);
        testQ5A(dr);
        testQ5B(dr);

        System.out.println("\n=== This is the end ===\n");
    }

    static void testConnection() {
        System.out.println("Test Connection) DBConnection.getConnection()");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("PASS: Connexion réussie à " + conn.getMetaData().getURL());
            }
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
        }
        System.out.println();
    }

    static void testQ1(DataRetriever dr) {
        System.out.println("Test Q1) findInvoiceTotals()");
        dr.findInvoiceTotals()
                .forEach(t -> System.out.println(t.getId() + " | " + t.getCustomerName() + " | " + t.getTotal()));
        System.out.println();
    }

    static void testQ2(DataRetriever dr) {
        System.out.println("Test Q2) findConfirmedAndPaidInvoiceTotals()");
        dr.findConfirmedAndPaidInvoiceTotals()
                .forEach(t -> System.out.println(t.getId() + " | " + t.getCustomerName() + " | " + t.getStatus() + " | " + t.getTotal()));
        System.out.println();
    }

    static void testQ3(DataRetriever dr) {
        System.out.println("Test Q3) computeStatusTotals()");
        InvoiceStatusTotals totals = dr.computeStatusTotals();
        System.out.println("total_paid = " + totals.getTotalPaid());
        System.out.println("total_confirmed = " + totals.getTotalConfirmed());
        System.out.println("total_draft = " + totals.getTotalDraft());
        System.out.println();
    }

    static void testQ4(DataRetriever dr) {
        System.out.println("Test Q4) computeWeightedTurnover()");
        System.out.printf("%.2f%n", dr.computeWeightedTurnover());
        System.out.println();
    }

    static void testQ5A(DataRetriever dr) {
        System.out.println("Test Q5-A) findInvoiceTaxSummaries()");
        dr.findInvoiceTaxSummaries()
                .forEach(t -> System.out.println(
                t.getId() + " | HT " + t.getHt().setScale(2)
                + " | TVA " + t.getTva().setScale(2)
                + " | TTC " + t.getTtc().setScale(2)
        ));
        System.out.println();
    }

    static void testQ5B(DataRetriever dr) {
        System.out.println("Test Q5-B) computeWeightedTurnoverTtc()");
        System.out.println(dr.computeWeightedTurnoverTtc().setScale(2));
        System.out.println();
    }
}
