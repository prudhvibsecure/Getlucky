package com.bsecure.getlucky.models;

public class AccountModel {

    String bank_id;
    String bank_acc_name;
    String bank_address;
    String bank_acc_no;
    String ifsc_code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_acc_name() {
        return bank_acc_name;
    }

    public void setBank_acc_name(String bank_acc_name) {
        this.bank_acc_name = bank_acc_name;
    }

    public String getBank_address() {
        return bank_address;
    }

    public void setBank_address(String bank_address) {
        this.bank_address = bank_address;
    }

    public String getBank_acc_no() {
        return bank_acc_no;
    }

    public void setBank_acc_no(String bank_acc_no) {
        this.bank_acc_no = bank_acc_no;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }

    public void setIfsc_code(String ifsc_code) {
        this.ifsc_code = ifsc_code;
    }
}
