package com.whatsapp;

public enum WhatsappAccountStatus {
    BLOCK("block"),SUCCESS("success"),ERROR("none");

    private String reason;

    WhatsappAccountStatus(String reason){
        this.reason = reason;
    }

    public WhatsappAccountStatus setReason(String reason){
        this.reason = reason;
        return this;
    }

    public String getReason(){
        return reason;
    }
}
