package com.pushdown1.model;

import java.math.BigDecimal;

public class InvoiceTaxSummary {
    private int id;
    private BigDecimal ht;
    private BigDecimal tva;
    private BigDecimal ttc;

    public InvoiceTaxSummary(int id, BigDecimal ht, BigDecimal tva, BigDecimal ttc) {
        this.id = id;
        this.ht = ht;
        this.tva = tva;
        this.ttc = ttc;
    }

    public int getId() { return id; }
    public BigDecimal getHt() { return ht; }
    public BigDecimal getTva() { return tva; }
    public BigDecimal getTtc() { return ttc; }
}
