package com.fabhurtado.mycontacts.utils;

import android.content.Context;

import com.fabhurtado.mycontacts.R;
import com.fabhurtado.mycontacts.sync.model.Contact;
import com.fabhurtado.mycontacts.sync.model.ContactAddress;
import com.fabhurtado.mycontacts.sync.model.ContactPhone;
import com.fabhurtado.mycontacts.view.model.ItemDetail;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.FormattedName;
import ezvcard.property.StructuredName;
import ezvcard.property.Title;

/**
 * Util class for My Contacts app
 *
 * @author FabHurtado
 */

public class MyContactsUtils {

    /**
     * Transform the contact data into a generic list of data
     *
     * @param c          Contact
     * @param context    Context
     * @return a list of ItemDetails
     */
    public static ArrayList<ItemDetail> prepareDetailData(Contact c, Context context){
        ArrayList<ItemDetail> items = new ArrayList<>();

        if(c.getBirthDate() != null){
            items.add(new ItemDetail(ItemDetail.DetailDataType.BIRTH_DATE,
                    R.drawable.ic_calendar,
                    DateFormat.getDateInstance().format(c.getBirthDate()),
                    context.getString(R.string.birth_date)));
        }

        for (ContactPhone cp : c.getPhones()){
            if(cp.getNumber() != null){
                items.add(new ItemDetail(ItemDetail.DetailDataType.PHONE,
                        R.drawable.ic_phone,
                        String.valueOf(cp.getNumber()),
                        context.getString(getPhoneTypeResourceID(cp.getType()))));
            }
        }

        for(ContactAddress ca : c.getAddresses()){
            if(ca.getHome() != null && !ca.getHome().isEmpty()){
                items.add(new ItemDetail(ItemDetail.DetailDataType.ADDRESS,
                        R.drawable.ic_location,
                        ca.getHome(),
                        context.getString(R.string.home)));
            }

            if(ca.getWork() != null && !ca.getWork().isEmpty()){
                items.add(new ItemDetail(ItemDetail.DetailDataType.ADDRESS,
                        R.drawable.ic_location,
                        ca.getWork(),
                        context.getString(R.string.work)));
            }
        }
        return items;
    }

    /**
     * Gets a resource identifier for PhoneType
     *
     * @param type PhoneType
     * @return string resource identifier
     */
    private static int getPhoneTypeResourceID(ContactPhone.PhoneType type){
        int id;
        switch (type){
            case HOME:
                id =  R.string.home;
                break;
            case CELL:
                id =  R.string.cell;
                break;
            case WORK:
                id = R.string.work;
                break;
            default:
                id = R.string.home;
        }
        return id;
    }

    /**
     * Creates a VCF file from Contact data
     *
     * @param contact Contact
     * @param context Context
     * @return File
     */
    public static File prepareVcf(Contact contact, Context context){

        String name = contact.getFirstName() + " " + contact.getLastName();
        String fileName = name + ".vcf";

        File vcfFile = new File(context.getExternalFilesDir(null), fileName);

        VCard vcard = new VCard();
        vcard.setVersion(VCardVersion.V3_0);

        StructuredName n = new StructuredName();
        n.setFamily(contact.getLastName());
        n.setGiven(contact.getFirstName());
        vcard.setStructuredName(n);

        vcard.setFormattedName(new FormattedName(name));

        vcard.addTitle(new Title(DateFormat.getDateInstance().format(contact.getBirthDate())));

        for (ContactPhone cp : contact.getPhones()) {
            if(cp.getNumber() != null){
                switch (cp.getType()){
                    case HOME:
                        vcard.addTelephoneNumber(String.valueOf(cp.getNumber()), TelephoneType.HOME);
                        break;
                    case CELL:
                        vcard.addTelephoneNumber(String.valueOf(cp.getNumber()), TelephoneType.CELL);
                        break;
                    case WORK:
                        vcard.addTelephoneNumber(String.valueOf(cp.getNumber()), TelephoneType.WORK);
                        break;
                }
            }
        }

        for (ContactAddress ca : contact.getAddresses()){
            if(ca.getHome() != null){
                Address adr = new Address();
                adr.setStreetAddress(ca.getHome());
                adr.setLabel(ca.getHome());
                adr.getTypes().add(AddressType.HOME);
                vcard.addAddress(adr);
            }
            if (ca.getWork() != null){
                Address adr = new Address();
                adr.setStreetAddress(ca.getWork());
                adr.setLabel(ca.getWork());
                adr.getTypes().add(AddressType.WORK);
                vcard.addAddress(adr);
            }
        }

        try {
            vcard.write(vcfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vcfFile;
    }

    /**
     * Calculate birthday from birth date
     * and convert it to millis.
     *
     * @param date  String date
     * @return time in millis
     */
    public static long calculateBirthdayInMillis(String date){
        String[] dates = date.split("/");
        Calendar birthday = Calendar.getInstance();
        birthday.set(birthday.get(Calendar.YEAR), Integer.valueOf(dates[1])-1,
                Integer.valueOf(dates[0]));

        Calendar today = Calendar.getInstance();
        //if birthday has passed show it in the following year
        if(birthday.before(today)){
            birthday.add(Calendar.YEAR, 1);
        }

        return birthday.getTimeInMillis();
    }
}
