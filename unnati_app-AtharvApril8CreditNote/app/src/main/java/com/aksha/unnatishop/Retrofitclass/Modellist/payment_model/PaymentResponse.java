package com.aksha.unnatishop.Retrofitclass.Modellist.payment_model;

import java.util.ArrayList;

public class PaymentResponse {
    public ArrayList<Invoicepayment> getInvoicepayment() {
        return invoicepayment;

    }

    public void setInvoicepayment(ArrayList<Invoicepayment> invoicepayment) {
        this.invoicepayment = invoicepayment;
    }

    public ArrayList<Invoicepayment> invoicepayment;


}
