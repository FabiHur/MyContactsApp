package com.fabhurtado.mycontacts.sync;

import com.fabhurtado.mycontacts.sync.service.ContactService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author FabHurtado
 */

public class RetrofitClient {

    private static final String SCHEMA = "https";

    private static final String AUTHORITY = "private-d0cc1-iguanafixtest.apiary-mock.com";

    private static final String CONTACT_URL = SCHEMA + "://" + AUTHORITY;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(CONTACT_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static ContactService getContactService(){
        return retrofit.create(ContactService.class);
    }
}
