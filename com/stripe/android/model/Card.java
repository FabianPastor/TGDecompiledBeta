package com.stripe.android.model;

import com.stripe.android.util.DateUtils;
import com.stripe.android.util.StripeTextUtils;

public class Card {
    public static final String[] PREFIXES_AMERICAN_EXPRESS = new String[]{"34", "37"};
    public static final String[] PREFIXES_DINERS_CLUB = new String[]{"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
    public static final String[] PREFIXES_DISCOVER = new String[]{"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = new String[]{"35"};
    public static final String[] PREFIXES_MASTERCARD = new String[]{"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55"};
    public static final String[] PREFIXES_VISA = new String[]{"4"};
    private String addressCity;
    private String addressCountry;
    private String addressLine1;
    private String addressLine2;
    private String addressState;
    private String addressZip;
    private String brand;
    private String country;
    private String currency;
    private String cvc;
    private Integer expMonth;
    private Integer expYear;
    private String fingerprint;
    private String funding;
    private String last4;
    private String name;
    private String number;

    public Card(String number, Integer expMonth, Integer expYear, String cvc, String name, String addressLine1, String addressLine2, String addressCity, String addressState, String addressZip, String addressCountry, String brand, String last4, String fingerprint, String funding, String country, String currency) {
        this.number = StripeTextUtils.nullIfBlank(normalizeCardNumber(number));
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = StripeTextUtils.nullIfBlank(cvc);
        this.name = StripeTextUtils.nullIfBlank(name);
        this.addressLine1 = StripeTextUtils.nullIfBlank(addressLine1);
        this.addressLine2 = StripeTextUtils.nullIfBlank(addressLine2);
        this.addressCity = StripeTextUtils.nullIfBlank(addressCity);
        this.addressState = StripeTextUtils.nullIfBlank(addressState);
        this.addressZip = StripeTextUtils.nullIfBlank(addressZip);
        this.addressCountry = StripeTextUtils.nullIfBlank(addressCountry);
        if (StripeTextUtils.asCardBrand(brand) == null) {
            brand = getBrand();
        }
        this.brand = brand;
        if (StripeTextUtils.nullIfBlank(last4) == null) {
            last4 = getLast4();
        }
        this.last4 = last4;
        this.fingerprint = StripeTextUtils.nullIfBlank(fingerprint);
        this.funding = StripeTextUtils.asFundingType(funding);
        this.country = StripeTextUtils.nullIfBlank(country);
        this.currency = StripeTextUtils.nullIfBlank(currency);
    }

    public Card(String number, Integer expMonth, Integer expYear, String cvc, String name, String addressLine1, String addressLine2, String addressCity, String addressState, String addressZip, String addressCountry, String currency) {
        this(number, expMonth, expYear, cvc, name, addressLine1, addressLine2, addressCity, addressState, addressZip, addressCountry, null, null, null, null, null, currency);
    }

    public boolean validateNumber() {
        if (StripeTextUtils.isBlank(this.number)) {
            return false;
        }
        String rawNumber = this.number.trim().replaceAll("\\s+|-", TtmlNode.ANONYMOUS_REGION_ID);
        if (StripeTextUtils.isBlank(rawNumber) || !StripeTextUtils.isWholePositiveNumber(rawNumber) || !isValidLuhnNumber(rawNumber)) {
            return false;
        }
        String updatedType = getBrand();
        if ("American Express".equals(updatedType)) {
            if (rawNumber.length() != 15) {
                return false;
            }
            return true;
        } else if ("Diners Club".equals(updatedType)) {
            if (rawNumber.length() != 14) {
                return false;
            }
            return true;
        } else if (rawNumber.length() != 16) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateExpiryDate() {
        if (validateExpMonth() && validateExpYear() && !DateUtils.hasMonthPassed(this.expYear.intValue(), this.expMonth.intValue())) {
            return true;
        }
        return false;
    }

    public boolean validateCVC() {
        boolean z = true;
        if (StripeTextUtils.isBlank(this.cvc)) {
            return false;
        }
        String cvcValue = this.cvc.trim();
        String updatedType = getBrand();
        boolean validLength;
        if ((updatedType != null || cvcValue.length() < 3 || cvcValue.length() > 4) && !(("American Express".equals(updatedType) && cvcValue.length() == 4) || cvcValue.length() == 3)) {
            validLength = false;
        } else {
            validLength = true;
        }
        if (!(StripeTextUtils.isWholePositiveNumber(cvcValue) && validLength)) {
            z = false;
        }
        return z;
    }

    public boolean validateExpMonth() {
        return this.expMonth != null && this.expMonth.intValue() >= 1 && this.expMonth.intValue() <= 12;
    }

    public boolean validateExpYear() {
        return (this.expYear == null || DateUtils.hasYearPassed(this.expYear.intValue())) ? false : true;
    }

    public String getNumber() {
        return this.number;
    }

    public String getCVC() {
        return this.cvc;
    }

    public Integer getExpMonth() {
        return this.expMonth;
    }

    public Integer getExpYear() {
        return this.expYear;
    }

    public String getName() {
        return this.name;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public String getAddressCity() {
        return this.addressCity;
    }

    public String getAddressZip() {
        return this.addressZip;
    }

    public String getAddressState() {
        return this.addressState;
    }

    public String getAddressCountry() {
        return this.addressCountry;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getLast4() {
        if (!StripeTextUtils.isBlank(this.last4)) {
            return this.last4;
        }
        if (this.number == null || this.number.length() <= 4) {
            return null;
        }
        this.last4 = this.number.substring(this.number.length() - 4, this.number.length());
        return this.last4;
    }

    @Deprecated
    public String getType() {
        return getBrand();
    }

    public String getBrand() {
        if (StripeTextUtils.isBlank(this.brand) && !StripeTextUtils.isBlank(this.number)) {
            String evaluatedType;
            if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_AMERICAN_EXPRESS)) {
                evaluatedType = "American Express";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DISCOVER)) {
                evaluatedType = "Discover";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_JCB)) {
                evaluatedType = "JCB";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DINERS_CLUB)) {
                evaluatedType = "Diners Club";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_VISA)) {
                evaluatedType = "Visa";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_MASTERCARD)) {
                evaluatedType = "MasterCard";
            } else {
                evaluatedType = "Unknown";
            }
            this.brand = evaluatedType;
        }
        return this.brand;
    }

    private boolean isValidLuhnNumber(String number) {
        boolean z = true;
        boolean isOdd = true;
        int sum = 0;
        for (int index = number.length() - 1; index >= 0; index--) {
            char c = number.charAt(index);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digitInteger = Integer.parseInt(TtmlNode.ANONYMOUS_REGION_ID + c);
            if (isOdd) {
                isOdd = false;
            } else {
                isOdd = true;
            }
            if (isOdd) {
                digitInteger *= 2;
            }
            if (digitInteger > 9) {
                digitInteger -= 9;
            }
            sum += digitInteger;
        }
        if (sum % 10 != 0) {
            z = false;
        }
        return z;
    }

    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", TtmlNode.ANONYMOUS_REGION_ID);
    }
}
