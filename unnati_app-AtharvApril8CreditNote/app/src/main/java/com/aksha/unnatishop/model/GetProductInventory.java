package com.aksha.unnatishop.model;

import java.util.ArrayList;

public class GetProductInventory {
    public boolean success;
    public ArrayList<InventoryProduct> products;
    public String listcount;
    public String total;


    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<InventoryProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<InventoryProduct> products) {
        this.products = products;
    }

    public String getListcount() {
        return listcount;
    }

    public void setListcount(String listcount) {
        this.listcount = listcount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
