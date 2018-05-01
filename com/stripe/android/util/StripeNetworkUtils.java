package com.stripe.android.util;

import com.stripe.android.model.Card;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class StripeNetworkUtils
{
  public static Map<String, Object> hashMapFromCard(Card paramCard)
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    localHashMap2.put("number", StripeTextUtils.nullIfBlank(paramCard.getNumber()));
    localHashMap2.put("cvc", StripeTextUtils.nullIfBlank(paramCard.getCVC()));
    localHashMap2.put("exp_month", paramCard.getExpMonth());
    localHashMap2.put("exp_year", paramCard.getExpYear());
    localHashMap2.put("name", StripeTextUtils.nullIfBlank(paramCard.getName()));
    localHashMap2.put("currency", StripeTextUtils.nullIfBlank(paramCard.getCurrency()));
    localHashMap2.put("address_line1", StripeTextUtils.nullIfBlank(paramCard.getAddressLine1()));
    localHashMap2.put("address_line2", StripeTextUtils.nullIfBlank(paramCard.getAddressLine2()));
    localHashMap2.put("address_city", StripeTextUtils.nullIfBlank(paramCard.getAddressCity()));
    localHashMap2.put("address_zip", StripeTextUtils.nullIfBlank(paramCard.getAddressZip()));
    localHashMap2.put("address_state", StripeTextUtils.nullIfBlank(paramCard.getAddressState()));
    localHashMap2.put("address_country", StripeTextUtils.nullIfBlank(paramCard.getAddressCountry()));
    paramCard = new HashSet(localHashMap2.keySet()).iterator();
    while (paramCard.hasNext())
    {
      String str = (String)paramCard.next();
      if (localHashMap2.get(str) == null) {
        localHashMap2.remove(str);
      }
    }
    localHashMap1.put("card", localHashMap2);
    return localHashMap1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/util/StripeNetworkUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */