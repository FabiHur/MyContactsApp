package com.fabhurtado.mycontacts.view.model;

/**
 * ItemDetail is a class created to represent a generic
 * row in Contact detail list.
 *
 * @author FabHurtado
 */

public class ItemDetail {

    //Row type
    private DetailDataType dataType;

    //Resource identifier that will be displayed in the row
    private int imageResource;

    //The value to display, this can be a phone number, an address or a date
    private String value;

    //The name of the value. Example: home, work, cell, etc.
    private String valueType;

    public ItemDetail(DetailDataType dataType, int imageResource, String value, String valueType) {
        this.dataType = dataType;
        this.imageResource = imageResource;
        this.value = value;
        this.valueType = valueType;
    }

    public DetailDataType getDataType() {
        return dataType;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    /**
     * Represents the available types
     */
    public enum DetailDataType{
        PHONE, ADDRESS, BIRTH_DATE
    }
}
