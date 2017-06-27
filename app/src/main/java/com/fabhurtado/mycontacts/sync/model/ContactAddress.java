package com.fabhurtado.mycontacts.sync.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents the contact's address json structure.
 *
 * @author FabHurtado
 */

public class ContactAddress {

    @SerializedName("work")
    @Expose
    private String work;

    @SerializedName("home")
    @Expose
    private String home;

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }
}
