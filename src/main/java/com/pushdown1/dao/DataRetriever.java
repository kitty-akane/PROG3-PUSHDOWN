package com.pushdown1.dao;

import com.pushdown1.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // Q1 - Total par facture
    public List<InvoiceTotal> findInvoiceTotals() {
        String sql = """
                SELECT i.id, i.customer_name,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                GROUP BY i.id, i.customer_name
                ORDER BY i.id
                """;

        List<InvoiceTotal> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InvoiceTotal inv = new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        null
                );
                inv.setTotal(rs.getBigDecimal("total"));
                result.add(inv);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    // Q2 - Total des factures CONFIRMED et PAID
    public List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        String sql = """
                SELECT i.id, i.customer_name, i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                WHERE i.status IN ('CONFIRMED', 'PAID')
                GROUP BY i.id, i.customer_name, i.status
                ORDER BY i.id
                """;

        List<InvoiceTotal> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InvoiceTotal inv = new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status"))
                );
                inv.setTotal(rs.getBigDecimal("total"));
                result.add(inv);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    // Q3 - Totaux cumulés par statut
    public InvoiceStatusTotals computeStatusTotals() {
        String sql = """
                SELECT
                    SUM(CASE WHEN i.status = 'PAID'
                        THEN il.quantity * il.unit_price ELSE 0 END) AS total_paid,
                    SUM(CASE WHEN i.status = 'CONFIRMED'
                        THEN il.quantity * il.unit_price ELSE 0 END) AS total_confirmed,
                    SUM(CASE WHEN i.status = 'DRAFT'
                        THEN il.quantity * il.unit_price ELSE 0 END) AS total_draft
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new InvoiceStatusTotals(
                        rs.getBigDecimal("total_paid"),
                        rs.getBigDecimal("total_confirmed"),
                        rs.getBigDecimal("total_draft")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new InvoiceStatusTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    // Q4 - Chiffre d'affaires pondéré
    public Double computeWeightedTurnover() {
        String sql = """
                SELECT SUM(
                    il.quantity * il.unit_price *
                    CASE i.status
                        WHEN 'PAID'      THEN 1.00
                        WHEN 'CONFIRMED' THEN 0.50
                        WHEN 'DRAFT'     THEN 0.00
                    END
                ) AS weighted_turnover
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("weighted_turnover");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return 0.0;
    }

    // Q5-A - Totaux HT, TVA et TTC par facture
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {
        String sql = """
                SELECT
                    i.id,
                    SUM(il.quantity * il.unit_price) AS ht,
                    SUM(il.quantity * il.unit_price * tc.rate / 100) AS tva,
                    SUM(il.quantity * il.unit_price * (1 + tc.rate / 100)) AS ttc
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                CROSS JOIN tax_config tc
                WHERE tc.label = 'TVA STANDARD'
                GROUP BY i.id
                ORDER BY i.id
                """;

        List<InvoiceTaxSummary> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new InvoiceTaxSummary(
                        rs.getInt("id"),
                        rs.getBigDecimal("ht"),
                        rs.getBigDecimal("tva"),
                        rs.getBigDecimal("ttc")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    // Q5-B - Chiffre d'affaires TTC pondéré
    public BigDecimal computeWeightedTurnoverTtc() {
        String sql = """
                SELECT SUM(
                    il.quantity * il.unit_price * (1 + tc.rate / 100) *
                    CASE i.status
                        WHEN 'PAID'      THEN 1.00
                        WHEN 'CONFIRMED' THEN 0.50
                        WHEN 'DRAFT'     THEN 0.00
                    END
                ) AS weighted_turnover_ttc
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                CROSS JOIN tax_config tc
                WHERE tc.label = 'TVA STANDARD'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("weighted_turnover_ttc");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}
