package com.fabhurtado.mycontacts.sync.service;

import com.fabhurtado.mycontacts.sync.model.Contact;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * In this interface are defined the Endpoints that
 * Retrofit will use to get data.
 *
 * @author FabHurtado
 */

public interface ContactService {

    @GET("/contacts")
    Call<ArrayList<Contact>> getContacts();

    @GET("/contacts/{id}")
    Call<Contact> getContactBy(@Path("id") String id);
}
