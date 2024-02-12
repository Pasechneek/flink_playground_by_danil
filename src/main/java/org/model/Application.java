package org.model;

public class Application {
    public Application(long id, long ucdb_id, Float requested_amount, String product) {
        this.id = id;
        this.ucdb_id = ucdb_id;
        this.requested_amount = requested_amount;
        this.product = product;
    }
    final long id;
    final long ucdb_id;
    final Float requested_amount;
    final String product;

    public long getId() {
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
