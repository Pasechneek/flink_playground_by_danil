package org.model;

public class Application {
    public Application(Long id, Long ucdb_id, Float requested_amount, String product) {
        this.id = id;
        this.ucdb_id = ucdb_id;
        this.requested_amount = requested_amount;
        this.product = product;
    }
    final Long id;
    final long ucdb_id;
    final Float requested_amount;
    final String product;

    public Long getId() {
        return id;
    }

    public Long getUcdbId() {
        return this.ucdb_id;
    }

    public Float getRequestedAmount() {
        return this.requested_amount;
    }

    public String getProduct() {
        return product;
    }


}
