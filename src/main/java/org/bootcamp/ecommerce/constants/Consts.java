package org.bootcamp.ecommerce.constants;

public class Consts {
    public static final String JWT_KEY = "axz";
    public static final String FIELDVALUESREGEX = "\\s*,\\s*";
    public static final String SELLER = "SELLER";
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADDRESS_REGEX = "^[a-zA-Z\\s.'-]*$";
    public static final String ZIPCODE_REGEX = "\\d{6}";
    public static final String PHONE_NUMBER_REGEX = "\\d{10}";
    private Consts() {
    }
}
