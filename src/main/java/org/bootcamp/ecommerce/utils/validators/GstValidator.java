package org.bootcamp.ecommerce.utils.validators;

import org.bootcamp.ecommerce.exception.InvalidGstException;

public class GstValidator {

    private static final String GST_REGEX =
            "^\\d{2}[A-Z]{5}\\d{4}[A-Z][1-9A-Z]Z[0-9A-Z]$";


    private GstValidator() {
    }

    public static boolean isValid(String gst) {
        if (gst == null || gst.isBlank()) {
            return false;
        }
        return gst.matches(GST_REGEX);
    }

    public static void validateGst(String gst) {
        if (!isValid(gst)) {
            throw new InvalidGstException();
        }
    }

}
