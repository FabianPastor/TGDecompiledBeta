package com.stripe.android.util;

public class StripeTextUtils
{
  public static String asCardBrand(String paramString)
  {
    if (isBlank(paramString)) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      if ("American Express".equalsIgnoreCase(paramString)) {
        paramString = "American Express";
      } else if ("MasterCard".equalsIgnoreCase(paramString)) {
        paramString = "MasterCard";
      } else if ("Diners Club".equalsIgnoreCase(paramString)) {
        paramString = "Diners Club";
      } else if ("Discover".equalsIgnoreCase(paramString)) {
        paramString = "Discover";
      } else if ("JCB".equalsIgnoreCase(paramString)) {
        paramString = "JCB";
      } else if ("Visa".equalsIgnoreCase(paramString)) {
        paramString = "Visa";
      } else {
        paramString = "Unknown";
      }
    }
  }
  
  public static String asFundingType(String paramString)
  {
    if (isBlank(paramString)) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      if ("credit".equalsIgnoreCase(paramString)) {
        paramString = "credit";
      } else if ("debit".equalsIgnoreCase(paramString)) {
        paramString = "debit";
      } else if ("prepaid".equalsIgnoreCase(paramString)) {
        paramString = "prepaid";
      } else {
        paramString = "unknown";
      }
    }
  }
  
  public static String asTokenType(String paramString)
  {
    if ("card".equals(paramString)) {}
    for (paramString = "card";; paramString = null) {
      return paramString;
    }
  }
  
  public static boolean hasAnyPrefix(String paramString, String... paramVarArgs)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramString == null)
    {
      bool2 = bool1;
      return bool2;
    }
    int i = paramVarArgs.length;
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j >= i) {
        break;
      }
      if (paramString.startsWith(paramVarArgs[j]))
      {
        bool2 = true;
        break;
      }
    }
  }
  
  public static boolean isBlank(String paramString)
  {
    if ((paramString == null) || (paramString.trim().length() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isWholePositiveNumber(String paramString)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramString == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      for (int i = 0;; i++)
      {
        if (i >= paramString.length()) {
          break label39;
        }
        bool2 = bool1;
        if (!Character.isDigit(paramString.charAt(i))) {
          break;
        }
      }
      label39:
      bool2 = true;
    }
  }
  
  public static String nullIfBlank(String paramString)
  {
    String str = paramString;
    if (isBlank(paramString)) {
      str = null;
    }
    return str;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/util/StripeTextUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */