package com.fabhurtado.mycontacts.sync.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent the phone structure that
 * match with json structure returned by the server.
 *
 * @author FabHurtado
 */

public class ContactPhone {

    @SerializedName("type")
    @Expose
    private PhoneType type;

    @SerializedName("number")
    @Expose
    private Object number;

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public Object getNumber() {
        return number;
    }

    public void setNumber(Object number) {
        this.number = number;
    }

    public enum PhoneType {
        @SerializedName("Home")
        HOME,
        @SerializedName("Cellphone")
        CELL,
        @SerializedName("Office")
        WORK
    }
}
