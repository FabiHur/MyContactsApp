package com.fabhurtado.mycontacts.sync.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * This class represents a Contact that is a perfect
 * match with the contact json structure returned
 * by the server.
 *
 * @author FabHurtado
 */

public class Contact {

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("birth_date")
    @Expose
    private Date birthDate;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("phones")
    @Expose
    private List<ContactPhone> phones;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("addresses")
    @Expose
    private List<ContactAddress> addresses;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<ContactPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactPhone> phones) {
        this.phones = phones;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<ContactAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ContactAddress> addresses) {
        this.addresses = addresses;
    }
}
