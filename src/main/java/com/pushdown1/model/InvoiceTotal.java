package com.pushdown1.model;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceTotal {
    private Integer id;
    private String customerName;
    private InvoiceStatus status;
    private List<InvoiceLine> lines;
    private BigDecimal total;

    public InvoiceTotal(Integer id, String customerName, InvoiceStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
    }

    public Integer getId() { return id; }
    public String getCustomerName() { return customerName; }
    public InvoiceStatus getStatus() { return status; }
    public List<InvoiceLine> getLines() { return lines; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
