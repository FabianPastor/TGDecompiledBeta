package com.stripe.android.net;

import com.stripe.android.model.Card;
import com.stripe.android.util.StripeJsonUtils;
import com.stripe.android.util.StripeTextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CardParser
{
  public static Card parseCard(JSONObject paramJSONObject)
    throws JSONException
  {
    return new Card(null, Integer.valueOf(paramJSONObject.getInt("exp_month")), Integer.valueOf(paramJSONObject.getInt("exp_year")), null, StripeJsonUtils.optString(paramJSONObject, "name"), StripeJsonUtils.optString(paramJSONObject, "address_line1"), StripeJsonUtils.optString(paramJSONObject, "address_line2"), StripeJsonUtils.optString(paramJSONObject, "address_city"), StripeJsonUtils.optString(paramJSONObject, "address_state"), StripeJsonUtils.optString(paramJSONObject, "address_zip"), StripeJsonUtils.optString(paramJSONObject, "address_country"), StripeTextUtils.asCardBrand(StripeJsonUtils.optString(paramJSONObject, "brand")), StripeJsonUtils.optString(paramJSONObject, "last4"), StripeJsonUtils.optString(paramJSONObject, "fingerprint"), StripeTextUtils.asFundingType(StripeJsonUtils.optString(paramJSONObject, "funding")), StripeJsonUtils.optString(paramJSONObject, "country"), StripeJsonUtils.optString(paramJSONObject, "currency"));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/CardParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */