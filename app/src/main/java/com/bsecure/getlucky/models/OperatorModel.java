package com.bsecure.getlucky.models;

public class OperatorModel {

    String store_name,operator_name,username,password,store_operator_id,status;

    public String getStore_operator_id() {
        return store_operator_id;
    }

    public void setStore_operator_id(String store_operator_id) {
        this.store_operator_id = store_operator_id;
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

    public String getOperator_name() {
        return operator_name;
    }

    public void setOperator_name(String operator_name) {
        this.operator_name = operator_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
