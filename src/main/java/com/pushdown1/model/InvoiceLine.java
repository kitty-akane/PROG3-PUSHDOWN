package com.pushdown1.model;

import java.math.BigDecimal;

public class InvoiceLine {
    private int invoiceId;
    private String label;
    private int quantity;
    private BigDecimal unitPrice;

    public InvoiceLine(int invoiceId, String label, int quantity, BigDecimal unitPrice) {
        this.invoiceId = invoiceId;
        this.label = label;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getInvoiceId() { return invoiceId; }
    public String getLabel() { return label; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
