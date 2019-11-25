package com.bsecure.getlucky.models;

public class OfferModel {

    String store_name,offer_description,offer_sp_id,status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getOffer_description() {
        return offer_description;
    }

    public void setOffer_description(String offer_description) {
        this.offer_description = offer_description;
    }

    public String getOffer_sp_id() {
        return offer_sp_id;
    }

    public void setOffer_sp_id(String offer_sp_id) {
        this.offer_sp_id = offer_sp_id;
    }
}
