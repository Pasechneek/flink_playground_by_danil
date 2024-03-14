package org.model;

public class Application2 {
    public Application2(int id, long ucdb_id, Float requested_amount, String product) {
        this.id = id;
        this.ucdb_id = ucdb_id;
        this.requested_amount = requested_amount;
        this.product = product;
    }

    final int id;
    final long ucdb_id;
    final Float requested_amount;
    final String product;

    public int getId() {
        return id;
    }

    public long getUcdbId() {
        return this.ucdb_id;
    }

    public Float getRequestedAmount() {
        return this.requested_amount;
    }

    public String getProduct() {
        return product;
    }


}
