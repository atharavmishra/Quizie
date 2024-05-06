package com.aksha.unnatishop.Retrofitclass.Modellist.payment_model;

public class Invoicepayment{
    public String invoice_id;
    public String order_id;
    public int product_id;
    public String invoice_number;
    public String product_name;
    public String partner_gst;
    public String p_qnty;
    public String invoice_amount;
    public String balance_amount;

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPartner_gst() {
        return partner_gst;
    }

    public void setPartner_gst(String partner_gst) {
        this.partner_gst = partner_gst;
    }

    public String getP_qnty() {
        return p_qnty;
    }

    public void setP_qnty(String p_qnty) {
        this.p_qnty = p_qnty;
    }

    public String getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getBalance_amount() {
        return balance_amount;
    }

    public void setBalance_amount(String balance_amount) {
        this.balance_amount = balance_amount;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public double getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(double cash_discount) {
        this.cash_discount = cash_discount;
    }

    public int getOverdue_interest() {
        return overdue_interest;
    }

    public void setOverdue_interest(int overdue_interest) {
        this.overdue_interest = overdue_interest;
    }

    public int getOverdue_days() {
        return overdue_days;
    }

    public void setOverdue_days(int overdue_days) {
        this.overdue_days = overdue_days;
    }

    public int getDiscount_days() {
        return discount_days;
    }

    public void setDiscount_days(int discount_days) {
        this.discount_days = discount_days;
    }

    public String order_date;
    public double cash_discount;
    public int overdue_interest;
    public int overdue_days;
    public int discount_days;
}