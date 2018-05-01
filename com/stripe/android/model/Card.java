package com.stripe.android.model;

import com.stripe.android.util.DateUtils;
import com.stripe.android.util.StripeTextUtils;

public class Card
{
  public static final String[] PREFIXES_AMERICAN_EXPRESS = { "34", "37" };
  public static final String[] PREFIXES_DINERS_CLUB;
  public static final String[] PREFIXES_DISCOVER = { "60", "62", "64", "65" };
  public static final String[] PREFIXES_JCB = { "35" };
  public static final String[] PREFIXES_MASTERCARD = { "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55" };
  public static final String[] PREFIXES_VISA;
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
  private String number = StripeTextUtils.nullIfBlank(normalizeCardNumber(paramString1));
  
  static
  {
    PREFIXES_DINERS_CLUB = new String[] { "300", "301", "302", "303", "304", "305", "309", "36", "38", "39" };
    PREFIXES_VISA = new String[] { "4" };
  }
  
  public Card(String paramString1, Integer paramInteger1, Integer paramInteger2, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10)
  {
    this(paramString1, paramInteger1, paramInteger2, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8, paramString9, null, null, null, null, null, paramString10);
  }
  
  public Card(String paramString1, Integer paramInteger1, Integer paramInteger2, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13, String paramString14, String paramString15)
  {
    this.expMonth = paramInteger1;
    this.expYear = paramInteger2;
    this.cvc = StripeTextUtils.nullIfBlank(paramString2);
    this.name = StripeTextUtils.nullIfBlank(paramString3);
    this.addressLine1 = StripeTextUtils.nullIfBlank(paramString4);
    this.addressLine2 = StripeTextUtils.nullIfBlank(paramString5);
    this.addressCity = StripeTextUtils.nullIfBlank(paramString6);
    this.addressState = StripeTextUtils.nullIfBlank(paramString7);
    this.addressZip = StripeTextUtils.nullIfBlank(paramString8);
    this.addressCountry = StripeTextUtils.nullIfBlank(paramString9);
    paramString1 = paramString10;
    if (StripeTextUtils.asCardBrand(paramString10) == null) {
      paramString1 = getBrand();
    }
    this.brand = paramString1;
    paramString1 = paramString11;
    if (StripeTextUtils.nullIfBlank(paramString11) == null) {
      paramString1 = getLast4();
    }
    this.last4 = paramString1;
    this.fingerprint = StripeTextUtils.nullIfBlank(paramString12);
    this.funding = StripeTextUtils.asFundingType(paramString13);
    this.country = StripeTextUtils.nullIfBlank(paramString14);
    this.currency = StripeTextUtils.nullIfBlank(paramString15);
  }
  
  private boolean isValidLuhnNumber(String paramString)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    int i = 1;
    int j = 0;
    int k = paramString.length() - 1;
    if (k >= 0)
    {
      c = paramString.charAt(k);
      if (!Character.isDigit(c)) {
        bool1 = bool2;
      }
    }
    while (j % 10 == 0)
    {
      char c;
      return bool1;
      int m = Integer.parseInt("" + c);
      if (i == 0) {}
      for (i = 1;; i = 0)
      {
        int n = m;
        if (i != 0) {
          n = m * 2;
        }
        m = n;
        if (n > 9) {
          m = n - 9;
        }
        j += m;
        k--;
        break;
      }
    }
    for (;;)
    {
      bool1 = false;
    }
  }
  
  private String normalizeCardNumber(String paramString)
  {
    if (paramString == null) {}
    for (paramString = null;; paramString = paramString.trim().replaceAll("\\s+|-", "")) {
      return paramString;
    }
  }
  
  public String getAddressCity()
  {
    return this.addressCity;
  }
  
  public String getAddressCountry()
  {
    return this.addressCountry;
  }
  
  public String getAddressLine1()
  {
    return this.addressLine1;
  }
  
  public String getAddressLine2()
  {
    return this.addressLine2;
  }
  
  public String getAddressState()
  {
    return this.addressState;
  }
  
  public String getAddressZip()
  {
    return this.addressZip;
  }
  
  public String getBrand()
  {
    String str;
    if ((StripeTextUtils.isBlank(this.brand)) && (!StripeTextUtils.isBlank(this.number)))
    {
      if (!StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_AMERICAN_EXPRESS)) {
        break label47;
      }
      str = "American Express";
    }
    for (;;)
    {
      this.brand = str;
      return this.brand;
      label47:
      if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DISCOVER)) {
        str = "Discover";
      } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_JCB)) {
        str = "JCB";
      } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DINERS_CLUB)) {
        str = "Diners Club";
      } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_VISA)) {
        str = "Visa";
      } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_MASTERCARD)) {
        str = "MasterCard";
      } else {
        str = "Unknown";
      }
    }
  }
  
  public String getCVC()
  {
    return this.cvc;
  }
  
  public String getCurrency()
  {
    return this.currency;
  }
  
  public Integer getExpMonth()
  {
    return this.expMonth;
  }
  
  public Integer getExpYear()
  {
    return this.expYear;
  }
  
  public String getLast4()
  {
    String str;
    if (!StripeTextUtils.isBlank(this.last4)) {
      str = this.last4;
    }
    for (;;)
    {
      return str;
      if ((this.number != null) && (this.number.length() > 4))
      {
        this.last4 = this.number.substring(this.number.length() - 4, this.number.length());
        str = this.last4;
      }
      else
      {
        str = null;
      }
    }
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getNumber()
  {
    return this.number;
  }
  
  @Deprecated
  public String getType()
  {
    return getBrand();
  }
  
  public boolean validateCVC()
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if (StripeTextUtils.isBlank(this.cvc))
    {
      bool1 = bool2;
      return bool1;
    }
    String str1 = this.cvc.trim();
    String str2 = getBrand();
    int i;
    if (((str2 == null) && (str1.length() >= 3) && (str1.length() <= 4)) || (("American Express".equals(str2)) && (str1.length() == 4)) || (str1.length() == 3))
    {
      i = 1;
      label83:
      if ((!StripeTextUtils.isWholePositiveNumber(str1)) || (i == 0)) {
        break label104;
      }
    }
    for (;;)
    {
      break;
      i = 0;
      break label83;
      label104:
      bool1 = false;
    }
  }
  
  public boolean validateExpMonth()
  {
    boolean bool = true;
    if ((this.expMonth != null) && (this.expMonth.intValue() >= 1) && (this.expMonth.intValue() <= 12)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public boolean validateExpYear()
  {
    if ((this.expYear != null) && (!DateUtils.hasYearPassed(this.expYear.intValue()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean validateExpiryDate()
  {
    boolean bool1 = false;
    boolean bool2;
    if (!validateExpMonth()) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (validateExpYear())
      {
        bool2 = bool1;
        if (!DateUtils.hasMonthPassed(this.expYear.intValue(), this.expMonth.intValue())) {
          bool2 = true;
        }
      }
    }
  }
  
  public boolean validateNumber()
  {
    boolean bool = true;
    if (StripeTextUtils.isBlank(this.number)) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      String str1 = this.number.trim().replaceAll("\\s+|-", "");
      if ((StripeTextUtils.isBlank(str1)) || (!StripeTextUtils.isWholePositiveNumber(str1)) || (!isValidLuhnNumber(str1)))
      {
        bool = false;
      }
      else
      {
        String str2 = getBrand();
        if ("American Express".equals(str2))
        {
          if (str1.length() != 15) {
            bool = false;
          }
        }
        else if ("Diners Club".equals(str2))
        {
          if (str1.length() != 14) {
            bool = false;
          }
        }
        else if (str1.length() != 16) {
          bool = false;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/model/Card.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */