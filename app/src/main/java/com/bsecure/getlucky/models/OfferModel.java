package com.bsecure.getlucky.models;

public class OfferModel {

    String offer_id,store_name,offer_description,offer_sp_id,status,offer_percent,min_amount,max_amount,refer_percent,store_refer_percent,admin_percent,total_percent,offer_percent_description;

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_percent_description() {
        return offer_percent_description;
    }

    public void setOffer_percent_description(String offer_percent_description) {
        this.offer_percent_description = offer_percent_description;
    }

    public String getOffer_percent() {
        return offer_percent;
    }

    public void setOffer_percent(String offer_percent) {
        this.offer_percent = offer_percent;
    }

    public String getMin_amount() {
        return min_amount;
    }

    public void setMin_amount(String min_amount) {
        this.min_amount = min_amount;
    }

    public String getMax_amount() {
        return max_amount;
    }

    public void setMax_amount(String max_amount) {
        this.max_amount = max_amount;
    }

    public String getRefer_percent() {
        return refer_percent;
    }

    public void setRefer_percent(String refer_percent) {
        this.refer_percent = refer_percent;
    }

    public String getStore_refer_percent() {
        return store_refer_percent;
    }

    public void setStore_refer_percent(String store_refer_percent) {
        this.store_refer_percent = store_refer_percent;
    }

    public String getAdmin_percent() {
        return admin_percent;
    }

    public void setAdmin_percent(String admin_percent) {
        this.admin_percent = admin_percent;
    }

    public String getTotal_percent() {
        return total_percent;
    }

    public void setTotal_percent(String total_percent) {
        this.total_percent = total_percent;
    }

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
